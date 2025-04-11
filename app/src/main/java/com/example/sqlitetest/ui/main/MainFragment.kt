package com.example.sqlitetest.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.example.sqlitetest.R
import com.example.sqlitetest.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels{
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(requireActivity().application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        var numUpdate=0
        lifecycleScope.launch {
            viewModel.allBooks.collectLatest {
                books ->
                numUpdate++
                val books_Text = "更新次数：${numUpdate}\n"+books.joinToString("\n"){

                    "id:${it.id} title:${it.title}, author:${it.author}, pages:${it.pages}"
                }

                binding.message.text = books_Text



                books.forEach {
                    Log.d("collect","id:${it.id} title:${it.title}, author:${it.author}, pages:${it.pages}")
                }
            }
        }
        lifecycleScope.launch {
            viewModel.selectedBook.collectLatest {
                val selected_books = "id:${it?.id}, title:${it?.title}, author:${it?.author},pages:${it?.pages}"

                binding.selectedTV.text = selected_books


                Log.d("selected--book","id:${it?.id}")
            }
        }

        binding.insertBTN.setOnClickListener {
            lifecycleScope.launch {
                val title = binding.insertTitleET.text.toString()
                val author = binding.insertAuthorET.text.toString()
                val pages = binding.insertPagesET.text.toString().toIntOrNull()

                viewModel.insertBook(mybookInfo(title = title, author = author, pages = pages?:100))
                Log.d("insertBTN_onClick", "title=${title}, author=${author}, pages=${pages}")
            }
        }
        binding.selectedBTN.setOnClickListener {
            lifecycleScope.launch {
                val num = binding.selectedIDET.text.toString().toLongOrNull()
                if (num != null){
                    viewModel.selectBook(num)
                    Log.d("selectedBTN_onClick","id:${num}")
                }else{
                    Log.d("selectedBTN_onClick","id is not a number")
                }
            }
        }
        binding.deleteBTN.setOnClickListener {
            lifecycleScope.launch {
                val num = binding.deleteIDET.text.toString().toLongOrNull()
                if (num != null){
                    viewModel.deleteBookById(num)
                    Log.d("deleteBTN_onClick","id:${num}")
                }else{
                    Log.d("deleteBTN_onClick","id is not a number")
                }
            }
        }


        return binding.root
    }

}