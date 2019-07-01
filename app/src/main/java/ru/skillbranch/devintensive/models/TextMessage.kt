package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extension.humanizeDiff
import java.util.*

class TextMessage(
    id: String,
    from: User?,
    chat: Chat,
    isIncoming: Boolean = false,
    date: Date = Date(),
    val text: String?
): BaseMessage(id, from, chat, isIncoming, date) {
    override fun formatMessage(): String =
        "${from?.firstName} ${if(isIncoming) "получил" else "отправил"} сообщение ${date.humanizeDiff()}"
}