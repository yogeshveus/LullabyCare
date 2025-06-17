package com.example.babymonitorapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.babymonitorapp.PlaylistItemDetail
import com.example.babymonitorapp.R
import com.example.babymonitorapp.databinding.ItemPlaylistVideoBinding

class PlaylistAdapter(
    private val onItemClick: (videoId: String) -> Unit
) : ListAdapter<PlaylistItemDetail, PlaylistAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemPlaylistVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoItem = getItem(position)
        holder.bind(videoItem)
    }

    inner class VideoViewHolder(private val binding: ItemPlaylistVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.snippet?.resourceId?.videoId?.let { videoId ->
                        onItemClick(videoId)
                    }
                }
            }
        }

        fun bind(videoItem: PlaylistItemDetail) {
            binding.textVideoTitle.text = videoItem.snippet.title
            binding.textVideoDescription.text = videoItem.snippet.description

            val thumbnailUrl = videoItem.snippet.thumbnails.medium?.url
                ?: videoItem.snippet.thumbnails.default?.url

            if (thumbnailUrl != null) {
                Glide.with(binding.imageVideoThumbnail.context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.baby_svgrepo_com) // Create this drawable
                    .error(R.drawable.baby_svgrepo_com__1_)         // Create this drawable
                    .into(binding.imageVideoThumbnail)
            } else {
                // Set a default image or hide the ImageView if no thumbnail is available
                binding.imageVideoThumbnail.setImageResource(R.drawable.baby_svgrepo_com__1_)
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<PlaylistItemDetail>() {
        override fun areItemsTheSame(oldItem: PlaylistItemDetail, newItem: PlaylistItemDetail): Boolean {
            return oldItem.snippet.resourceId.videoId == newItem.snippet.resourceId.videoId
        }

        override fun areContentsTheSame(oldItem: PlaylistItemDetail, newItem: PlaylistItemDetail): Boolean {
            return oldItem == newItem // Assumes PlaylistItemDetail is a data class
        }
    }
}