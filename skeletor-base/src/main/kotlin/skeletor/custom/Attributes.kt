package skeletor.custom

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer

typealias AttributeSelector = (View, Attributes) -> Attributes

/**
 * Attributes to be applied while generating the skeleton layout.
 *
 * @param color - ColorInt to use for the skeleton.
 * @param cornerRadius - Float value to be used to round the corners for the skeleton.
 * @param isShimmerEnabled - Flag to enable shimmer effect.
 * @param shimmer - Settings for shimmer effect. See: [com.facebook.shimmer.Shimmer]
 * @param lineSpacing - Float value that controls the lines spacing.
 * @param invert - Boolean indicating if the skeleton colors should be inverted. Inverted skeletons color ViewGroups.
 * @param attributesSelector - Function that allows inspection of views to customize a child view's attributes.
 */
sealed class Attributes {
    abstract val color: Int
    abstract val cornerRadius: Float
    abstract val isShimmerEnabled: Boolean
    abstract val shimmer: Shimmer
    abstract val lineSpacing: Float
    open val invert: Boolean = false
    abstract val attributesSelector: AttributeSelector
}

data class RecyclerViewAttributes(
    val view: RecyclerView,
    @ColorInt override val color: Int,
    @Px override val cornerRadius: Float,
    override val isShimmerEnabled: Boolean,
    override val shimmer: Shimmer,
    override val lineSpacing: Float,
    @LayoutRes val itemLayout: Int,
    val itemCount: Int,
    override val invert: Boolean = false,
    private val attributesForView: AttributeSelector? = null
) : Attributes() {
    override val attributesSelector: AttributeSelector
        get() = { view, attr -> attributesForView?.invoke(view, attr) ?: this }
}

data class SimpleViewAttributes(
    @ColorInt override val color: Int,
    @Px override val cornerRadius: Float,
    override val isShimmerEnabled: Boolean,
    override val shimmer: Shimmer,
    override val lineSpacing: Float,
    override val invert: Boolean = false,
    private val attributesForView: AttributeSelector? = null
) : Attributes() {
    override val attributesSelector: AttributeSelector
        get() = { view, attr-> attributesForView?.invoke(view, attr) ?: this }
}

data class TextViewAttributes(
    val view: TextView,
    @ColorInt override val color: Int,
    @Px override val cornerRadius: Float,
    override val isShimmerEnabled: Boolean,
    override val shimmer: Shimmer,
    override val lineSpacing: Float,
    val length: Int,
    override val invert: Boolean = false,
    private val attributesForView: AttributeSelector? = null
) : Attributes() {
    override val attributesSelector: AttributeSelector
        get() = { view, attr-> attributesForView?.invoke(view, attr) ?: this }
}