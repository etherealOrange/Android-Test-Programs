package com.example.sqlitetest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import com.example.sqlitetest.databinding.ActivityMainBinding
import com.example.sqlitetest.ui.main.MainFragment
import com.example.sqlitetest.ui.main.MainViewModel

class MainActivity : AppCompatActivity() {

    private var _mainActivityBinding: ActivityMainBinding? = null
    private val mainActivityBinding get() = _mainActivityBinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(mainActivityBinding.container.id, MainFragment.newInstance())
                .commitNow()
        }


    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}