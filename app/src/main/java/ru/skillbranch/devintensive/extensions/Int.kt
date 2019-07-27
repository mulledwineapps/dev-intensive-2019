package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
import kotlin.math.roundToInt

fun Int.pxToDp(): Float = this / Resources.getSystem().displayMetrics.density

// (px / scale + 0.5f).toInt()

fun Int.dpToPx(): Float = this * Resources.getSystem().displayMetrics.density

//fun dp2px(value: Float, context: Context) =
//    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//        value, context.resources.displayMetrics)