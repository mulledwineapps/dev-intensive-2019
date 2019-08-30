package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class ArchiveViewModel: ViewModel() {
    private val chatRepository = ChatRepository

    private val archiveItems = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { it.isArchived }
            .map { it.toChatItem() }
    }

    fun getArchiveData(): LiveData<List<ChatItem>> = archiveItems

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

}