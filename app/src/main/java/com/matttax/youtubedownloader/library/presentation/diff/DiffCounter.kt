package com.matttax.youtubedownloader.library.presentation.diff

class DiffCounter<T, K : Comparable<K>>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val comparator: (T) -> K
) {

    private val newListSorted = newList.toComparableForm()
    private val oldListSorted = oldList.toComparableForm()

    private val oldListUnordered = oldList.sortedBy { comparator(it) }
    private val newListUnordered = newList.sortedBy { comparator(it) }

    fun countListDiff(): ListDiff {
        if (oldListUnordered == newListUnordered)
            return ListDiff.NoDifference
        checkItemModified()?.let { return ListDiff.ItemModified(it.first, it.second) } ?:
        checkItemDeletedPosition()?.let { return ListDiff.ItemDeleted(it) } ?:
        return if (checkItemInserted()) ListDiff.ItemInserted else ListDiff.SignificantDifference
    }

    private fun checkItemInserted(): Boolean {
        if (newList.size != oldList.size + 1)
            return false
        return oldList.toComparableForm() == newList.drop(1).toComparableForm()
    }

    private fun checkItemDeletedPosition(): Int? {
        if (newList.size != oldList.size - 1)
            return null
        val diff = oldListSorted - newListSorted
        return if (diff.size == 1) {
            oldList.indexOfFirst { comparator(it) == diff.first() }.takeIf { it != -1 }
        } else null
    }

    private fun checkItemModified(): Pair<Int, Int>? {
        if (newList.size != oldList.size)
            return null
        var oldItem: T? = null
        var modifiedItem: T? = null
        oldListUnordered.zip(newListUnordered).forEach { (old, new) ->
            if (old != new) {
                if (oldItem == null) {
                    oldItem = old
                    modifiedItem = new
                } else return null
            }
        }
        return oldItem?.let { old ->
            modifiedItem?.let { new ->
                oldList.indexOfFirst { it == old } to newList.indexOfFirst { it == new }
            }
        }
    }

    private fun List<T>.toComparableForm(): Set<K> {
        return map { comparator(it) }.toSet()
    }
}
