package com.alexeymerov.radiostations.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.radiostations.domain.usecase.category.CategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val categoryUseCase: CategoryUseCase) : ViewModel() {

    val categories = categoryUseCase.getCategories()
    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryUseCase.loadCategories()
        }
    }
}

