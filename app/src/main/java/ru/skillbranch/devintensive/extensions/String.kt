package ru.skillbranch.devintensive.extensions

fun String.truncate(lastPos: Int = 16): String {
    val trimmed = this.trim()
    return if (trimmed.length <= lastPos + 1) trimmed
    else "${trimmed.substring(0, lastPos).trim()}..."
}

fun String.stripHtml(): String {
    var step = Regex("<[^<>]*>").replace(this, "")
    step = Regex("&[^;а-яА-я ]*;").replace(step, "")
    step = Regex("[ ]+").replace(step, " ")
    return step
}