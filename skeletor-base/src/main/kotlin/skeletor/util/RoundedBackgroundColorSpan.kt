package skeletor.util

import android.graphics.*
import android.text.style.LineBackgroundSpan
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

internal class RoundedBackgroundColorSpan(
    @ColorInt private val color: Int,
    private val cornerRadius: Float,
    private val lineSpacingPerLine: Float,
    private val inverse: Boolean
) : LineBackgroundSpan {

    private val rect = Rect()

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        val paintColor = paint.color
        val xfermode = paint.xfermode

        paint.color = color
        if (inverse) {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        val width = paint.measureText(text, start, end).roundToInt()
        val rightWrapping = left + width
        rect.set(
            left,
            top + lineSpacingPerLine.toInt(),
            if (rightWrapping > right * WRAPPING_LIMIT) right else rightWrapping,
            bottom - lineSpacingPerLine.toInt()
        )
        paint.isAntiAlias = cornerRadius > NUMBER_ZERO
        canvas.drawRoundRect(RectF(rect), cornerRadius, cornerRadius, paint)

        paint.color = paintColor
        paint.xfermode = xfermode
    }
}