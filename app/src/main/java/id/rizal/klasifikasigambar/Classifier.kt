package id.rizal.klasifikasigambar

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier(assetManager: AssetManager) {

    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModel(assetManager, "StrawberryModel.tflite"))
    }

    @Throws(IOException::class)
    private fun loadModel(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    fun predict(bitmap: Bitmap): String {
        val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        for (x in 0 until 224) {
            for (y in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                input[0][x][y][0] = (pixel shr 16 and 0xFF) / 255.0f
                input[0][x][y][1] = (pixel shr 8 and 0xFF) / 255.0f
                input[0][x][y][2] = (pixel and 0xFF) / 255.0f
            }
        }

        val output = Array(1) { FloatArray(3) } // 3 kelas
        interpreter.run(input, output)

        val maxValue = output[0].maxOrNull() ?: 0f
        val resultIndex = output[0].indexOfFirst { it == maxValue }

        return when (resultIndex) {
            0 -> "Mentah (Unripe)"
            1 -> "Setengah Matang (Semi-ripe)"
            2 -> "Matang (Ripe)"
            else -> "Tidak diketahui"
        }
    }
}
