package com.otel.mrz.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.otel.mrz.R
import com.otel.mrz.data.DataManager
import com.otel.mrz.databinding.ActivityRoomsBinding

class RoomsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomsBinding
    private lateinit var adapter: RoomsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DataManager.init(this)

        adapter = RoomsAdapter(
            onRoomClick = { room ->
                val intent = Intent(this, RoomDetailActivity::class.java)
                intent.putExtra("room_id", room.id)
                startActivity(intent)
            },
            onRoomDelete = { room ->
                AlertDialog.Builder(this)
                    .setTitle("Odayı Sil")
                    .setMessage("Oda ${room.number} silinsin mi?")
                    .setPositiveButton("Sil") { _, _ ->
                        DataManager.deleteRoom(this, room.id)
                        refreshList()
                    }
                    .setNegativeButton("İptal", null)
                    .show()
            }
        )

        binding.recyclerRooms.layoutManager = LinearLayoutManager(this)
        binding.recyclerRooms.adapter = adapter

        binding.fabNewRoom.setOnClickListener { showNewRoomDialog() }

        refreshList()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val rooms = DataManager.getRooms()
        adapter.submitList(rooms.reversed())
        binding.emptyView.visibility =
            if (rooms.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun showNewRoomDialog() {
        val input = EditText(this).apply {
            hint = "Oda numarası (örn: 2104)"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Yeni Oda")
            .setView(input)
            .setPositiveButton("Oluştur") { _, _ ->
                val number = input.text.toString().trim()
                if (number.isBlank()) return@setPositiveButton
                if (DataManager.getRooms().any { it.number == number }) {
                    android.widget.Toast.makeText(this, "Bu oda zaten mevcut", android.widget.Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val room = DataManager.addRoom(this, number)
                val intent = Intent(this, RoomDetailActivity::class.java)
                intent.putExtra("room_id", room.id)
                startActivity(intent)
            }
            .setNegativeButton("İptal", null)
            .show()

        input.requestFocus()
    }
}
