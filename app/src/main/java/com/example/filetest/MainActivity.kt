package com.example.filetest


import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.example.filetest.databinding.ActivityMainBinding
import com.example.filetest.model.BookChapter
import com.example.filetest.model.BookChapterAdapter
import com.example.filetest.model.BookPage
import com.example.filetest.model.BookPagerAdapter
import com.example.filetest.model.fullBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var bookPagerAdapter: BookPagerAdapter
    private val viewModel: MyViewModel by viewModels()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            Log.d("MainActivity", "Selected file URI: $uri")
            viewModel.handleFileSelection(uri,contentResolver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 模拟书籍数据（实际应从数据库/网络加载）
        val bookPages = generateBookPages()
        val bookChapterAdapter = BookChapterAdapter()
        //TODO:需要解决 Cannot call this method while RecyclerView is computing a layout or scrolling 问题
        lifecycleScope.launch {
            viewModel.filePagingFlow
                .collect {
                    bookChapterAdapter.submitData(it)
                }
        }
        // 设置ViewPager2
//        bookPagerAdapter = BookPagerAdapter(this, bookPages)
//        binding.viewpager.adapter = bookPagerAdapter

//        binding.viewpager.adapter = bookChapterAdapter
        // 显示阅读进度
//        updateProgress(0)

//        binding.viewpager.offscreenPageLimit = 1
        // 页面切换监听
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL,false)
            this.adapter = bookChapterAdapter
            PagerSnapHelper().attachToRecyclerView(this)
        }




        binding.importBTN.setOnClickListener {
            filePickerLauncher.launch(arrayOf(
                "text/plain","application/pdf","application/epub+zip"
            ))
        }
    }
//    private fun updateProgress(currentPosition: Int) {
//        binding.progressBar.progress =
//            ((currentPosition + 1) * 100 / bookPagerAdapter.itemCount)

    private fun generateBookPages(): List<BookPage> {
        // 这里应该是从数据库或文件解析的真实数据
        val book1 = fullBook(
            content = "第一章\n" +
                    "${" \t内容\t".repeat(100)}\n" +
                    "${"\t内容 ".repeat(100)}\n" +
                    "${"内容".repeat(100)}\n" +
                    "${"内容".repeat(400)}\n" +
                    "${"内容".repeat(422)}\n" +
                    "第二章\n" +
                    "${"内容".repeat(1200)}\n" +
                    "第三章\n" +
                    "${"内容".repeat(1554)}\n"
        )
        val chapterRegex = """^第([一二三四五六七八九十\d]+)章\s*(.*)$""".toRegex()
        fun isChapterLine(line: String): Boolean {
            return chapterRegex.matches(line)
        }
        val chapter: MutableList<BookChapter> =mutableListOf()

        fun splitChapters(text: String){
            val lines = text.lines()
            var currentChapter: BookChapter? = null
            lines.forEach { line ->
                if (isChapterLine(line)) {
                    currentChapter?.let { chapter.add(it) }
                    currentChapter = BookChapter(title = line, content = "")
                } else {
                    currentChapter = currentChapter?.copy(
                        content = currentChapter.content + line + "\n"
                    )
                }
            }
            currentChapter?.let { chapter.add(it) }
        }
        val fix = book1.content.trimIndent()
        splitChapters(fix)
        chapter.forEach { Log.d("Main","章节 ${it.title}\n内容 ${it.content}" ) }

        lifecycleScope.launch {
            viewModel.myHeight.collectLatest {  }
            viewModel.myWith.collectLatest {  }
        }








        return listOf(
            BookPage(1, "第一章", "这是第一章的内容..."),
            BookPage(2, "第一章", "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "第一章继续...第一章继续...第一章继续...第一章继续..." +
                    "123${"asdasd\n".repeat(50) }}"),
            BookPage(2, "第一章", "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n" +
                    "第一章继续...第一章继续...第一章继续...第一章继续...\n"),
            BookPage(4, "第二章", "第二章继续...", ),
            BookPage(5, "第三章", "第三章开始...", ),
            BookPage(6, "第三章", "第三章继续...", ),
            BookPage(7, "第三章", "第三章结束...", ),
            BookPage(8, "第四章", "第四章开始...", ),
            BookPage(9, "第四章", "第四章开始...", ),
            BookPage(10, "第四章", "第四章开始...", ),
            BookPage(11, "第四章", "第四章开始...", ),

        )
    }
//    private fun saveCurrentPosition(position: Int) {
//        // 实际应保存到SharedPreferences/数据库
//        getSharedPreferences("book_progress", MODE_PRIVATE)
//            .edit()
//            .putInt("last_page", position)
//            .apply()
//    }

}


