package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extensions.humanizeDiff
import java.util.*

class ImageMessage(
    id: String,
    from: User?,
    chat: Chat,
    isIncoming: Boolean = false,
    date: Date = Date(),
    var image: String?
): BaseMessage(id, from, chat, isIncoming, date) {
    override fun formatMessage(): String =
        "${from?.firstName} ${if(isIncoming) "получил" else "отправил"} изображение ${date.humanizeDiff()}"
        //"""
        //id: $id
        //from: ${from?.firstName}
        //chat: $chat
        //isIncoming: ${if(isIncoming) "получил" else "отправил"}
        //date: ${date.format()}
        //image: $image
        //""".trimIndent()
}