package com.example.coiltest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil3.Image
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.imageLoader
import coil3.load
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import coil3.toBitmap
import com.example.coiltest.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var bind : ActivityMainBinding
    private lateinit var imagePath: File


    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia())
    {uri->
        // Handle the image URI
        uri?.let {
//            bind.nomarlImageView.load(
//                uri
//            )
            if(imagePath.exists())imagePath.delete()
            val imageLoader = imageLoader
            val request = ImageRequest.Builder(this)
                .data(uri)
                .size(1080,1920)
                .target{
                    val bitmap = it.toBitmap()
                    imagePath.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                    Log.d("Main","成功 成功 ${imagePath.absolutePath}")
                }
                .build()
            imageLoader.enqueue(request)



//            val inputStream = contentResolver.openInputStream(uri)
//            inputStream?.use {
//                imagePath.outputStream().use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//            Log.d("Main","uri $uri 成功")
            // Do something with the selected image URI
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
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        imagePath = File(this.cacheDir,"image.jpg")


        bind.changeImageButton.setOnClickListener {
            imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        bind.showBTN.setOnClickListener {
            Log.d("Main","dir ${imagePath.absolutePath}")
            bind.nomarlImageView.load(
                imagePath
            )
            {
                memoryCachePolicy(CachePolicy.DISABLED)
            }
            Log.d("Main",imagePath.length().toString())

        }



    }
}