package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.IntDef
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.pxToSp
import ru.skillbranch.devintensive.extensions.spToPx
import ru.skillbranch.devintensive.utils.Utils

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : CircleImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val SHOW_INITIAL = 1
        private const val SHOW_IMAGE = 2

        private const val DEFAULT_INITIALS = ""
        private const val DEFAULT_TEXT_COLOR = Color.WHITE
        private const val DEFAULT_TEXT_SIZE = 48

        @State
        private val DEFAULT_STATE = SHOW_INITIAL
    }

    @IntDef(SHOW_INITIAL, SHOW_IMAGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class State

    var initials: String = DEFAULT_INITIALS
        private set

    fun setInitials(firstName: String?, lastName: String?) {
        initials = extractInitials(firstName, lastName)
        updateTextBounds()
        invalidate()
    }

    private var textColor = DEFAULT_TEXT_COLOR
    private var textSize = DEFAULT_TEXT_SIZE

    private var pBackgroundColor = resources.getColor(R.color.color_accent, App.applicationContext().theme)

    @ColorInt
    fun getAvatarBackgroundColor(): Int = backgroundPaint.color
    fun setAvatarBackgroundColor(@ColorInt color: Int) {
        backgroundPaint.color = color
        invalidate()
    }

    var showState = DEFAULT_STATE
        @State get() = field
        set(@State value) {
            if (value != SHOW_INITIAL && value != SHOW_INITIAL) {
                val msg = "Illegal avatar state value: $value, use either SHOW_INITIAL or SHOW_IMAGE constant"
                throw IllegalArgumentException(msg)
            }
            field = value
            invalidate()
        }

    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textBounds = Rect()

    private var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var backgroundBounds = RectF()

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, defStyleAttr, 0)
            initials = a.getString(R.styleable.AvatarImageView_text) ?: DEFAULT_INITIALS
            textColor = a.getColor(R.styleable.AvatarImageView_textColor, DEFAULT_TEXT_COLOR)
            textSize = a.getDimensionPixelSize(R.styleable.AvatarImageView_textSize, DEFAULT_TEXT_SIZE)
            pBackgroundColor = a.getColor(R.styleable.AvatarImageView_avatarBackgroundColor, pBackgroundColor)
            showState = a.getInt(R.styleable.AvatarImageView_view_state, DEFAULT_STATE)

            a.recycle()
        }

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = textColor
        textPaint.textSize = textSize.spToPx()

        updateTextBounds()

        backgroundPaint.color = pBackgroundColor
        backgroundPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateCircleDrawBounds(backgroundBounds)
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("M_AvatarImageView", "onDraw")
        if (showState == SHOW_INITIAL) {
            val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
            canvas?.drawOval(backgroundBounds, backgroundPaint)
            canvas?.drawText(initials, backgroundBounds.centerX(), textBottom, textPaint)
            drawStroke(canvas)
            drawHighlight(canvas)
        } else {
            super.onDraw(canvas)
        }
    }

    @Dimension
    fun getTextSize() = textPaint.textSize.pxToSp()

    fun setTextSize(@Dimension size: Float) {
        textPaint.textSize = size.spToPx()
        updateTextBounds()
        invalidate()
    }

    @ColorInt
    fun getTextColor(): Int = textPaint.color

    fun setTextColor(@ColorInt color: Int) {
        textPaint.color = color
        invalidate()
    }

    private fun extractInitials(fullName: String?): String {
        val (firstName, lastName) = Utils.parseFullName(fullName)
        return extractInitials(firstName, lastName)
    }

    private fun extractInitials(firstName: String?, lastName: String?) =
        Utils.toInitials(firstName, lastName) ?: DEFAULT_INITIALS

    private fun updateTextBounds() {
        textPaint.getTextBounds(initials, 0, initials.length, textBounds)
    }
}