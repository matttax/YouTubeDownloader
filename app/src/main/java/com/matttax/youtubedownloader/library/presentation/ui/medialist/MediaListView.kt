package com.matttax.youtubedownloader.library.presentation.ui.medialist

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.recyclerview.widget.*
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.library.presentation.diff.ListDiff
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class MediaListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.media_list_view, this, true)
    }

    var mediaItemCallback = object : MediaItemCallback {}
    var onDragged: (Int, Int) -> Unit = { _, _ -> }

    private val recyclerView = findViewById<RecyclerView>(R.id.mediaItemList).also {
        it.layoutManager = LinearLayoutManager(context)
        it.addItemDecoration(MarginItemDecoration(5))
    }
    private var adapter: MediaItemAdapter? = null

    fun init(
        listFlow: Flow<List<UiMediaModel>>,
        eventFlow: Flow<ListDiff>,
        currentUriFlow: StateFlow<String?>,
        isPlayingFlow: StateFlow<Boolean>,
        scope: CoroutineScope
    ) {
        with(scope) {
            observeMediaList(listFlow, eventFlow)
            observePlayingState(currentUriFlow, isPlayingFlow)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun CoroutineScope.observeMediaList(
        listFlow: Flow<List<UiMediaModel>>,
        eventFlow: Flow<ListDiff>
    ) {
        listFlow.combine(eventFlow) { list, event ->
            if (recyclerView == null || adapter?.getCurrentItemList() == list) return@combine
            if (adapter == null) {
                recyclerView.adapter = MediaItemAdapter(list, mediaItemCallback).also {
                    adapter = it
                    ItemTouchHelper(
                        DragAndDropCallback(
                            onSwapped = it::onSwap,
                            onDragged = onDragged
                        )
                    ).attachToRecyclerView(recyclerView)
                }
            } else if (event != ListDiff.NoDifference) adapter?.setData(list)
            when (event) {
                is ListDiff.ItemInserted -> adapter?.notifyItemInserted(0)
                is ListDiff.ItemDeleted -> adapter?.notifyItemRemoved(event.position)
                is ListDiff.ItemModified -> adapter?.notifyItemChanged(event.oldListPosition)
                is ListDiff.SignificantDifference -> adapter?.notifyDataSetChanged()
                is ListDiff.NoDifference -> {}
            }
        }.launchIn(this)
    }

    private fun CoroutineScope.observePlayingState(
        currentUriFlow: StateFlow<String?>,
        isPlayingFlow: StateFlow<Boolean>
    ) {
        currentUriFlow.combine(isPlayingFlow) { uri, isPlaying ->
            when {
                uri != null && isPlaying -> PlayingState.Playing(uri)
                uri != null && !isPlaying -> PlayingState.Paused(uri)
                else -> PlayingState.None
            }
        }.onEach { state ->
            adapter?.let {
                it.playingState = state
            }
        }.launchIn(this)
    }
}
