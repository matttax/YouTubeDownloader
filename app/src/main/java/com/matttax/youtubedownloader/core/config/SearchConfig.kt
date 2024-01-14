package com.matttax.youtubedownloader.core.config

data class SearchConfig(
    val allowCorrection: Boolean = false,
    val duration: Duration = Duration.ANY,
    val uploaded: Uploaded = Uploaded.ANY,
    val sortBy: SortedBy = SortedBy.RELEVANCE
)

enum class Duration(val text: String) {
    ANY("Any"),
    SHORT("Short"),
    MIDDLE("Middle"),
    LONG("Long")
}

enum class Uploaded(val text: String) {
    ANY("Any"),
    TODAY("Today"),
    THIS_WEEK("This week"),
    THIS_MONTH("This month"),
    THIS_YEAR("This year")
}

enum class SortedBy(val text: String) {
    RELEVANCE("Relevance"),
    VIEW_COUNT("View count"),
    RATING("Rating"),
    UPLOAD_DATE("Upload date")
}
