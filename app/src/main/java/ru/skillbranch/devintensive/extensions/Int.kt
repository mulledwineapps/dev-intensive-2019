package ru.skillbranch.devintensive.extensions

import android.content.res.Resources
import kotlin.math.roundToInt

fun Int.pxToDp(): Int = (this / Resources.getSystem().displayMetrics.density).roundToInt()

fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).roundToInt()

//fun dp2px(value: Float, context: Context) =
//    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//        value, context.resources.displayMetrics)