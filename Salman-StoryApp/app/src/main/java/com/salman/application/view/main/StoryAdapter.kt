package com.salman.application.view.main

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.salman.application.data.api.response.ListStoryItem
import com.salman.application.databinding.WidgetListStoryBinding
import com.salman.application.view.detail.DetailActivity

class StoryAdapter :

    PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            WidgetListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            if (story != null) {
                intentDetail.putExtra("storyId", story.id)
                intentDetail.putExtra("storyPhotoUrl", story.photoUrl)
            }

            val photoPair = android.util.Pair.create(holder.binding.ivItemPhoto as View, "photo")
            val namePair = android.util.Pair.create(holder.binding.tvItemName as View, "name")
            val descriptionPair =
                android.util.Pair.create(holder.binding.storyDescription as View, "description")

            val options = ActivityOptions.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                photoPair,
                namePair,
                descriptionPair
            ).toBundle()

            holder.itemView.context.startActivity(intentDetail, options)
        }
    }

    class StoryViewHolder(val binding: WidgetListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.storyDescription.text = story.description
            binding.ivItemPhoto.load(story.photoUrl)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}