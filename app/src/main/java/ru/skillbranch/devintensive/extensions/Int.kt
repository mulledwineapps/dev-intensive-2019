package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Int.pxToDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

//fun dp2px(value: Float, context: Context) =
//    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//        value, context.resources.displayMetrics)

fun Int.spToPx(): Float = this.toFloat().spToPx()