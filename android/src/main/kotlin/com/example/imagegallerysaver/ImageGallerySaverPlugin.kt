package com.example.imagegallerysaver

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.os.Environment.DIRECTORY_PICTURES

class ImageGallerySaverPlugin(private val registrar: Registrar): MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "save_image")
      channel.setMethodCallHandler(ImageGallerySaverPlugin(registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result): Unit {
    when {
        call.method == "saveImageToGallery" -> {
          val image = call.argument<ByteArray>("img") ?: return
          val quality = call.argument<Int>("quality") ?: return
          val folder = call.argument<String>("dirName") ?: return
          val name = call.argument<String>("name")

          result.success(saveImageToGallery(BitmapFactory.decodeByteArray(image,0,image.size), quality, name, folder))
        }
        else -> result.notImplemented()
    }

  }

  private fun generateFile(extension: String = "", name: String? = null, folder: String? = ""): File {

    var appDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);

    if (!appDir.exists()){
      appDir.mkdir()
    }

    appDir = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "DevTools");

    if (!appDir.exists()){
      appDir.mkdir()
    }

    appDir = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "DevTools/$folder");

    if (!appDir.exists()) {
      appDir.mkdir()
    }
    var fileName = name?:System.currentTimeMillis().toString()
    if (extension.isNotEmpty()) {
      fileName += (".$extension")
    }
    return File(appDir, fileName)
  }

  private fun saveImageToGallery(bmp: Bitmap, quality: Int, name: String?, folder: String?): String {
    val context = registrar.activeContext().applicationContext
    val file = generateFile("jpg", name = name, folder = folder)
    try {
      val fos = FileOutputStream(file)
      bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos)
      fos.flush()
      fos.close()
      val uri = Uri.fromFile(file)
      print(uri)
      context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
      bmp.recycle()
      return uri.toString()
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return ""
  }

}
