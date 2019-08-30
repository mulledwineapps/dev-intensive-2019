package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import java.util.*

abstract class BaseMessage(
    val id: String,
    val from: User,
    val chat: Chat,
    val isIncoming: Boolean = true,
    val date: Date = Date(),
    var isReaded: Boolean = false
) {

    abstract fun formatMessage(): String
    abstract fun shortMessage(): String

    companion object AbstractFactory {
        private var lastId = -1
        fun makeMessage(from: User,
                        chat: Chat,
                        date: Date = Date(),
                        type: String = "fullName",
                        payload: Any?,
                        isIncoming: Boolean = false): BaseMessage {
            lastId++
            return when(type) {
                "image" -> ImageMessage(
                    "$lastId",
                    from,
                    chat,
                    date = date,
                    image = payload as String,
                    isIncoming = isIncoming
                )
                "fullName" -> TextMessage(
                    "$lastId",
                    from,
                    chat,
                    date = date,
                    text = payload as String,
                    isIncoming = isIncoming
                )
                else -> if ("image" == payload || "fullName" == payload) makeMessage(from, chat, date, payload, type, isIncoming)
                        else throw IllegalArgumentException()
            }
        }
    }
}