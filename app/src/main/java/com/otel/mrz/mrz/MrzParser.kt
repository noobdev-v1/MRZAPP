package com.otel.mrz.mrz

import com.otel.mrz.data.Guest

object MrzParser {

    private val MRZ_LINE_REGEX = Regex("^[A-Z0-9<]{30,44}$")

    fun findAndParse(rawText: String): Guest? {
        val lines = rawText.lines()
            .map { it.trim().uppercase().replace(" ", "") }
            .filter { MRZ_LINE_REGEX.matches(it) }

        // TD3: Pasaport - 2 satır x 44 karakter
        val td3Lines = lines.filter { it.length == 44 }
        if (td3Lines.size >= 2) {
            parseTd3(td3Lines[td3Lines.size - 2], td3Lines[td3Lines.size - 1])
                ?.let { return it }
        }

        // TD1: Kimlik kartı - 3 satır x 30 karakter
        val td1Lines = lines.filter { it.length == 30 }
        if (td1Lines.size >= 3) {
            parseTd1(td1Lines[td1Lines.size - 3], td1Lines[td1Lines.size - 2], td1Lines[td1Lines.size - 1])
                ?.let { return it }
        }

        return null
    }

    // TD3 - Pasaport
    private fun parseTd3(line1: String, line2: String): Guest? {
        return try {
            val namePart = line1.substring(5).split("<<")
            val lastName = namePart.getOrElse(0) { "" }.replace("<", " ").trim()
            val firstName = namePart.getOrElse(1) { "" }.replace("<", " ").trim()

            val docNumber = line2.substring(0, 9).replace("<", "")
            val nationality = line2.substring(10, 13).replace("<", "")
            val dob = line2.substring(13, 19)
            val gender = line2.substring(20, 21).let { if (it == "<") "M" else it }
            val expiry = line2.substring(21, 27)

            Guest(
                firstName = firstName,
                lastName = lastName,
                docNumber = docNumber,
                dob = dob,
                gender = gender,
                nationality = nationality,
                expiry = expiry,
                docType = "PASSPORT"
            )
        } catch (e: Exception) {
            null
        }
    }

    // TD1 - Kimlik Kartı
    private fun parseTd1(line1: String, line2: String, line3: String): Guest? {
        return try {
            val docNumber = line1.substring(5, 14).replace("<", "")

            val dob = line2.substring(0, 6)
            val gender = line2.substring(7, 8).let { if (it == "<") "M" else it }
            val expiry = line2.substring(8, 14)
            val nationality = line2.substring(15, 18).replace("<", "")

            val namePart = line3.split("<<")
            val lastName = namePart.getOrElse(0) { "" }.replace("<", " ").trim()
            val firstName = namePart.getOrElse(1) { "" }.replace("<", " ").trim()

            Guest(
                firstName = firstName,
                lastName = lastName,
                docNumber = docNumber,
                dob = dob,
                gender = gender,
                nationality = nationality,
                expiry = expiry,
                docType = "ID_CARD"
            )
        } catch (e: Exception) {
            null
        }
    }
}
