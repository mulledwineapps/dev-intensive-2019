package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*
import kotlin.math.min

data class Chat(
    val id: String,
    val title: String,
    var members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {

    companion object {
        private const val MAX_LENGTH_OF_SHORT_MESSAGE = 128
    }

    fun unreadableMessageCount(): Int = messages.count { !it.isReaded }

    fun lastMessageDate(): Date? {
        return if (messages.isEmpty()) null else messages.last().date
    }

    fun lastMessageShort(): Pair<String, String> {
        if (messages.isEmpty()) return "Сообщений ещё нет" to "@John_Doe"

        val lastMessage = messages.last()
        val text =
            if (lastMessage is TextMessage) lastMessage.text.orEmpty().trim()
            else "${lastMessage.from.firstName} - отправил фото"

        return (if (text.isNotEmpty()) text.substring(0, min(text.length, MAX_LENGTH_OF_SHORT_MESSAGE)) else "") to
                "${lastMessage.from.firstName}"
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }
}

enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}