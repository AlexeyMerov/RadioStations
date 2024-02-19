package com.alexeymerov.radiostations.core.test

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback

class TestDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any) = oldItem == newItem
}

class TestUpdateCallback : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}
