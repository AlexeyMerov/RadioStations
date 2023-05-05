package com.example.radiostations.data.repository

interface CategoryRepository {

    suspend fun loadCategories()

}