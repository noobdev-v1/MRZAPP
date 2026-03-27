package com.otel.mrz.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.otel.mrz.data.DataManager
import com.otel.mrz.data.Guest
import com.otel.mrz.databinding.ActivityConfirmBinding

class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding
    private lateinit var guest: Guest
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomId = intent.getStringExtra("room_id") ?: run { finish(); return }

        guest = Guest(
            firstName = intent.getStringExtra("first_name") ?: "",
            lastName = intent.getStringExtra("last_name") ?: "",
            docNumber = intent.getStringExtra("doc_number") ?: "",
            dob = intent.getStringExtra("dob") ?: "",
            gender = intent.getStringExtra("gender") ?: "",
            nationality = intent.getStringExtra("nationality") ?: "",
            expiry = intent.getStringExtra("expiry") ?: "",
            docType = intent.getStringExtra("doc_type") ?: ""
        )

        binding.tvName.text = guest.fullName()
        binding.tvDocNumber.text = guest.docNumber.ifBlank { "—" }
        binding.tvDob.text = guest.formattedDob()
        binding.tvGender.text = guest.genderLabel()
        binding.tvNationality.text = guest.nationality.ifBlank { "—" }
        binding.tvExpiry.text = guest.formattedExpiry()
        binding.tvDocType.text = guest.docTypeLabel()

        binding.btnApprove.setOnClickListener {
            DataManager.addGuest(this, roomId, guest)
            finish()
        }

        binding.btnRetry.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            intent.putExtra("room_id", roomId)
            startActivity(intent)
            finish()
        }
    }
}
