package pt.isel.ls.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Returns a substring of this string that starts at the specified [startIndex] and continues to the end of the string.
 *
 * @return the substring or null if the [startIndex] is greater or equal than the string length
 */
fun String.substringOrNull(startIndex: Int): String? =
    if (this.length > startIndex)
        this.substring(startIndex)
    else
        null

fun String.format(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}
