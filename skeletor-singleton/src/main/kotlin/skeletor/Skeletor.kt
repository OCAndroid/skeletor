@file:Suppress("NOTHING_TO_INLINE", "unused")

package skeletor

import android.app.Application
import android.content.Context
import skeletor.skeleton.ViewSkeleton

/**
 * A singleton that holds the default [SkeletonLoader] instance.
 */
object Skeletor {

    private var skeletonLoader: SkeletonLoader? = null
    private var skeletonLoaderFactory: SkeletonLoaderFactory? = null

    /**
     * Get the default [SkeletonLoader]. Creates a new instance if none has been set.
     */
    @JvmStatic
    fun skeletonLoader(context: Context): SkeletonLoader = skeletonLoader ?: newSkeletonLoader(context)

    /**
     * Set the default [SkeletonLoader]. Shutdown the current instance if there is one.
     */
    @JvmStatic
    fun setSkeletonLoader(loader: SkeletonLoader) {
        setSkeletonLoader(object : SkeletonLoaderFactory {
            override fun newSkeletonLoader() = loader
        })
    }

    /**
     * Convenience function to get the default [SkeletonLoader] and execute the [skeleton].
     *
     * @see SkeletonLoader.load
     */
    @JvmStatic
    inline fun execute(skeleton: ViewSkeleton) {
        return skeletonLoader(skeleton.context).load(skeleton)
    }

    /**
     * Set the [SkeletonLoaderFactory] that will be used to create the default [SkeletonLoader].
     * Shutdown the current instance if there is one. The [factory] is guaranteed to be called at most once.
     *
     * Using this method to set an explicit [factory] takes precedence over an [Application] that
     * implements [SkeletonLoaderFactory].
     */
    @JvmStatic
    @Synchronized
    fun setSkeletonLoader(factory: SkeletonLoaderFactory) {
        skeletonLoaderFactory = factory

        // Shutdown the skeleton loader after clearing the reference.
        skeletonLoader = null
    }

    /** Create and set the new default [SkeletonLoader]. */
    @Synchronized
    private fun newSkeletonLoader(context: Context): SkeletonLoader {
        // Check again in case skeletonLoader was just set.
        skeletonLoader?.let { return it }

        // Create a new SkeletonLoader.
        val loader = skeletonLoaderFactory?.newSkeletonLoader()
            ?: (context.applicationContext as? SkeletonLoaderFactory)?.newSkeletonLoader()
            ?: SkeletonLoader(context)
        skeletonLoaderFactory = null
        setSkeletonLoader(loader)
        return loader
    }
}
