package id.rizal.klasifikasigambar.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import id.rizal.klasifikasigambar.databinding.FragmentHomeBinding
import id.rizal.klasifikasigambar.ml.StrawberryModel
import id.rizal.klasifikasigambar.ui.data.AppDatabase
import id.rizal.klasifikasigambar.ui.data.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val IMAGE_PICK_CODE = 1001
    private var selectedBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        binding.btnProses.setOnClickListener {
            selectedBitmap?.let { bitmap ->
                classifyImage(bitmap)
            } ?: run {
                binding.tvKeterangan.text = "Silakan pilih gambar terlebih dahulu."
            }
        }

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            val inputStream: InputStream? = imageUri?.let {
                requireActivity().contentResolver.openInputStream(it)
            }
            selectedBitmap = BitmapFactory.decodeStream(inputStream)
            binding.imagePreview.setImageBitmap(selectedBitmap)
            binding.tvKeterangan.text = "Gambar berhasil dimuat. Klik proses untuk analisa."
        }
    }

    private fun classifyImage(bitmap: Bitmap) {
        try {
            val model = StrawberryModel.newInstance(requireContext())
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)

            val inputFeature = TensorBuffer.createFixedSize(
                intArrayOf(1, 150, 150, 3), DataType.FLOAT32
            )
            inputFeature.loadBuffer(inputBuffer)

            val outputs = model.process(inputFeature)
            val confidences = outputs.outputFeature0AsTensorBuffer.floatArray

            val labels = arrayOf("Mentah", "Setengah Matang", "Matang")
            val maxIdx = confidences.indices.maxByOrNull { confidences[it] } ?: -1
            val confidence = confidences[maxIdx]

            if (maxIdx != -1 && confidence >= 0.6f) {
                val label = labels[maxIdx]
                val accuracyPercent = confidence * 100

                binding.tvKeterangan.text = "Hasil: $label\nAkurasi: ${"%.2f".format(accuracyPercent)}%"
                binding.tvKeterangan.setTextColor(
                    when (label) {
                        "Matang" -> resources.getColor(android.R.color.holo_green_dark, null)
                        "Setengah Matang" -> resources.getColor(android.R.color.holo_orange_dark, null)
                        else -> resources.getColor(android.R.color.holo_red_dark, null)
                    }
                )

                // Simpan ke database (tidak navigasi ke fragment history)
                lifecycleScope.launch(Dispatchers.IO) {
                    saveToHistory(label, accuracyPercent, bitmap)
                }

            } else {
                binding.tvKeterangan.text = "Gambar tidak dikenali (akurasi rendah)"
                binding.tvKeterangan.setTextColor(
                    resources.getColor(android.R.color.darker_gray, null)
                )
            }

            model.close()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvKeterangan.text = "Terjadi kesalahan saat klasifikasi."
        }
    }

    private suspend fun saveToHistory(label: String, accuracy: Float, bitmap: Bitmap) {
        try {
            val filename = "IMG_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, filename)
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }

            val uri = Uri.fromFile(file)
            val entity = HistoryEntity(label = label, accuracy = accuracy, imageUri = uri.toString())

            val db = AppDatabase.getDatabase(requireContext())
            db.historyDao().insert(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * 150 * 150 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(150 * 150)
        bitmap.getPixels(intValues, 0, 150, 0, 0, 150, 150)

        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f))
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))
            byteBuffer.putFloat(((pixel and 0xFF) / 255.0f))
        }

        return byteBuffer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
