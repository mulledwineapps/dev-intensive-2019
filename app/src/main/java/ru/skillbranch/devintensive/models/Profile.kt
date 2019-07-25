package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.utils.Utils

data class Profile(
    val firstName: String,
    val lastName: String,
    val about: String,
    val repository: String,
    val rating: Int = 0,
    val respect: Int = 0
) {
    val rank: String = "Junior Android Developer"
    val nickName: String // = "John Doe" // TODO implement me
    get() {
        return Utils.transliteration("$firstName $lastName","_")
    }

    fun toMap(): Map<String, Any> = mapOf(
        "nickname" to nickName,
        "rank" to rank,
        "firstName" to firstName,
        "lastName" to lastName,
        "about" to about,
        "repository" to repository,
        "rating" to rating,
        "respect" to respect
    )
}