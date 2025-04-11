package com.example.sqlitetest.ui.main

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: mybookInfo)

    @Update
    suspend fun update(book: mybookInfo)

    @Delete
    suspend fun delete(book: mybookInfo)


    @Query("SELECT * FROM Book")
    fun getAllBooks(): Flow<List<mybookInfo>>

    @Query("SELECT * FROM Book WHERE id = :bookId")
    suspend fun getBookById(bookId: Long): mybookInfo?

    @Query("Delete FROM Book Where id = :bookId")
    suspend fun deleteBookById(bookId: Long)


}