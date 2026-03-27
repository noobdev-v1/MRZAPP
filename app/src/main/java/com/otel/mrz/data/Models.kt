package com.otel.mrz.data

data class Guest(
    val firstName: String,
    val lastName: String,
    val docNumber: String,
    val dob: String,
    val gender: String,
    val nationality: String,
    val expiry: String,
    val docType: String
) {
    fun fullName() = "$firstName $lastName".trim()

    fun formattedDob() = formatMrzDate(dob)
    fun formattedExpiry() = formatMrzDate(expiry)

    fun genderLabel() = when (gender) {
        "M" -> "Erkek"
        "F" -> "Kadın"
        else -> gender
    }

    fun docTypeLabel() = when (docType) {
        "PASSPORT" -> "Pasaport"
        else -> "Kimlik Kartı"
    }

    fun toShareText(): String {
        return buildString {
            appendLine("👤 ${fullName()}")
            appendLine("📄 ${docNumber.ifBlank { "—" }}")
            appendLine("🎂 ${formattedDob()}  |  ${genderLabel()}  |  ${nationality.ifBlank { "—" }}")
            appendLine("📅 Geçerlilik: ${formattedExpiry()}")
        }
    }

    private fun formatMrzDate(s: String): String {
        if (s.length < 6) return s
        val yy = s.substring(0, 2).toIntOrNull() ?: return s
        val mm = s.substring(2, 4)
        val dd = s.substring(4, 6)
        val year = if (yy > 30) 1900 + yy else 2000 + yy
        return "$dd.$mm.$year"
    }
}

data class Room(
    val id: String,
    val number: String,
    val guests: MutableList<Guest> = mutableListOf()
)
