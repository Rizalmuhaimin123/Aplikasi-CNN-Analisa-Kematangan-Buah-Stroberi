package id.rizal.klasifikasigambar.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.rizal.klasifikasigambar.R
import id.rizal.klasifikasigambar.databinding.FragmentSampleBinding

class SampleFragment : Fragment() {

    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ubah gambar jika ingin dynamic (opsional)
        binding.imageMatang.setImageResource(R.drawable.sampelmatang)
        binding.textMatang.text = "Matang"

        binding.imageMentah.setImageResource(R.drawable.sampementah)
        binding.textMentah.text = "Mentah"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
