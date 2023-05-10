package com.alexeymerov.radiostations.common

import android.content.res.Resources
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * Simplified Adapter for recycler to make code more compact and pretty.
 *
 * DiffUtils included.
 * */
abstract class AsyncAutoUpdatableAdapter<T : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected abstract val differ: AsyncListDiffer<T>

    protected val diffCallback = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = compareItems(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = compareContent(oldItem, newItem)
        override fun getChangePayload(oldItem: T, newItem: T) = compareContentForPayload(oldItem, newItem)
    }

    fun submitList(newList: List<T>) = differ.submitList(newList)

    override fun getItemCount() = differ.currentList.size

    protected fun getListItem(position: Int): T = differ.currentList.elementAt(position)

    protected abstract fun compareItems(old: T, new: T): Boolean

    protected abstract fun compareContent(old: T, new: T): Boolean

    protected abstract fun compareContentForPayload(old: T, new: T): Any?

}

/**
 * For some reason server returns http links.
 * */
fun String.httpsEverywhere() = replace("http:", "https:")

fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

val String.Companion.EMPTY: String
    get() = ""