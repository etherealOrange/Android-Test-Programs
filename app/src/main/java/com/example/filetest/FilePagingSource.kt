package com.example.filetest

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.filetest.model.BookChapter

class FilePagingSource(
    private val contentResolver: ContentResolver,
    private val file: Uri  // 要读取的文件
) : PagingSource<Int, BookChapter>() {
    override fun getRefreshKey(state: PagingState<Int, BookChapter>): Int? {
        // 获取最近访问的页码（通常是列表中间的位置）
        return state.anchorPosition?.let { anchorPos ->
            state.closestPageToPosition(anchorPos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPos)?.nextKey?.minus(1)
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookChapter> {
        return try {
            // 确定当前页码（首次加载时key为null）
            val pageNumber = params.key ?: 0// 当前页码(首次加载为null，默认0)
//            val pageSize = 1000 // 每页大小
            val readChapterNum = 10
            val startChapterNum = pageNumber * readChapterNum
            val endChapterNum = (pageNumber + 1) * readChapterNum - 1


            // 计算读取范围（每页100行）
            //TODO:之后改为数据库表中的 章节 开始与结束 行号
//            val startChapterLines = pageNumber * pageSize
//            val endChapterLines = (pageNumber + 1) * pageSize - 1
            Log.d("FPS", "开始读取文件 1 的第 读$startChapterNum 章节 到 读$endChapterNum 章节")

            // 读取文件指定行
            val chapters = readFileChapters(file, startChapterNum, endChapterNum)

            // 构建分页结果
            //这里的章节数不对应行数
            LoadResult.Page(
                data = chapters,
                prevKey = if (pageNumber <= 0 ) null else pageNumber -1 ,
                nextKey = if (chapters.isEmpty()) null else pageNumber + 1
            ).also { Log.d("FPS", "size ${it.data.size} pre ${it.prevKey} next ${it.nextKey}") }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    val chapterRegex = """^第([一二三四五六七八九十\d]+)章\s*(.*)$""".toRegex()
    fun isChapterLine(line: String): Boolean {
        return chapterRegex.matches(line)
    }

    private fun readFileChapters(file: Uri, start: Int, end: Int): List<BookChapter> {
        return mutableListOf<BookChapter>().apply {
            Log.d("FPS","开始读取文件  2 开始从章节$start 读  读到$end 章节")
            val inputStream = contentResolver.openInputStream(file)?: throw Exception("无法打开文件")
                inputStream.bufferedReader().use {
                //现在的行数
                var currentChapterNum =0
                //每行读取的内容
                var lineContent = String()
                Log.d("FPS","跳过前面部分  开始从章节$start 读  读到$end 章节")
                while (currentChapterNum < start &&  it.readLine().also { lineContent=it } !=null){
                    if(isChapterLine(lineContent)){
                        currentChapterNum++
                    }
                }
                Log.d("FPS","读取到文件 1 的第 ${currentChapterNum-1}章")
                //当前章节
                var currentChapter: BookChapter? = null
                //章节内容汇总
                var summaryContent = StringBuilder()
                while (currentChapterNum <= end && it.readLine().also { lineContent = it }!=null){
                    //填入章节名
                    if(isChapterLine(lineContent) && currentChapter ==null){
                        currentChapter = BookChapter(lineContent,"")
                        currentChapterNum++
                    }
                    //下一章节了, 把上一章节添加到列表中
                    else if(isChapterLine(lineContent) && currentChapter != null){
                        currentChapter = currentChapter.copy(
                            content = summaryContent.toString()
                        )
                        add(currentChapter)
                        currentChapter = BookChapter(lineContent,"")
                        summaryContent.clear()
                        currentChapterNum++
                    }
                    //有内容但是不在章节内
                    //开头的简介部分
                    else if(lineContent.isNotEmpty() && currentChapter == null){
                        //TODO: 这里是简介部分
                    }
                    //有内容也在章节里头, 把内容合并
                    else
                    {
                        summaryContent.append(lineContent+"\n")
                    }
                }
                Log.d("FPS","读取到文件 2 的第 ${currentChapterNum}章\n")
                if(currentChapter!=null && summaryContent.isNotEmpty()) {
                    currentChapter = currentChapter.copy(
                        content = summaryContent.toString()
                    )
                    add(currentChapter)
                    currentChapter = null
                    summaryContent.clear()
                }
            }
        }.also {
            Log.d("FPS","it ${it.hashCode()} size ${it.size}")
            //it.forEach { Log.d("FPS","得到的Chapter  ${it.title} \n ${it.content}") }
        }

/*        file.readLines()
            .subList(start.coerceAtLeast(0), end.coerceAtMost(file.readLines().lastIndex))
            .mapIndexed { index, content ->
//                BookPage(lineNumber = start + index + 1, content)
                BookPage(
                    pageId = start + index + 1,
                    chapterName = "Chapter ${start / 100 + 1}",
                    content = content
                )
            }*/
    }
}
