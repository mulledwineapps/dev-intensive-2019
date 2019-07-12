package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    val view = this.currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

//val Activity.isKeyboardOpen: Boolean
    //get() = keyboardIsOpen(this)

//val Activity.isKeyboardClosed: Boolean
    //get() = !keyboardIsOpen(this)

fun Activity.isKeyboardClosed() : Boolean = this.isKeyboardOpen().not()

// https://github.com/ravindu1024/android-keyboardlistener/blob/master/keyboard-listener/src/main/java/com/rw/keyboardlistener/KeyboardUtils.java
fun Activity.isKeyboardOpen(): Boolean {
    val rootView = this.findViewById(android.R.id.content) as ViewGroup
    val screenDensity = this.resources.displayMetrics.density

    val r = Rect()
    rootView.getWindowVisibleDisplayFrame(r)

    val heightDiff = rootView.rootView.height - (r.bottom - r.top)
    val dp = heightDiff / screenDensity
    return dp > 200
}