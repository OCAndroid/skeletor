package skeletor.mask

import android.graphics.*
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.graphics.toRectF
import skeletor.custom.Attributes
import skeletor.util.*
import kotlin.math.absoluteValue

/**
 * Class that defines drawing behaviour for a view.
 */
internal class SkeletorMask(
    val view: View,
    private val attr: Attributes
) {

    private val paint: Paint by lazy { Paint().apply { color = attr.color } }
    private val bitmap: Bitmap by lazy {
        Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ALPHA_8
        )
    }
    private val canvas: Canvas by lazy { Canvas(bitmap) }
    private val lineSpacingPerLine: Float by lazy { attr.lineSpacing / 2 }

    init {
        val paint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = attr.cornerRadius > NUMBER_ZERO
        }
        mask(view, view as ViewGroup, paint)
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun mask(view: View, root: ViewGroup, paint: Paint, parentInverted: Boolean? = null) {
        val viewAttributes = attr.attributesSelector(view, attr)
        val invert = (parentInverted ?: false) || viewAttributes.invert
        when (view) {
            is ViewGroup -> {
                maskViewGroup(view, root, paint, invert, viewAttributes)
                view.children().forEach { v -> mask(v, root, paint, invert) }
            }
            is TextView -> {
                maskTextView(view, root, invert)
            }
            else -> {
                maskView(view, root, paint, viewAttributes, invert)
            }
        }
    }

    private fun maskViewGroup(view: View, root: ViewGroup, paint: Paint, invert: Boolean, viewAttributes: Attributes) {
        val rect = Rect().also {
            view.getDrawingRect(it)
            root.offsetDescendantRectToMyCoords(view, it)
        }

        val xfermode = paint.xfermode
        if (!(invert || viewAttributes.invert)) {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        canvas.drawRoundRect(RectF(rect), viewAttributes.cornerRadius, viewAttributes.cornerRadius, paint)
        paint.xfermode = xfermode
    }

    private fun maskView(view: View, root: ViewGroup, paint: Paint, viewAttributes: Attributes, inverse: Boolean) {
        val rect = Rect().also {
            view.getDrawingRect(it)
            root.offsetDescendantRectToMyCoords(view, it)
        }.toRectF()

        val cutoutPaint =
            if (inverse) Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
            else paint

        canvas.drawRoundRect(rect, viewAttributes.cornerRadius, viewAttributes.cornerRadius, cutoutPaint)
    }

    private fun maskTextView(
        view: TextView,
        root: ViewGroup,
        inverse: Boolean
    ) {
        val rect = Rect().also {
            view.getDrawingRect(it)
            root.offsetDescendantRectToMyCoords(view, it)
        }
        val textPaint = view.paint.apply { isAntiAlias = attr.cornerRadius > NUMBER_ZERO }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            maskStaticLayout(view, rect, textPaint, inverse)
        } else {
            maskLineWrapping(view, rect, textPaint, inverse)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun maskStaticLayout(
        view: TextView,
        rect: Rect,
        textPaint: TextPaint,
        inverse: Boolean
    ) {
        val spannable =
            spannable {
                background(
                    attr.color,
                    attr.cornerRadius,
                    lineSpacingPerLine,
                    view.text,
                    inverse
                )
            }
        val staticLayout = StaticLayout.Builder
            .obtain(
                spannable,
                0,
                spannable.length,
                textPaint.apply { color = Color.TRANSPARENT },
                view.width
            )
            .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
            .setIncludePad(view.includeFontPadding)
            .setMaxLines(view.lineCount)
            .build()
        canvas.save()
        canvas.translate(rect.left.toFloat(), rect.top.toFloat())
        staticLayout.draw(canvas)
        canvas.restore()
    }

    private fun maskLineWrapping(
        view: TextView,
        rect: Rect,
        textPaint: TextPaint,
        inverse: Boolean
    ) {
        if (view.lineCount.isZero()) return
        val measuredWidth = floatArrayOf(0F)
        var startIndex = 0
        var count: Int
        var lineIndex = 0
        while (startIndex < view.text.length) {
            count = textPaint.breakText(
                view.text,
                startIndex,
                view.text.length,
                true,
                view.width.toFloat(),
                measuredWidth
            )
            val topOffset = view.height * lineIndex / view.lineCount
            val bottomOffset =
                view.height * (lineIndex - (view.lineCount - NUMBER_ONE)).absoluteValue / view.lineCount
            val top = rect.top.toFloat() + (topOffset + lineSpacingPerLine)
            val bottom = rect.bottom.toFloat() - (bottomOffset + lineSpacingPerLine)
            val right = rect.left.toFloat() + measuredWidth[0]
            val rectF = RectF(
                rect.left.toFloat(),
                top,
                if (right > rect.right * WRAPPING_LIMIT) rect.right.toFloat() else right,
                bottom
            )
            val xfermode = textPaint.xfermode
            if (inverse) {
                textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
            canvas.drawRoundRect(rectF, attr.cornerRadius, attr.cornerRadius, textPaint)
            textPaint.xfermode = xfermode
            startIndex += count
            lineIndex++
        }
    }
}