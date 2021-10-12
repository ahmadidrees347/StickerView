package com.sticker.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.sticker.view.databinding.ActivityMainBinding
import com.sticker.view.dialog.TextDialog
import com.stickers.DrawableSticker
import com.stickers.TextSticker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val logoList = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLogos()
        initViews()
    }

    private fun initViews() {
        with(binding) {
            imgBack.setOnClickListener(this@MainActivity)
            imgSave.setOnClickListener(this@MainActivity)
            imgClear.setOnClickListener(this@MainActivity)
            with(mainLayout) {
                txtText.setOnClickListener(this@MainActivity)
                txtText.setOnClickListener(this@MainActivity)
                img1.setOnClickListener(this@MainActivity)
                img2.setOnClickListener(this@MainActivity)
                img3.setOnClickListener(this@MainActivity)
                img4.setOnClickListener(this@MainActivity)
                img5.setOnClickListener(this@MainActivity)
            }
        }
    }

    private fun loadLogos() {
        //Logos
        val array = resources.obtainTypedArray(R.array.logos)
        for (index in 0 until array.length()) {
            val resId = array.getResourceId(index, 0)
            logoList.add(resId)
        }
        array.recycle()
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view?.id) {
                R.id.img_back -> {
                    onBackPressed()
                }
                R.id.txtText -> {
                    TextDialog(this@MainActivity) {
                        addTextSticker(it)
                    }.show()
                }
                R.id.img_clear -> {
                    //Clear All Stickers
                    stickerView.removeAllStickers()
                    stickerView.invalidate()
                }
                R.id.img_save -> {
                    if (stickerView.isNoneSticker) {
                        Toast.makeText(
                            this@MainActivity,
                            "Add Some Sticker First!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val fileName = StringBuilder()
                        fileName.append(System.currentTimeMillis())
                        fileName.append(".png")
                        if (!stickerView.isLocked) {
                            stickerView.isLocked = true
                        }
                        saveLogo(fileName.toString())
                    }
                }
                R.id.img1 -> {
                    addLogo(logoList[0])
                }
                R.id.img2 -> {
                    addLogo(logoList[1])
                }
                R.id.img3 -> {
                    addLogo(logoList[2])
                }
                R.id.img4 -> {
                    addLogo(logoList[3])
                }
                R.id.img5 -> {
                    addLogo(logoList[4])
                }
            }
        }
    }

    private fun addTextSticker(strText: String) {
        val textSticker = TextSticker(this@MainActivity)
        textSticker.text = strText
        textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER)
        textSticker.resizeText()
        binding.stickerView.addSticker(textSticker)
        binding.stickerView.invalidate()
    }

    private fun saveLogo(fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val filePath = executeSaveLogoTask(binding.stickerView, fileName)
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "File Saved : $filePath",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addLogo(resId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, resId)
        val drawable: Drawable = BitmapDrawable(resources, bitmap)
        binding.stickerView.addSticker(DrawableSticker(drawable))
        binding.stickerView.invalidate()
    }

    private fun executeSaveLogoTask(imageView: View, fileName: String): String {
        val saveImage: Bitmap = imageView.drawToBitmap(Bitmap.Config.ARGB_8888)
        var file = File(getExternalFilesDir("").toString())
        if (!file.exists()) {
            file.mkdir()
        }
        file = File(file, fileName)
        return try {
            val fileOutputStream = FileOutputStream(file)
            saveImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            "" + file.absoluteFile
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}