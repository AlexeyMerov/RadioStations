package com.alexeymerov.radiostations.presentation.fragment.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.alexeymerov.radiostations.common.collectWhenResumed
import com.alexeymerov.radiostations.databinding.FragmentAudioBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * At the moment just a garbage.
 * Planned to use as a player screen but have no time and not sure i want separate screen but not some popup player.
 * */
@AndroidEntryPoint
class AudioFragment : Fragment() {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private val args: AudioFragmentArgs by navArgs()
    private val viewModel: AudioViewModel by viewModels()

    private lateinit var player: ExoPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ItemFragment onViewCreated")

        initView()
        initViewModel()
    }

    private fun initView() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
    }

    private fun initViewModel() = with(viewModel) {
        viewState.collectWhenResumed(viewLifecycleOwner) {
            when (it) {
                is AudioViewModel.ViewState.ReadyToPlay -> startPlayer(it.url)
                AudioViewModel.ViewState.Error -> binding.nothingAvailableTv.isVisible = true
                AudioViewModel.ViewState.Loading -> { /*stub*/
                } // move to ViewEffects
            }
            binding.progressBar.isVisible = it == AudioViewModel.ViewState.Loading // better to hide after audio started to play
        }
        loadAudioLink(args.categoryUrl)
    }

    private fun startPlayer(url: String) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] audioLink $url")
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        super.onDestroy()
    }

}