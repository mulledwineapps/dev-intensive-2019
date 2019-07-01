package ru.skillbranch.devintensive.models

class UserView(
    val id: String,
    val fullName: String,
    val nickName: String,
    val avarar: String? = null,
    val status: String? = "offline",
    val initials: String?
) {
    fun printMe() {
        println("""
            id: $id
            fullName: $fullName
            nickName: $nickName
            avarar: $avarar
            status: $status
            initials: $initials
        """.trimIndent())
    }
}