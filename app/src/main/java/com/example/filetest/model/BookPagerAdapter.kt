package com.example.filetest.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// BookPagerAdapter.kt
class BookPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val pages: List<BookPage>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = pages.size
    override fun createFragment(position: Int): Fragment {
        return PageFragment.newInstance(pages[position])
    }
}
