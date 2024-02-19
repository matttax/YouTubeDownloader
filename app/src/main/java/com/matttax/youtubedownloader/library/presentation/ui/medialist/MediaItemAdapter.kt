package com.matttax.youtubedownloader.library.presentation.ui.medialist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.matttax.swipereveal.ViewBinderHelper
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.core.ui.utils.secondsToDuration
import com.matttax.youtubedownloader.databinding.MediaItemBinding
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import java.util.*

class MediaItemAdapter(
    mediaItems: List<UiMediaModel>,
    private val mediaItemCallback: MediaItemCallback
): ListAdapter<UiMediaModel, MediaItemAdapter.MediaItemViewHolder>(MediaItemDiffCallback) {

    private lateinit var binding: MediaItemBinding

    private val viewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    private var _mediaItems = mediaItems.toMutableList()

    var playingState: PlayingState = PlayingState.None
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun onSwap(i: Int, j: Int) {
        Collections.swap(_mediaItems, i, j)
        notifyItemMoved(i, j)
    }

    fun setData(list: List<UiMediaModel>) {
        _mediaItems = list.toMutableList()
    }

    fun getCurrentItemList() = _mediaItems.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemViewHolder {
        binding = MediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) {
        holder.onBind(_mediaItems[position])
    }

    override fun getItemCount() = _mediaItems.size

    object MediaItemDiffCallback : DiffUtil.ItemCallback<UiMediaModel>() {
        override fun areItemsTheSame(oldItem: UiMediaModel, newItem: UiMediaModel): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: UiMediaModel, newItem: UiMediaModel): Boolean {
            return oldItem == newItem
        }
    }

    inner class MediaItemViewHolder(
        private val binding: MediaItemBinding
        ): RecyclerView.ViewHolder(binding.root) {

        fun onBind(
            uiMediaModel: UiMediaModel
        ) {
            viewBinderHelper.bind(binding.swipeRevealLayout, uiMediaModel.id)
            viewBinderHelper.closeLayout(uiMediaModel.id)
            Glide.with(itemView.context)
                .load(uiMediaModel.thumbnailUri)
                .transform(
                    if ((playingState as? PlayingState.Playing)?.uri == uiMediaModel.id)
                        selectedTransformation
                    else unselectedTransformation
                ).into(binding.thumbnail)
            setPlayingState(playingState, uiMediaModel.id)
            binding.apply {
                title.text = uiMediaModel.name
                author.text = uiMediaModel.author
                duration.text = uiMediaModel.duration.secondsToDuration()
                swipeRevealLayout.setLockDrag(playingState != PlayingState.None)
                mediaItem.setOnClickListener { mediaItemCallback.onClick(uiMediaModel.id, layoutPosition) }
                editButton.setListener(mediaItemCallback::onEditClick)
                moveButton.setListener(mediaItemCallback::onMoveClick)
                deleteButton.setListener(mediaItemCallback::onDeleteClick)
            }
        }

        private fun ImageButton.setListener(action: (Int) -> Unit) {
            setOnClickListener {
                binding.swipeRevealLayout.close(true)
                action(layoutPosition)
            }
        }

        private fun setPlayingState(state: PlayingState, currentId: String) {
            when (state) {
                is PlayingState.Playing -> {
                    binding.playing.isVisible = (state.uri == currentId).also {
                        if (it){
                            binding.playing.startAnimation(playingAnimation)
                        } else binding.playing.clearAnimation()
                    }
                    binding.paused.isVisible = false
                }
                is PlayingState.Paused -> binding.apply {
                    playing.clearAnimation()
                    playing.isVisible = false
                    paused.isVisible = state.uri == currentId
                }
                is PlayingState.None -> binding.apply {
                    playing.clearAnimation()
                    playing.isVisible = false
                    paused.isVisible = false
                }
            }
        }
    }

    companion object {

        private val playingAnimation = ScaleAnimation(
            1f, 0.7f, 1f, 0.7f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 700
            interpolator = AccelerateInterpolator()
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }

        private val selectedTransformation = MultiTransformation(
            CropTransformation(0, 130),
            RoundedCorners(35),
            BlurTransformation(15)
        )

        private val unselectedTransformation = MultiTransformation(
            CropTransformation(0, 130),
            RoundedCorners(35)
        )
    }
}
