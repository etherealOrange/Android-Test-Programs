package com.example.coiltest

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        createNotificationChannel()
        bind.NotifyBTN.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(
                    this,arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
            showSimpleNotification()

        }



    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "default_channel_id", // 渠道ID，必须唯一
            "默认通知",            // 用户可见的渠道名称
            NotificationManager.IMPORTANCE_HIGH // 重要性级别
        ).apply {
            description = "这是默认通知渠道的描述" // 渠道描述
        }

        // 获取NotificationManager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // 创建渠道
        notificationManager.createNotificationChannel(channel)
    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showSimpleNotification() {
        // 1. 创建通知构建器
        val builder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_background) // 必须设置的小图标
            .setContentTitle("我的通知标题")         // 通知标题
            .setContentText("这是通知的内容文本")     // 通知内容
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 优先级

        // 2. 获取NotificationManager
        val notificationManager = NotificationManagerCompat.from(this)

        // 3. 发送通知
        // 第一个参数是通知ID，必须是唯一的，用于后续更新或取消通知
        notificationManager.notify(1, builder.build())
    }


}