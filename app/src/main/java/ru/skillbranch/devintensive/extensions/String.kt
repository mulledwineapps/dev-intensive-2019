package ru.skillbranch.devintensive.extensions

fun String.truncate(length: Int = 16): String {
    val trimmed = this.trim()
    return if (trimmed.length <= length) trimmed
    else "${trimmed.substring(0, length).trim()}..."

    // exclusive!!!
    // fun String.substring(startIndex: Int, endIndex: Int): String
    // startIndex - the start index (inclusive)
    // endIndex - the end index (exclusive)
}

fun String.stripHtml(): String {
    var step = Regex("<[^<>]*>").replace(this, "")
    step = Regex("&[^;а-яА-я ]*;").replace(step, "")
    step = Regex("[ ]+").replace(step, " ")
    return step
}