package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class User(
    val id: String,
    var firstName: String?,
    var lastName: String?,
    var avatar: String?,
    var rating: Int = 0,
    var respect: Int = 0,
    val lastVisit: Date? = Date(),
    val isOnline: Boolean = false
) {
    var introBit: String

    constructor(id: String, firstName: String?, lastName: String?) : this(
        id = id,
        firstName = firstName,
        lastName = lastName,
        avatar = null)

    constructor(id: String) : this (id, "John", "Doe")

    init {
        introBit = getIntro()

        println("It's Alive!!!\n" +
                "${if (lastName==="Doe") "His name is $firstName $lastName" else
                    "And his name is ${firstName ?: "unknown"} ${lastName ?: "unknown"}!!!"}\n")
    }

    companion object Factory {
        private var lastId: Int = -1
        fun makeUser(fullName: String?): User {
            lastId++

            val (firstName, lastName) = Utils.parseFullName(fullName)
            return User(
                "$lastId",
                firstName = firstName,
                lastName = lastName
            )
        }
    }

    private fun getIntro(): String = """
        tu tu tu tuuuuuuu !!!
        tu tu tu tuuuuuuuuu ....
        ${"\n\n\n"}
        $firstName $lastName
    """.trimIndent()

    fun printMe() = println("""
        id: $id
        firstName: $firstName
        lastName: $lastName
        avatar: $avatar
        rating: $rating
        respect: $respect
        lastVisit: $lastVisit
        isOnline: $isOnline
    """.trimIndent())

    class Builder() {
        companion object Builder {
            private var nextId: Int = 0
        }

        private var id: String = "$nextId"
        private var firstName: String? = null
        private var lastName: String? = null
        private var avatar: String? = null
        private var rating: Int = 0
        private var respect: Int = 0
        private var lastVisit: Date? = Date()
        private var isOnline: Boolean = false

        fun id(value: String) = apply { id = value }
        fun firstName(value: String?) = apply { firstName = value }
        fun lastName(value: String?) = apply { lastName = value }
        fun avatar(value: String?) = apply { avatar = value }
        fun rating(value: Int) = apply { rating = value }
        fun respect(value: Int) = apply { respect = value }
        fun lastVisit(value: Date?) = apply { lastVisit = value }
        fun isOnline(value: Boolean) = apply { isOnline = value }

        fun build(): User {
            if (id.toIntOrNull() != null) nextId = id.toInt()+1
            return User(
                id,
                firstName,
                lastName,
                avatar,
                rating,
                respect,
                lastVisit,
                isOnline
            )
        }
    }
}