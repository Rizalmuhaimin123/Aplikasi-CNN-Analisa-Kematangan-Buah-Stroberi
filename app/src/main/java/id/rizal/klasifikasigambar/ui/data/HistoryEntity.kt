package id.rizal.klasifikasigambar.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val accuracy: Float, // ⬅️ Tambahkan ini
    val imageUri: String
)

