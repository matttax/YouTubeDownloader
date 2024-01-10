package com.matttax.youtubedownloader.domain.exceptions

sealed class SearchException: RuntimeException() {
    class NoRequestException(override val message: String?) : SearchException()
    class NoContinuationException(override val message: String?) : SearchException()
    class SearchFailedException : SearchException()
}
