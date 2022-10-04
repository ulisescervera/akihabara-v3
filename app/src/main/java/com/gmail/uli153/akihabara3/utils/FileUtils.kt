package com.gmail.uli153.akihabara3.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.gmail.uli153.akihabara3.utils.extensions.toPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

object FileUtils {

    suspend fun cropSquare(
        path: String,
        targetFile: File,
        croppedSize: Int = 100.toPx.toInt()
    ): File = withContext(Dispatchers.Default) {
        val bitmap = BitmapFactory.decodeFile(path)
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size)/2
        val y = (bitmap.height - size)/2
        val cropped = Bitmap.createBitmap(bitmap, x, y, size, size)
        val scaled = Bitmap.createScaledBitmap(cropped, croppedSize, croppedSize, true)

        val oldExif = ExifInterface(path)
        val exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val finalBitmap = if (exifOrientation != null) {
            rotateBitmap(scaled, oldExif)
        } else {
            scaled
        }

        saveBitmap(finalBitmap, targetFile)

        return@withContext targetFile
    }

    suspend fun rotateBitmap(
        bitmap: Bitmap,
        exif: ExifInterface
    ): Bitmap = withContext(Dispatchers.Default) {
        var rotate = 0f
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270f
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180f
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90f
        }
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return@withContext Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    suspend fun saveBitmap(
        bitmap: Bitmap,
        file: File,
        quality: Int = 100
    ) = withContext(Dispatchers.Default) {
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun moveFile(inputFile: File, outputFile: File) = withContext(Dispatchers.IO) {
        var inputStream: InputStream?
        var out: OutputStream?
        try {
            //create output directory if it doesn't exist
            if (outputFile.exists()) {
                outputFile.delete()
            }

            if (!outputFile.exists()) {
                outputFile.createNewFile()
            }
            inputStream = FileInputStream(inputFile)
            out = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            inputStream.close()
            inputStream = null

            // write the output file
            out.flush()
            out.close()
            out = null

            // delete the original file
            inputFile.delete()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}