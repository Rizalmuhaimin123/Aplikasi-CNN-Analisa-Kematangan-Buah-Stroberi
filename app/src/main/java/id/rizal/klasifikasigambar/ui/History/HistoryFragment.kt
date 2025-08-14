package id.rizal.klasifikasigambar.ui.history

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.rizal.klasifikasigambar.R
import id.rizal.klasifikasigambar.ui.data.AppDatabase
import id.rizal.klasifikasigambar.ui.data.HistoryEntity
import id.rizal.klasifikasigambar.ui.History.HistoryAdapter


class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Inisialisasi adapter dengan list kosong dulu
        historyAdapter = HistoryAdapter(emptyList())
        recyclerView.adapter = historyAdapter

        db = AppDatabase.getDatabase(requireContext())

        // ✅ Amati perubahan data dan update adapter
        db.historyDao().getAllHistory().observe(viewLifecycleOwner) { historyList ->
            historyAdapter.updateData(historyList)
        }
    }
}

