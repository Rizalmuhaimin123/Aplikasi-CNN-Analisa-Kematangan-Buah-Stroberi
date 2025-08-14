package id.rizal.klasifikasigambar.ui.jelajah

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.rizal.klasifikasigambar.R

class JelajahFragment : Fragment() {

    private lateinit var jelajahViewModel: JelajahViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi ViewModel
        jelajahViewModel = ViewModelProvider(this)[JelajahViewModel::class.java]

        // Inflate layout Jelajah
        return inflater.inflate(R.layout.fragment_jelajah, container, false)
    }
}
