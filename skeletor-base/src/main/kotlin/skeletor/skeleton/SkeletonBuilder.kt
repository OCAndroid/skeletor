package skeletor.skeleton

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import com.facebook.shimmer.Shimmer
import skeletor.annotation.BuilderMarker
import skeletor.custom.AttributeSelector
import skeletor.util.getColorCompat
import skeletor.util.self

/** Base class for [ViewSkeleton.Builder] and [RecyclerViewSkeleton.Builder] */
@BuilderMarker
open class SkeletonBuilder<T : SkeletonBuilder<T>> {

    @JvmField protected val context: Context
    @JvmField @Px protected var cornerRadius: Float?
    @JvmField @ColorInt protected var color: Int?
    @JvmField protected var isShimmerEnabled: Boolean?
    @JvmField protected var shimmer: Shimmer?
    @JvmField @Px protected var lineSpacing: Float?
    @JvmField protected var invert: Boolean?
    @JvmField protected var attributeSelector: AttributeSelector?


    constructor(context: Context) {
        this.context = context
        this.color = null
        this.cornerRadius = null
        this.isShimmerEnabled = null
        this.shimmer = null
        this.lineSpacing = null
        this.invert = null
        this.attributeSelector = null
    }

    constructor(skeleton: Skeleton, context: Context) {
        this.context = context
        this.color = skeleton.color
        this.cornerRadius = skeleton.cornerRadius
        this.isShimmerEnabled = skeleton.isShimmerEnabled
        this.shimmer = skeleton.shimmer
        this.lineSpacing = skeleton.lineSpacing
        this.invert = skeleton.invert
        this.attributeSelector = skeleton.attributeSelector
    }

    /**
     * Set a custom attribute selector to customize attributes for specific child views.
     */
    fun attributeSelector(selector: AttributeSelector): T = self {
        this.attributeSelector = selector
    }

    /**
     * Set the radius in pixels of the corners of the skeleton.
     */
    fun cornerRadius(@Px radius: Float): T = self {
        this.cornerRadius = radius
    }

    /**
     * Set the skeleton color.
     */
    fun colorInt(@ColorInt color: Int): T = self {
        this.color = color
    }

    /**
     * Set the skeleton color res.
     */
    fun color(@ColorRes colorRes: Int): T = self {
        colorInt(context.getColorCompat(colorRes))
    }

    /**
     * Invert the skeleton's color.
     */
    fun invert(enable: Boolean): T = self {
        this.invert = enable
    }

    /**
     * Set the skeleton shimmer.
     */
    fun shimmer(enable: Boolean): T = self {
        this.isShimmerEnabled = enable
    }

    /**
     * Set the skeleton shimmer.
     */
    fun shimmer(shimmer: Shimmer): T = self {
        this.shimmer = shimmer
    }

    /**
     * Set the space between each line of the skeleton associated with a TextView.
     */
    fun lineSpacing(@Px lineSpacing: Float): T = self {
        this.lineSpacing = lineSpacing
    }
}