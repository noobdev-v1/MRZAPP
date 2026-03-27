package com.otel.mrz.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.otel.mrz.data.DataManager
import com.otel.mrz.data.Guest
import com.otel.mrz.databinding.ActivityRoomDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class RoomDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomDetailBinding
    private lateinit var adapter: GuestsAdapter
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("room_id") ?: run { finish(); return }

        val room = DataManager.getRoom(roomId) ?: run { finish(); return }
        supportActionBar?.title = "Oda ${room.number}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = GuestsAdapter { index ->
            AlertDialog.Builder(this)
                .setTitle("Misafiri Sil")
                .setMessage("Misafir silinsin mi?")
                .setPositiveButton("Sil") { _, _ ->
                    DataManager.removeGuest(this, roomId, index)
                    refreshList()
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        binding.recyclerGuests.layoutManager = LinearLayoutManager(this)
        binding.recyclerGuests.adapter = adapter

        binding.btnAddGuest.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("room_id", roomId)
            startActivity(intent)
        }

        binding.btnShare.setOnClickListener { showShareDialog() }

        refreshList()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish(); return true
    }

    private fun refreshList() {
        val room = DataManager.getRoom(roomId) ?: return
        adapter.submitList(room.guests.toList())
        binding.tvGuestCount.text = "${room.guests.size} misafir"
        binding.emptyView.visibility =
            if (room.guests.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnShare.isEnabled = room.guests.isNotEmpty()
    }

    private fun showShareDialog() {
        val room = DataManager.getRoom(roomId) ?: return
        val savedNumber = DataManager.getWaNumber(this)

        val input = EditText(this).apply {
            hint = "905xxxxxxxxx"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(savedNumber)
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("WhatsApp ile Gönder")
            .setMessage("Ülke kodu dahil numara girin (başında + olmadan)")
            .setView(input)
            .setPositiveButton("Gönder") { _, _ ->
                val number = input.text.toString().trim().replace(Regex("[^0-9]"), "")
                if (number.isBlank()) { Toast.makeText(this, "Numara giriniz", Toast.LENGTH_SHORT).show(); return@setPositiveButton }

                DataManager.saveWaNumber(this, number)

                val today = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                val msg = buildString {
                    appendLine("🏨 *Oda ${room.number}* — $today")
                    appendLine("─────────────────────────")
                    room.guests.forEachIndexed { i, g ->
                        appendLine()
                        appendLine("*${i + 1}. ${g.fullName()}*")
                        appendLine("📄 ${g.docNumber.ifBlank { "—" }}")
                        appendLine("🎂 ${g.formattedDob()}  |  ${g.genderLabel()}  |  ${g.nationality.ifBlank { "—" }}")
                        appendLine("📅 Geçerlilik: ${g.formattedExpiry()}")
                    }
                    appendLine()
                    appendLine("─────────────────────────")
                    append("✅ ${room.guests.size} misafir")
                }

                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$number?text=${Uri.encode(msg)}")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "WhatsApp açılamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()

        input.selectAll()
    }
}
