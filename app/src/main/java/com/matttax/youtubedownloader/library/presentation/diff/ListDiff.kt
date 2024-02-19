package com.matttax.youtubedownloader.library.presentation.diff

sealed interface ListDiff {
    object SignificantDifference: ListDiff
    object ItemInserted : ListDiff
    object NoDifference : ListDiff
    data class ItemDeleted(val position: Int) : ListDiff
    data class ItemModified(val oldListPosition: Int, val newListPosition: Int) : ListDiff
}
