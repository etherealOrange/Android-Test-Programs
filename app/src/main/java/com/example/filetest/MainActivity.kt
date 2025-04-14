package com.example.filetest


import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.filetest.databinding.ActivityMainBinding
import com.example.filetest.model.BookChapterAdapter
import com.example.filetest.ui.main.ExtendAppCompatActivity
import kotlin.getValue

class MainActivity : ExtendAppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 模拟书籍数据（实际应从数据库/网络加载）
        val bookChapterAdapter = BookChapterAdapter()

        viewModel.filePagingFlow.launchLifeScopeCollect { bookChapterAdapter.submitData(it) }

        // 设置ViewPager2
//        bookPagerAdapter = BookPagerAdapter(this, bookPages)
//        binding.viewpager.adapter = bookChapterAdapter
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

}



