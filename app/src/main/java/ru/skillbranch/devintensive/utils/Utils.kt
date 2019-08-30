package ru.skillbranch.devintensive.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

object Utils {

    fun getThemeColor(@AttrRes attrColorRes: Int, theme: Resources.Theme): Int {
        val value = TypedValue()
        theme.resolveAttribute(attrColorRes, value, true)
        return value.data
    }

    fun parseFullName(fullName: String?): Pair<String?, String?> {

        val fn = if (fullName?.trim() == "") null else fullName
        val parts: List<String>? = fn?.split(" ")

        val firstName = parts?.getOrNull(0)
        val lastName = parts?.getOrNull(1)
        return Pair(firstName, lastName)
    }

    fun transliteration(payload: String, divider: String = " "): String {
        val divided = Regex("[^a-zA-Zа-яА-яЁё0-9]+").replace(payload.trim(), divider)

        return buildString {
            for (i in 0 until divided.length) {
                val c = divided[i]
                var cNext = divided.getOrNull(i+1)
                if ("$cNext" == divider) cNext = null
                append(transliterate(c, cNext))
            }
        }
    }

    private fun transliterate(c: Char, cNext: Char?): String {
        val newLetter = when (c.toLowerCase()) {
            'а'-> "a"
            'б'-> "b"
            'в'-> "v"
            'г'-> "g"
            'д'-> "d"
            'е'-> "e"
            'ё'-> "e"
            'ж'-> "zh"
            'з'-> "z"
            'и'-> "i"
            'й'-> "i"
            'к'-> "k"
            'л'-> "l"
            'м'-> "m"
            'н'-> "n"
            'о'-> "o"
            'п'-> "p"
            'р'-> "r"
            'с'-> "s"
            'т'-> "t"
            'у'-> "u"
            'ф'-> "f"
            'х'-> "h"
            'ц'-> "c"
            'ч'-> "ch"
            'ш'-> "sh"
            'щ'-> "sh'"
            'ъ'-> ""
            'ы'-> "i"
            'ь'-> ""
            'э'-> "e"
            'ю'-> "yu"
            'я'-> "ya"
            else -> "$c"
        }
        return if (c.isLowerCase()) newLetter
        else if (cNext == null)
            // если нет следующего символа, 'Я' преобразуется в 'YA'
            newLetter.toUpperCase()
        else {
            // иначе принимаем в расчёт следующий символ
            if (cNext.isUpperCase()) newLetter.toUpperCase() else newLetter.capitalize()
        }
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val f = firstName?.trim()?.getOrNull(0)?.toUpperCase()
        val l = lastName?.trim()?.getOrNull(0)?.toUpperCase()
        val initials: String?
        if (f == null && l == null) {
            initials = null
        } else {
            initials = "${f?:""}${l?:""}"
        }
        return initials
    }
}