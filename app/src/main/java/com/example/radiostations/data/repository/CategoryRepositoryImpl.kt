package com.example.radiostations.data.repository

import com.example.radiostations.common.BaseCoroutineScope
import com.example.radiostations.data.db.dao.CategoryDao
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val pointsDao: CategoryDao
) : CategoryRepository, BaseCoroutineScope() {

}