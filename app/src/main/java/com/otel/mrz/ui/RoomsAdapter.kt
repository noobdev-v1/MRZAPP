package com.otel.mrz.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.otel.mrz.data.Room
import com.otel.mrz.databinding.ItemRoomBinding

class RoomsAdapter(
    private val onRoomClick: (Room) -> Unit,
    private val onRoomDelete: (Room) -> Unit
) : ListAdapter<Room, RoomsAdapter.VH>(DiffCallback()) {

    inner class VH(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val room = getItem(position)
        holder.binding.apply {
            tvRoomNumber.text = "Oda ${room.number}"
            tvGuestCount.text = "${room.guests.size} misafir"
            root.setOnClickListener { onRoomClick(room) }
            root.setOnLongClickListener { onRoomDelete(room); true }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(a: Room, b: Room) = a.id == b.id
        override fun areContentsTheSame(a: Room, b: Room) = a == b
    }
}
