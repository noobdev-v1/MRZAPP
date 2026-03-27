package com.otel.mrz.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.otel.mrz.data.Guest
import com.otel.mrz.databinding.ItemGuestBinding

class GuestsAdapter(
    private val onDelete: (Int) -> Unit
) : ListAdapter<Guest, GuestsAdapter.VH>(DiffCallback()) {

    inner class VH(val binding: ItemGuestBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemGuestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val guest = getItem(position)
        holder.binding.apply {
            tvName.text = guest.fullName()
            tvDocNumber.text = guest.docNumber.ifBlank { "—" }
            tvDob.text = guest.formattedDob()
            tvGender.text = guest.genderLabel()
            tvNationality.text = guest.nationality.ifBlank { "—" }
            tvExpiry.text = guest.formattedExpiry()
            tvDocType.text = guest.docTypeLabel()
            btnDelete.setOnClickListener { onDelete(position) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Guest>() {
        override fun areItemsTheSame(a: Guest, b: Guest) = a.docNumber == b.docNumber && a.dob == b.dob
        override fun areContentsTheSame(a: Guest, b: Guest) = a == b
    }
}
