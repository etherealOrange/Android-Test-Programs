package com.example.sqlitetest.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@Entity(tableName = "Book")
data class mybookInfo(
    @PrimaryKey(autoGenerate  = true) val id: Long=0,
    @ColumnInfo(name = "bookname") val title: String,
    @ColumnInfo val author: String,
    @ColumnInfo val pages: Int
)


@OptIn(FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val bookDao = db.bookDao()
    private  var _allBooks = MutableStateFlow<List<mybookInfo>>(emptyList())
    val allBooks: StateFlow<List<mybookInfo>> get() = _allBooks

    private var _selectedBook = MutableStateFlow<mybookInfo?>(null)
    val selectedBook: StateFlow<mybookInfo?> get() = _selectedBook

    init {
        viewModelScope.launch {
            bookDao.getAllBooks()
                .debounce (200) //防抖200
                .distinctUntilChanged() //去除查询带来的数据库变化
                .collect {
                _allBooks.value=it
            }
        }

    }
    fun insertBook(book: mybookInfo) {
        viewModelScope.launch {
            bookDao.insert(book)
        }
    }

    fun selectBook(bookId: Long) {
        viewModelScope.launch {
            _selectedBook.value = allBooks.value.firstOrNull(){
                it.id == bookId
            }
        }

    }
    fun deleteBookById(bookId: Long) {
        viewModelScope.launch {
            bookDao.deleteBookById(bookId)
        }
    }
    fun deleteBook(book: mybookInfo) {
        viewModelScope.launch {
            bookDao.delete(book)
        }
    }
}