package com.alexeymerov.radiostations.presentation.fragment.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.alexeymerov.radiostations.common.EMPTY
import com.alexeymerov.radiostations.databinding.FragmentItemBinding
import com.alexeymerov.radiostations.presentation.fragment.category.CategoryListFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * At the moment just a garbage.
 * Planned to use as a player screen but have no time and not sure i want separate screen but not some popup player.
 * */
@AndroidEntryPoint
class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] ItemFragment onViewCreated")

        val player = ExoPlayer.Builder(requireContext()).build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(String.EMPTY)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

    }

}