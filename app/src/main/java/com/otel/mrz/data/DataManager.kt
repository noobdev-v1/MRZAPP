package com.otel.mrz.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object DataManager {

    private const val PREF_NAME = "mrz_data"
    private const val KEY_ROOMS = "rooms"
    private const val KEY_DATE = "saved_date"
    private const val KEY_WA_NUMBER = "wa_number"

    private val gson = Gson()
    private val rooms = mutableListOf<Room>()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val today = todayStr()
        val savedDate = prefs.getString(KEY_DATE, "")

        if (savedDate != today) {
            prefs.edit().remove(KEY_ROOMS).putString(KEY_DATE, today).apply()
            rooms.clear()
        } else {
            val json = prefs.getString(KEY_ROOMS, null)
            if (json != null) {
                val type = object : TypeToken<MutableList<Room>>() {}.type
                val loaded: MutableList<Room> = gson.fromJson(json, type)
                rooms.clear()
                rooms.addAll(loaded)
            }
        }
    }

    fun getRooms(): List<Room> = rooms.toList()

    fun getRoom(id: String): Room? = rooms.find { it.id == id }

    fun addRoom(context: Context, number: String): Room {
        val room = Room(id = UUID.randomUUID().toString(), number = number)
        rooms.add(room)
        save(context)
        return room
    }

    fun deleteRoom(context: Context, id: String) {
        rooms.removeAll { it.id == id }
        save(context)
    }

    fun addGuest(context: Context, roomId: String, guest: Guest) {
        rooms.find { it.id == roomId }?.guests?.add(guest)
        save(context)
    }

    fun removeGuest(context: Context, roomId: String, guestIndex: Int) {
        rooms.find { it.id == roomId }?.guests?.removeAt(guestIndex)
        save(context)
    }

    fun saveWaNumber(context: Context, number: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_WA_NUMBER, number).apply()
    }

    fun getWaNumber(context: Context): String {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_WA_NUMBER, "") ?: ""
    }

    private fun save(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ROOMS, gson.toJson(rooms)).apply()
    }

    private fun todayStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
