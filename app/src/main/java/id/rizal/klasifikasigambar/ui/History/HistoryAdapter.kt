package id.rizal.klasifikasigambar.ui.History

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.rizal.klasifikasigambar.R
import id.rizal.klasifikasigambar.ui.data.HistoryEntity

class HistoryAdapter(private var items: List<HistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val labelView: TextView = view.findViewById(R.id.labelView)
        val accuracyView: TextView = view.findViewById(R.id.accuracyView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Set label dan akurasi secara terpisah
        holder.labelView.text = item.label
        holder.accuracyView.text = "Akurasi: %.2f%%".format(item.accuracy)

        // Tampilkan gambar dari URI
        holder.imageView.setImageURI(Uri.parse(item.imageUri))
    }

    fun updateData(newItems: List<HistoryEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
