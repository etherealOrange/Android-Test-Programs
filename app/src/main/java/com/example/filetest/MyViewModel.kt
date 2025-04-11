package com.example.filetest

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class MyViewModel: ViewModel() {

    val myWith: MutableStateFlow<Int> = MutableStateFlow(0)
    val myHeight: MutableStateFlow<Int> = MutableStateFlow(0)
    val scroll: MutableStateFlow<HashMap<Int, Int>> = MutableStateFlow(hashMapOf())
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
            Pair(file,resolver)
        }
        .flatMapLatest {(file,resolver) ->
            Pager(
                config = PagingConfig(
                    pageSize = 1,
                    maxSize = 3,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { FilePagingSource(resolver,file)}
            ).flow.cachedIn(viewModelScope)
        }


}