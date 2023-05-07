package com.alexeymerov.radiostations.presentation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alexeymerov.radiostations.common.AsyncAutoUpdatableAdapter

abstract class BaseRecyclerAdapter<T : Any, VH : BaseViewHolder<T>> : AsyncAutoUpdatableAdapter<T, VH>() {

	override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getListItem(position))

	override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) = when {
		payloads.isEmpty() -> onBindViewHolder(holder, position)
		else -> proceedPayloads(payloads, holder, position)
	}

	abstract fun proceedPayloads(payloads: MutableList<Any>, holder: VH, position: Int)
}

abstract class BaseViewHolder<in T : Any>(containerView: View) : RecyclerView.ViewHolder(containerView) {

	abstract fun bind(currentItem: T)
}
