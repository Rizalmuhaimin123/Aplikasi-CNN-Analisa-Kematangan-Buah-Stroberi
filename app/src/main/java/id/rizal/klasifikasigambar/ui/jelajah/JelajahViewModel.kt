package id.rizal.klasifikasigambar.ui.jelajah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JelajahViewModel : ViewModel() {

    // Contoh data LiveData (opsional jika ingin mengembangkan)
    private val _judul = MutableLiveData<String>().apply {
        value = "Berita Seputar Stroberi"
    }

    val judul: LiveData<String> = _judul
}
