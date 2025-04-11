package com.example.filetest.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// BookPage.kt
@Parcelize
data class BookPage(
    val pageId: Int,      // 页码
    val chapterName: String,
    val content: String,  // 当前页文本
): Parcelable
