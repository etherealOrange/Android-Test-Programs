package com.example.filetest.model

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.filetest.databinding.FragmentPageBinding

class BookChapterAdapter() : PagingDataAdapter<BookChapter, BookChapterAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookChapter>() {
            override fun areItemsTheSame(oldItem: BookChapter, newItem: BookChapter): Boolean {
                return oldItem.title == newItem.title
            }
            override fun areContentsTheSame(oldItem: BookChapter, newItem: BookChapter): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class ViewHolder(val binding: FragmentPageBinding):
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = FragmentPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        getItem(position)?.let {
            holder.binding.tvChapterName.text = it.title
            holder.binding.tvContent.text = it.content
        }

    }



}