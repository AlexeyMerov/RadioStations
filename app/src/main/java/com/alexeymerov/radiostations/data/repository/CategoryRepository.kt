package com.alexeymerov.radiostations.data.repository

interface CategoryRepository {

    suspend fun loadCategories()

}