package com.example.filetest.model

import android.graphics.text.LineBreaker
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.filetest.MyViewModel
import com.example.filetest.R
import com.example.filetest.databinding.FragmentPageBinding
import com.example.filetest.ui.main.ExtendFragment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

// PageFragment.kt
class PageFragment : ExtendFragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyViewModel by activityViewModels()

    companion object {
        private const val ARG_PAGE_DATA = "page_data"

        fun newInstance(page: BookPage): PageFragment {
            val fragment = PageFragment()
            val args = Bundle().apply {
                putParcelable(ARG_PAGE_DATA, page)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val page = arguments?.getParcelable<BookPage>(ARG_PAGE_DATA)
        page?.let {
            binding.tvChapterName.text = it.chapterName
            binding.tvContent.text = it.content
            binding.tvContent.breakStrategy= LineBreaker.BREAK_STRATEGY_HIGH_QUALITY
        }
        binding.tvContent.doOnLayout {
            viewModel.myWith.value = it.width
            viewModel.myHeight.value = it.height
            Log.d("Page","宽度 ${it.width} 高度 ${it.height} " +
                    "paddingStart ${it.paddingStart} paddingEnd ${it.paddingEnd}\n" +
                    "paddingTop ${it.paddingTop} paddingBottom ${it.paddingBottom}\n"+
                "textsize ${binding.tvContent.textSize} lineHeight${binding.tvContent.lineHeight}\n" +
                    "letterspaceing ${binding.tvContent.letterSpacing} letterspace px " +
                    "${binding.tvContent.letterSpacing*binding.tvContent.textSize}"
                    )

        }
        binding.scrollViewSV.setOnScrollChangeListener {
            _,_,scrollY,_,_ ->
            viewModel.scroll2.value=scrollY
        }
        var i =0
        binding.tvContent.setOnClickListener {
            Log.d("Page","点击了 ${i++}")
        }

        viewModel.scroll2
            .debounce(200)
            .filter { isVisible }
            .launchLifeScopeCollectLatest {
                Log.d("PageFragment","scrollY $it")
            }

    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PageFragment","oncreate 创建了一个实例 $this")
    }

    override fun onDestroy() {
        Log.d("PageFragment","ondestroy 销毁了一个实例 $this")
        super.onDestroy()
    }
}
