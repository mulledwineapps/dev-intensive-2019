package ru.skillbranch.devintensive.extension

import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.IllegalStateException

const val SECOND = 1000L
const val MINUTE = 60* SECOND
const val HOUR = 60* MINUTE
const val DAY = 24* HOUR

fun Date.format(pattern: String="HH:mm:ss dd.MM.yy") : String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

enum class TimeUnits {
    SECOND,
    MINUTE,
    HOUR,
    DAY
}

fun getUnitForm(value: Long, unit: TimeUnits): String {
    val i = value % 10
    // 21-24 час(а), 31-34 час(а), но 11-14 часов
    val ii = value % 100
    return when (unit) {
        TimeUnits.MINUTE -> {
            when {
                ii in 11L..14L -> "минут"
                i == 1L -> "минуту"
                i in 2L..4L -> "минуты"
                else -> "минут"
            }
        }
        TimeUnits.HOUR -> {
            when {
                ii in 11L..14L -> "часов"
                i == 1L -> "час"
                i in 2L..4L -> "часа"
                else -> "часов"
            }
        }
        TimeUnits.DAY -> {
            when {
                ii in 11L..14L -> "дней"
                i == 1L -> "день"
                i in 2L..4L -> "дня"
                else -> "дней"
            }
        }
        else -> throw IllegalStateException("Неверная единица времени: $unit")
    }
}

fun Date.humanizeDiff(date: Date = Date()): String {
    // если аргумент опущен - оцениваем this.time по отношению к текущей дате, иначе - дату-аргумент по отношению к this
    val backward: Boolean = when (date) {
        Date() -> date.time > this.time
        else -> this.time > date.time
    }

    val diff = abs(date.time - this.time)
    val seconds: Long = diff / SECOND
    val minutes = diff / MINUTE
    val hours = diff / HOUR
    val days = diff / DAY

    return if (backward) {
        when {
            seconds in 0..1 -> "только что"
            seconds in 1..45 -> "несколько секунд назад"
            seconds in 45..75 -> "минуту назад"
            minutes <= 45 -> "$minutes ${getUnitForm(minutes, TimeUnits.MINUTE)} назад"
            minutes in 45..75 -> "час назад"
            hours <= 22 -> "$hours ${getUnitForm(hours, TimeUnits.HOUR)} назад"
            hours in 22..26 -> "день назад"
            days <= 360 -> "$days ${getUnitForm(days, TimeUnits.DAY)} назад"
            else -> "более года назад"
        }
    } else {
        when {
            seconds in 0..1 -> "только что"
            seconds in 1..45 -> "через несколько секунд"
            seconds in 45..75 -> "через минуту"
            minutes <= 45 -> "через $minutes ${getUnitForm(minutes, TimeUnits.MINUTE)}"
            minutes in 45..75 -> "через час"
            hours <= 22 -> "через $hours ${getUnitForm(hours, TimeUnits.HOUR)}"
            hours in 22..26 -> "через день"
            days <= 360 -> "через $days ${getUnitForm(days, TimeUnits.DAY)}"
            else -> "более чем через год"
        }
    }
}