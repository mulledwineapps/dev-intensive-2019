package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.*
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    // если LiveData, которая хранится в chatRepository будет изменена,
    // то и LiveData, которая будет эмиттиться этим полем (chats) тоже будет изменена
    // вызывая Transformations.map мы неявным образом подписываемся на изменения источника
    // не используйте методы observe внутри view модели - нужно использовать либо Transformations, либо MediatorData

    private val chats = chatRepository.loadChats()
    // Transformations.map(chatRepository.loadChats()) { chats ->
    //     return@map chats.filter { !it.isArchived }
    //         .map { it.toChatItem() }
    //         .sortedBy { it.id.toInt() }
    // }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!

            val (archived, unarchived) = this.chats.value!!.partition { it.isArchived }

            val chatItems = (
                    if (queryStr.isEmpty()) unarchived.map { it.toChatItem() }
                    else unarchived.map { it.toChatItem() }.filter { it.title.contains(queryStr, true) })
                .sortedBy { it.id.toInt() }
                .toMutableList()

            Chat.toArchiveChatItem(archived)?.let {
                chatItems.add(0, it)
            }

            result.value = chatItems
        }

        result.addSource(chats) { filterF.invoke(); }
        result.addSource(query) { filterF.invoke(); }

        return result
    }

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

    fun handleSearchQuery(text: String?) {
        query.value = text
    }
}