package com.matttax.youtubedownloader.library.presentation.ui.medialist

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragAndDropCallback(
    private val onSwapped: (previous: Int, current: Int) -> Unit,
    private val onDragged: (from: Int, to: Int) -> Unit,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
) {

    private var dragStartPosition: Int? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (dragStartPosition == null) dragStartPosition = viewHolder.bindingAdapterPosition
        (recyclerView.adapter as? MediaItemAdapter)?.onSwap(
            viewHolder.bindingAdapterPosition,
            target.bindingAdapterPosition
        )
        return true
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        viewHolder.itemView.alpha = 1f
        dragStartPosition?.let { onDragged(it, viewHolder.bindingAdapterPosition) }
        dragStartPosition = null
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun isLongPressDragEnabled() = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
}
