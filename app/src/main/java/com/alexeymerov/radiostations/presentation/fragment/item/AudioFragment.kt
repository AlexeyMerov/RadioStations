package com.alexeymerov.radiostations.presentation.fragment.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.alexeymerov.radiostations.R
import com.alexeymerov.radiostations.common.collectWhenResumed
import com.alexeymerov.radiostations.common.setOnSingleClick
import com.alexeymerov.radiostations.databinding.FragmentAudioBinding
import com.alexeymerov.radiostations.presentation.fragment.BaseFragment
import com.alexeymerov.radiostations.presentation.fragment.item.AudioViewModel.ViewAction
import com.alexeymerov.radiostations.presentation.fragment.item.AudioViewModel.ViewEffect
import com.alexeymerov.radiostations.presentation.fragment.item.AudioViewModel.ViewState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * At the moment just a garbage.
 * Planned to use as a player screen but have no time and not sure i want separate screen but not some popup player.
 * */
@AndroidEntryPoint
class AudioFragment : BaseFragment<FragmentAudioBinding>() {

    @Inject
    lateinit var requestOptions: RequestOptions

    private val args: AudioFragmentArgs by navArgs()
    private val viewModel: AudioViewModel by viewModels()

    private lateinit var player: ExoPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initViews() {
        setToolbarTitle(args.stationName)
        binding.progressBar.isVisible = true
        player = ExoPlayer.Builder(requireContext()).build()
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(args.stationImgUrl).into(binding.imageView)
        binding.playerControls.setOnSingleClick {
            viewModel.setAction(ViewAction.ToggleAudio)
        }
    }

    override fun initViewModel() = with(viewModel) {
        viewEffect.collectWhenResumed(viewLifecycleOwner, ::processViewEffects)
        viewState.collectWhenResumed(viewLifecycleOwner, ::processViewState)
        viewModel.setAction(ViewAction.LoadAudio(args.rawUrl))
    }

    private fun processPlayerState(state: AudioViewModel.PlayerState) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${state.javaClass.simpleName}")
        when (state) {
            AudioViewModel.PlayerState.Play -> processAudioPlay()
            AudioViewModel.PlayerState.Stop -> processAudioStop()
        }
    }

    private fun processAudioPlay() {
        binding.playerControls.setImageResource(R.drawable.stop_square)
        player.prepare()
        player.play()
    }

    private fun processAudioStop() {
        binding.playerControls.setImageResource(R.drawable.play_arrow)
        player.stop()
    }

    private fun processViewState(state: ViewState) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${state.javaClass.simpleName}")
        when (state) {
            is ViewState.ReadyToPlay -> processReadyToPlay(state)
            ViewState.Error -> binding.nothingAvailableTv.isVisible = true
            ViewState.Loading -> { /*stub*/ // i prefer to handle loader state in the View and not ViewModel
            }
        }
        binding.progressBar.isVisible = state == ViewState.Loading
    }

    private fun processReadyToPlay(state: ViewState.ReadyToPlay) {
        preparePlayer(state.url)
        viewModel.playerState.collectWhenResumed(viewLifecycleOwner, ::processPlayerState)
    }

    private fun processViewEffects(effect: ViewEffect) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ${effect.javaClass.simpleName}")
        if (effect is ViewEffect.ShowToast) {
            Toast.makeText(requireContext(), effect.text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun preparePlayer(url: String) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] audioLink $url")
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
    }

    override fun onDestroy() {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ]")
        player.stop()
        player.release()
        super.onDestroy()
    }

}