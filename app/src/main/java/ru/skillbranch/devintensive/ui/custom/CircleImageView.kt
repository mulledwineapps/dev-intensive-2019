package ru.skillbranch.devintensive.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// https://github.com/abdularis/CircularImageView
open class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private val DEFAULT_BORDER_WIDTH = 2.dpToPx
        private const val DEFAULT_HIGHLIGHT_COLOR = 0x32000000
        private const val DEFAULT_HIGHLIGHT_ENABLE = true
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH
    private var highlightColor = DEFAULT_HIGHLIGHT_COLOR
    private var highlightEnable: Boolean = DEFAULT_HIGHLIGHT_ENABLE

    private lateinit var bitmapShader: Shader
    private var shaderMatrix: Matrix = Matrix()

    private var bitmapDrawBounds: RectF = RectF()
    private var strokeBounds: RectF = RectF()

    private var bitmap: Bitmap? = null

    private var bitmapPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var strokePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pressedPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var initialized: Boolean = true
    private var pPressed: Boolean = false

    // TODO использовать этот класс, а не аватар!

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            highlightEnable = a.getBoolean(R.styleable.CircleImageView_highlightEnable, DEFAULT_HIGHLIGHT_ENABLE)
            highlightColor = a.getColor(R.styleable.CircleImageView_highlightColor, DEFAULT_HIGHLIGHT_COLOR)
            a.recycle()
        }

        strokePaint.color = borderColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = borderWidth.toFloat()

        pressedPaint.color = highlightColor
        pressedPaint.style = Paint.Style.FILL

        setupBitmap()
    }

    fun getBorderWidth(): Int = borderWidth

    fun setBorderWidth(dp: Int) {
        borderWidth = dp.dpToPx
    }

    fun getBorderColor(): Int {
        return 0
    }

    fun setBorderColor(hex: String) {
        // val strColor = String.format("#%06X", 0xFFFFFF & color)
        borderColor = Color.parseColor(hex)
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = strokePaint.strokeWidth / 2f
        updateCircleDrawBounds(bitmapDrawBounds)
        strokeBounds.set(bitmapDrawBounds)
        strokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()

        outlineProvider = CircleImageViewOutlineProvider(strokeBounds)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var processed = false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isInCircle(event.x, event.y)) return false
                processed = true
                pPressed = true
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            MotionEvent.ACTION_UP -> {
                processed = true
                pPressed = false
                invalidate()
                if (!isInCircle(event.x, event.y)) return false
            }
        }
        return super.onTouchEvent(event) || processed
    }

    //  onDraw will be called automatically by the framework when we call invalidate() or when it needs to be redrawn.
    override fun onDraw(canvas: Canvas?) {
        drawBitmap(canvas)
        drawStroke(canvas)
        drawHighlight(canvas)
    }

    protected fun drawBitmap(canvas: Canvas?) {
        canvas?.drawOval(bitmapDrawBounds, bitmapPaint)
    }

    protected fun drawStroke(canvas: Canvas?) {
        if (strokePaint.strokeWidth > 0f) {
            canvas?.drawOval(strokeBounds, strokePaint)
        }
    }

    protected fun drawHighlight(canvas: Canvas?) {
        if (highlightEnable && pPressed) {
            canvas?.drawOval(bitmapDrawBounds, pressedPaint)
        }
    }

    protected fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        var left: Float = paddingLeft.toFloat()
        var top: Float = paddingRight.toFloat()

        // we'll center bounds by translating left/top
        // so that the rendered circle always in the center of view
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        if (initialized.not() || drawable == null) return
        bitmap = getBitmapFromDrawable(drawable)
        val bm = bitmap ?: return

        bitmapShader = BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.shader = bitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        val bm = bitmap ?: return

        val scale: Float
        val dx: Float
        val dy: Float

        if (bm.width < bm.height) {
            scale = bitmapDrawBounds.width() / bm.width
            dx = bitmapDrawBounds.left
            dy = bitmapDrawBounds.top - (bm.height * scale / 2f) + (bitmapDrawBounds.width() / 2f)
        } else {
            scale = bitmapDrawBounds.height() / bm.height
            dx = bitmapDrawBounds.left - (bm.width * scale / 2f) + (bitmapDrawBounds.width() / 2f)
            dy = bitmapDrawBounds.top
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx, dy)
        bitmapShader.setLocalMatrix(shaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) return drawable.bitmap

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun isInCircle(x: Float, y: Float): Boolean {
        val distance = sqrt((bitmapDrawBounds.centerX() - x).pow(2) + (bitmapDrawBounds.centerY() - y).pow(2))
        return distance <= (bitmapDrawBounds.width() / 2)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class CircleImageViewOutlineProvider(rectF: RectF) : ViewOutlineProvider() {
        private val rect = Rect(
            rectF.left.toInt(),
            rectF.right.toInt(),
            rectF.left.toInt(),
            rectF.bottom.toInt()
        )

        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setOval(rect)
        }

    }
}