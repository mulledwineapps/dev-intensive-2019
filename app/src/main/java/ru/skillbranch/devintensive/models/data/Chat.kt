package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    var members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {

    fun unreadableMessageCount(): Int = messages.count { !it.isReaded }

    fun lastMessageDate(): Date? {
        return if (messages.isEmpty()) null else messages.last().date
    }

    fun lastMessageShort(): Pair<String, String> {
        if (messages.isEmpty()) return "Сообщений ещё нет" to "@John_Doe"

        val lastMessage = messages.last()

        return lastMessage.shortMessage() to "${lastMessage.from.firstName}"
    }

    private fun isSingle(): Boolean = members.size == 1

    companion object {
        private const val ARCHIVE_ID = "-1"
        fun toArchiveChatItem(chats: List<Chat>): ChatItem? {
            return if (chats.isEmpty()) null
            else {
                val lastChat = chats.sortedByDescending { it.lastMessageDate() }.first()
                val (message, author) = lastChat.lastMessageShort()
                ChatItem(
                    ARCHIVE_ID,
                    null,
                    "",
                    "Архив чатов",
                    message,
                    chats.sumBy { it.unreadableMessageCount() },
                    lastChat.lastMessageDate()?.shortFormat(),
                    false,
                    ChatType.ARCHIVE,
                    author
                )
            }
        }
    }

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