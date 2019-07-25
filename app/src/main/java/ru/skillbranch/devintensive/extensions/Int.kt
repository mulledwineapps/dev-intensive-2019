package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
import android.util.Log

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()