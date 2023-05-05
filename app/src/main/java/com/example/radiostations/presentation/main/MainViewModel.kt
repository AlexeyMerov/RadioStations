package com.example.radiostations.presentation.main

import androidx.lifecycle.ViewModel
import com.example.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {
}

