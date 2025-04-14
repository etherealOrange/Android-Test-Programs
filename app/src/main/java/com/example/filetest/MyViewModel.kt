package com.example.filetest

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.filetest.model.BookChapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn

class MyViewModel: ViewModel() {

    val myWith: MutableStateFlow<Int> = MutableStateFlow(0)
    val myHeight: MutableStateFlow<Int> = MutableStateFlow(0)
    //检测当前的scroll位置TODO:这里的scroll是共用的，实际应该是在pause时把scroll位置保存到数据库中
    val scroll2: MutableStateFlow<Int> = MutableStateFlow(0)

    //TODO: 这里的文件路径需要根据实际情况进行修改
    val currentFile: MutableStateFlow<Uri?> = MutableStateFlow(null)
    val contentResolver: MutableStateFlow<ContentResolver?> = MutableStateFlow(null)
    fun handleFileSelection(uri: Uri, resolver: ContentResolver) {
        contentResolver.value = resolver
        currentFile.value = uri
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val filePagingFlow = currentFile.filterNotNull()
        .combine(contentResolver.filterNotNull()) {file,resolver ->
            Pager(
                config = PagingConfig(
                    pageSize = 1,
                    maxSize = 10,
                    prefetchDistance = 1,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { FilePagingSource(resolver,file)}
            ).flow
        }
        .flatMapLatest { it }
        .cachedIn(viewModelScope)



}