package com.matttax.youtubedownloader

import com.github.kiulian.downloader.YoutubeDownloader
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.youtube.search.VideoSearcherImpl
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun checkResults() {
        val searcher = VideoSearcherImpl(YoutubeDownloader())
        searcher.search("маргинал хрюсы", SearchConfig())
        println("___")
        searcher.loadMore()
        println("___")
        searcher.loadMore()
    }
}