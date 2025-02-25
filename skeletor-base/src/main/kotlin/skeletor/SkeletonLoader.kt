@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "unused")
@file:OptIn(ExperimentalSkeletorApi::class)

package skeletor

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Px
import com.facebook.shimmer.Shimmer
import skeletor.annotation.ExperimentalSkeletorApi
import skeletor.custom.SkeletorView
import skeletor.skeleton.Skeleton
import skeletor.target.Target
import skeletor.util.getColorCompat

interface SkeletonLoader {

    /**
     * The default options that are used to fill in unset [Skeleton] values.
     */
    val defaults: DefaultSkeletonOptions

    /**
     * Launch an asynchronous operation that loads the [Skeleton] and sets the result on its [Target].
     *
     * @param skeleton The skeleton to load.
     */
    fun load(skeleton: Skeleton)

    /**
     * Generate the [Skeleton]
     *
     * @param skeleton The skeleton to generate.
     * @return the [SkeletorView] that contains the skeleton
     */
    fun generate(skeleton: Skeleton): SkeletorView

    /**
     * Hide and cancel any skeleton attached to [view].
     *
     * @param view The original view
     * @param skeletorView The skeleton view loaded
     */
    fun hide(view: View, skeletorView: SkeletorView)

    class Builder(context: Context) {

        private val applicationContext = context.applicationContext
        private var defaults = DefaultSkeletonOptions()

        /**
         * Set the radius in pixels of the corners of the skeleton.
         */
        fun cornerRadius(@Px radius: Float) = apply {
            this.defaults = this.defaults.copy(cornerRadius = radius)
        }

        /**
         * Set the skeleton color.
         */
        fun colorInt(@ColorInt color: Int) = apply {
            this.defaults = this.defaults.copy(color = color)
        }

        /**
         * Set the skeleton color.
         */
        fun color(@ColorRes colorRes: Int) = apply {
            colorInt(applicationContext.getColorCompat(colorRes))
        }

        /**
         * Set the skeleton shimmer.
         */
        fun shimmer(enable: Boolean) = apply {
            this.defaults = this.defaults.copy(isShimmerEnabled = enable)
        }

        /**
         * Set the skeleton shimmer.
         */
        fun shimmer(shimmer: Shimmer) = apply {
            this.defaults = this.defaults.copy(shimmer = shimmer)
        }

        /**
         * Set the total number of items in the skeleton adapter.
         */
        fun itemCount(itemCount: Int) = apply {
            this.defaults = this.defaults.copy(itemCount = itemCount)
        }

        /**
         * Set the space between each line of the skeleton associated with a TextView.
         */
        fun lineSpacing(@Px lineSpacing: Float) = apply {
            this.defaults = this.defaults.copy(lineSpacing = lineSpacing)
        }

        /**
         * Create a new [SkeletonLoader] instance.
         */
        fun build(): SkeletonLoader {
            return MainSkeletonLoader(
                context = applicationContext,
                defaults = defaults
            )
        }
    }

    companion object {
        /** Create a new [SkeletonLoader] without configuration. */
        @JvmStatic
        @JvmName("create")
        inline operator fun invoke(context: Context) = Builder(context).build()
    }
}
