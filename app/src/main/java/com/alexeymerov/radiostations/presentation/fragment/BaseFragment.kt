package com.alexeymerov.radiostations.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.alexeymerov.radiostations.presentation.activity.main.MainActivity
import timber.log.Timber

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected var _binding: T? = null
    protected val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewModel()
    }

    fun setToolbarTitle(text: String) {
        (activity as? MainActivity)?.supportActionBar?.title = text
    }

    abstract fun initViews()

    abstract fun initViewModel()

    protected fun navigateTo(direction: NavDirections) {
        Timber.d("[ ${object {}.javaClass.enclosingMethod?.name} ] $direction")
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}