package com.alexeymerov.radiostations.data.repository

import com.alexeymerov.radiostations.common.BaseCoroutineScope
import com.alexeymerov.radiostations.data.db.dao.CategoryDao
import com.alexeymerov.radiostations.data.remote.client.radio.RadioClientImpl
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioCommunicator: RadioClientImpl,
    private val categoryDao: CategoryDao
) : CategoryRepository, BaseCoroutineScope() {

    override suspend fun loadCategories() {
        radioCommunicator.loadCategories()
    }
}