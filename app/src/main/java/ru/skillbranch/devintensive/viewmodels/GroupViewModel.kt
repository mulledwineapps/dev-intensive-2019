package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.GroupRepository

class GroupViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val groupRepository = GroupRepository
    private val userItems = mutableLiveData(loadUsers())
    private val selectedItems = Transformations.map(userItems) { users -> users.filter { it.isSelected } }

    fun getUsersData(): LiveData<List<UserItem>> {
        // 2:38:40 5-го занятия
        // MediatorLiveData - класс, который способен получать на вход большое количество источников и может
        // подписаться на их изменения
        // например, есть несколько LiveData, которые могут изменяться и нам нужно какое-то одно результирующее значение,
        // которое будет обрабатывать изменение каждой из этих LiveData
        // Сейчас нам необходимо обрабатывать изменения Query запроса и изменения UserItems, которые были выбраны
        // в addSource указываем, на какие данные необходимо подписаться MediatorLiveData и вторым аргументом передаём
        // лямбду, которая будет вызываться каждый раз, когда эти данные будут изменены (по сути - это метод OnChange)
        val result = MediatorLiveData<List<UserItem>>()

        val filterF = {
            val queryStr = query.value!!
            val users = userItems.value!!

            result.value = if (queryStr.isEmpty()) users else users.filter { it.fullName.contains(queryStr, true) }
        }

        result.addSource(userItems) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }

        return result
    }

    fun getSelectedData(): LiveData<List<UserItem>> = selectedItems

    fun handleSelectedItem(userId: String) {
        userItems.value = userItems.value!!.map {
            if (it.id == userId)
                it.copy(isSelected = !it.isSelected)
            else it
        }
    }

    fun handleRemoveChip(userId: String) {
        userItems.value = userItems.value!!.map {
            if (it.id == userId)
                it.copy(isSelected = false)
            else it
        }
    }

    fun handleSearchQuery(text: String?) {
        query.value = text
    }

    fun handleCreateGroup() {
        groupRepository.createChat(selectedItems.value!!)
    }

    // ~2:20:++ 5-го занятия
    // придерживаемся структурного паттерна MVVM -
    // все взаимодействия помещаем во вью модель, а слой представления (вью) отображает только изменения данных
    // мы не делаем никакую логику, связанную с обработкой данных на слое представления
    // и оттуда же мы не удаляем какие-то вью элементы
    // все элементы подвязываем к нашим данным - если данные изменяются, должны изменяться и вью элементы
    // вью модель должна по сути являться хэндлером всех взаимодействий пользователя
    // т.е. если пользователь жмёт на какие-то элементы UI, они должны транслироваться во вью модель и изменять данные

    private fun loadUsers(): List<UserItem> = groupRepository.loadUsers().map { it.toUserItem() }
}