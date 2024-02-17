package com.matttax.youtubedownloader.library.presentation.diff

class DiffCounter<T, K : Comparable<K>>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val comparator: (T) -> K
) {
    fun countListDiff(): ListDiff {
        if (newList.size == oldList.size + 1 && checkItemInserted()) return ListDiff.ItemInserted
        if (newList.size == oldList.size - 1)
            return checkItemDeletedPosition()?.let { ListDiff.ItemDeleted(it) } ?: ListDiff.SignificantDifference
        return ListDiff.SignificantDifference
    }

    private fun checkItemInserted(): Boolean {
        return oldList.toComparableForm() == newList.drop(1).toComparableForm()
    }

    private fun checkItemDeletedPosition(): Int? {
        val newListSorted = newList.toComparableForm()
        val oldListSorted = oldList.toComparableForm()
        val diff = oldListSorted - newListSorted
        return if (diff.size == 1) {
            val index = oldList.indexOfFirst { comparator(it) == diff.first() }
            if (index == -1) null else index
        } else null
    }

    private fun List<T>.toComparableForm(): Set<K> {
        return map { comparator(it) }.toSet()
    }
}
