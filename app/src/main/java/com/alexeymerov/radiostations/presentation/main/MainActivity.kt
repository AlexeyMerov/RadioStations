package com.alexeymerov.radiostations.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.alexeymerov.radiostations.common.collectWhenResumed
import com.alexeymerov.radiostations.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() {
        binding.testButton.setOnClickListener {
            viewModel.loadCategories()
        }
    }

    private fun initViewModel() = with(viewModel) {
        categories.collectWhenResumed(this@MainActivity) {
            binding.testText.text = it.toString()
        }
    }
}