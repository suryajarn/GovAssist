package com.govassist.app.application

import java.util.Calendar

/**
 * Holds the draft CSHC application fields and validates them one at a time.
 *
 * This mirrors what the backend's Form & Application Agent is designed to do
 * (see the requirements and backend decisions docs): collect a small set of
 * fields, validate each one as it's given, and prepare a complete draft. For
 * now this is all local, in-memory validation with no submission behind it.
 * [com.govassist.app.screens.ApplicationGuideScreen] talks to it only through
 * the functions below, so a real backend call can replace this file later
 * without the screen needing to change.
 *
 * Field set matches the backend doc: name, date of birth, CRN, address, phone.
 */
data class CshcApplicationData(
    val fullName: String = "",
    val dateOfBirth: String = "", // stored as entered, DD/MM/YYYY
    val crn: String = "",
    val streetAddress: String = "",
    val suburb: String = "",
    val state: AuState? = null,
    val postcode: String = "",
    val phone: String = ""
)

enum class AuState(val label: String) {
    NSW("New South Wales"),
    VIC("Victoria"),
    QLD("Queensland"),
    WA("Western Australia"),
    SA("South Australia"),
    TAS("Tasmania"),
    ACT("Australian Capital Territory"),
    NT("Northern Territory")
}

/**
 * Field-level validation. Each function returns an error message to show the
 * user, or null when the value is acceptable. Kept as small pure functions so
 * they can be unit tested and reused wherever this data is collected.
 */
object CshcApplicationValidator {

    fun validateFullName(value: String): String? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> "Enter your full name."
            trimmed.length < 2 -> "Enter your full name."
            !trimmed.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' } ->
                "Names can only contain letters, spaces, hyphens, and apostrophes."
            else -> null
        }
    }

    /** Returns an error message, or null if valid. Expects DD/MM/YYYY. */
    fun validateDateOfBirth(value: String): String? {
        val parts = value.split("/")
        if (parts.size != 3) return "Enter your date of birth as DD/MM/YYYY."

        val day = parts[0].toIntOrNull()
        val month = parts[1].toIntOrNull()
        val year = parts[2].toIntOrNull()
        if (day == null || month == null || year == null) {
            return "Use numbers only, for example 05/03/1955."
        }
        if (month !in 1..12) return "Month must be between 01 and 12."

        val daysInMonth = daysInMonth(month, year)
        if (day !in 1..daysInMonth) return "Day must be between 01 and $daysInMonth for that month."

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year < 1900 || year > currentYear) return "Enter a realistic birth year."

        return null
    }

    /**
     * Not a hard error, since the applicant may be a guardian filling this in
     * for someone else, but worth flagging: CSHC requires Age Pension age.
     */
    fun ageWarningIfAny(value: String, agePensionAge: Int): String? {
        if (validateDateOfBirth(value) != null) return null
        val parts = value.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - year
        val hasHadBirthdayThisYear = (today.get(Calendar.MONTH) + 1 > month) ||
            (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.DAY_OF_MONTH) >= day)
        if (!hasHadBirthdayThisYear) age -= 1

        return if (age < agePensionAge) {
            "This date of birth makes the applicant $age years old, which is under the " +
                "Age Pension age of $agePensionAge. Double check this is correct."
        } else {
            null
        }
    }

    /** Centrelink Reference Numbers are 9 digits followed by 1 letter, e.g. 123456789A. */
    fun validateCrn(value: String): String? {
        val trimmed = value.trim().uppercase()
        val pattern = Regex("^\\d{9}[A-Z]$")
        return if (pattern.matches(trimmed)) null else {
            "Enter a valid CRN: 9 digits followed by 1 letter, for example 123456789A."
        }
    }

    fun validateStreetAddress(value: String): String? {
        val trimmed = value.trim()
        return if (trimmed.length < 5) "Enter your street address." else null
    }

    fun validateSuburb(value: String): String? {
        val trimmed = value.trim()
        return if (trimmed.isEmpty()) "Enter your suburb." else null
    }

    fun validatePostcode(value: String): String? {
        val pattern = Regex("^\\d{4}$")
        return if (pattern.matches(value.trim())) null else "Enter a 4-digit postcode."
    }

    /** Accepts Australian mobile or landline numbers, with or without spaces. */
    fun validatePhone(value: String): String? {
        val digitsOnly = value.replace(" ", "").replace("-", "")
        val pattern = Regex("^0\\d{9}$")
        return if (pattern.matches(digitsOnly)) null else {
            "Enter a 10-digit Australian phone number, for example 0412 345 678."
        }
    }

    private fun daysInMonth(month: Int, year: Int): Int {
        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear) 29 else 28
            else -> 31
        }
    }
}
