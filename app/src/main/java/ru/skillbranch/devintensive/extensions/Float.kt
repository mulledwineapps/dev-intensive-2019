package ru.skillbranch.devintensive.extensions


import android.content.res.Resources
import android.util.TypedValue

fun Float.pxToSp(): Float = this / Resources.getSystem().displayMetrics.scaledDensity
// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this, Resources.getSystem().displayMetrics)

fun Float.spToPx(): Float = this * Resources.getSystem().displayMetrics.scaledDensity
    // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

//DisplayMetrics dm = new DisplayMetrics();
//getWindowManager().getDefaultDisplay().getMetrics(dm);
//pixelSize = (int)scaledPixelSize * dm.scaledDensity;