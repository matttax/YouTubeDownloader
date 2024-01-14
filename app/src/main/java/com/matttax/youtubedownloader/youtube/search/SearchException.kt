package com.matttax.youtubedownloader.youtube.search

sealed class SearchException: RuntimeException() {
    class NoRequestException(override val message: String?) : SearchException()
    class NoContinuationException(override val message: String?) : SearchException()
    class SearchFailedException : SearchException()
}
