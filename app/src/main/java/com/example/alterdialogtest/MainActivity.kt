package com.example.alterdialogtest

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alterdialogtest.databinding.ActivityMainBinding
import com.example.alterdialogtest.databinding.MaterialAlertDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    //文件选择器
    private lateinit var filePickerLauncher:ActivityResultLauncher<String>
    internal var filePath: String? = null
    private val startPosition = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri ->
            if(uri != null){
                // 文件选择成功，uri是文件的Uri
                handleSelectedFile(uri)
            }
            else{
                Toast.makeText(this,"文件选择失败",Toast.LENGTH_SHORT).show()
            }
        }


        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //弹窗
        binding.alterDialogButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("标题")
                .setMessage("你确定要删除吗？")
                .setPositiveButton("OK") { _, _ ->
                    Toast.makeText(this, "你选择了OK", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "你选择了Cancel", Toast.LENGTH_SHORT).show()
                }
                .create()
            dialog.show()
        }
        //输入文本框
        binding.alterInputDialogButton.setOnClickListener {
            val materialBinding :MaterialAlertDialogBinding
            materialBinding = MaterialAlertDialogBinding.inflate(layoutInflater)

            MaterialAlertDialogBuilder(this)
                .setTitle("标题")
                .setView(materialBinding.root)
                .setPositiveButton("确定"){ _,_ ->
                    Toast.makeText(this,materialBinding.etInput.text.toString(),Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消"){ _,_ ->
                    Toast.makeText(this,"取消",Toast.LENGTH_SHORT).show()
                }.create()
                .show()
        }
        //权限获取和文件选取
        binding.importSomthingButton.setOnClickListener {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
                filePickerLauncher.launch("*/*")

            } else {
                // 打开系统的权限管理页面
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
        //文件内容
        binding.ShowFileContentButton.setOnClickListener {
            if(filePath == null){
                Toast.makeText(this,"文件路径为空",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = RandomAccessFile(filePath,"r")
            val bufferedInputStream = BufferedInputStream(FileInputStream(file.fd),2048)


            bufferedInputStream.use{
                // 移动文件指针到指定位置
                file.seek(startPosition)
                val buffer = ByteArray(2048)
                var bytesRead: Int
                // 循环读取文件内容，直到文件末尾
                bufferedInputStream.read(buffer).also { bytesRead = it }
                Log.d("FileContentPieces",String(buffer, 0, bytesRead, Charset.forName("GBK")))
//                while (bufferedInputStream.read(buffer).also { bytesRead = it } != -1) {
//                    // 将读取的字节转换为字符串并追加到结果中
//                    Log.d("FileContentPieces",String(buffer, 0, bytesRead, Charset.forName("GBK")))
//                }
            }

        }

    }

}

private fun MainActivity.handleSelectedFile(uri: Uri) {
    // 获取文件内容
    val inputStream = contentResolver.openInputStream(uri)
    inputStream?.use { stream ->
        // 读取文件内容
        //bufferedReader把stream转化为了BufferReader
//        val fileContent = stream.bufferedReader(Charset.forName("GBK")).use { it.readText() }
//        Log.d("FileContent", fileContent)
    }
    copyFileToPrivateStorage(uri = uri)

}
private fun MainActivity.copyFileToPrivateStorage(uri: Uri): File? {
    val fileName = getFileNameFromUri(uri) ?: return null
    val file = File(getExternalFilesDir(null), fileName)

    //把inputStream完全复制到到file中 copyTo适合小文件几MB到几十MB
    contentResolver.openInputStream(uri)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    filePath = file.path
//    val file2 = File(file.path)
//    val reader = BufferedReader(InputStreamReader(FileInputStream(file2),"GBK"))
//    val context = reader.use{it.readText()}
//    Log.d("FileContentInApp",file.path)
//    Log.d("FileContentInApp", context)
    return file
}


private fun MainActivity.getFileNameFromUri(uri: Uri): String? {
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor == null)
        Log.d("CursorContent", "cursor is null")
//    logCursorContent(cursor)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex =it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (columnIndex != -1){
                Log.d("CursorContent", "未找到 columnIndex: $columnIndex")
            }
            if(columnIndex>=0){
                return it.getString(columnIndex)
            }
        }
    }
    return null
}
private fun MainActivity.logCursorContent(cursor: Cursor?) {
    cursor?.use {
        // 获取列名
        val columnNames = cursor.columnNames

        // 遍历每一行
        while (cursor.moveToNext()) {
            val rowData = StringBuilder()
            // 遍历每一列
            for (columnName in columnNames) {
                val columnIndex = cursor.getColumnIndex(columnName)
                if (columnIndex >= 0) {
                    // 根据列类型获取值
                    when (cursor.getType(columnIndex)) {
                        Cursor.FIELD_TYPE_NULL -> rowData.append("$columnName: null, ")
                        Cursor.FIELD_TYPE_INTEGER -> rowData.append("$columnName: ${cursor.getInt(columnIndex)}, ")
                        Cursor.FIELD_TYPE_FLOAT -> rowData.append("$columnName: ${cursor.getFloat(columnIndex)}, ")
                        Cursor.FIELD_TYPE_STRING -> rowData.append("$columnName: ${cursor.getString(columnIndex)}, ")
                        Cursor.FIELD_TYPE_BLOB -> rowData.append("$columnName: [BLOB], ")
                    }
                }
            }
            // 打印当前行的数据
            Log.d("CursorContent", rowData.toString())
        }
    }
}


