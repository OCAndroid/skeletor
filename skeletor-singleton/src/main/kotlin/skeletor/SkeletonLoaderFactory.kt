package skeletor

import android.app.Application


/**
 * A factory that creates new [SkeletonLoader] instances.
 *
 * To configure how the default [SkeletonLoader] is created **either**:
 * - Implement [SkeletonLoaderFactory] in your [Application].
 * - **Or** call [Skeletor.setSkeletonLoader] with your [SkeletonLoaderFactory].
 */
interface SkeletonLoaderFactory {

    /**
     * Return a new [SkeletonLoader].
     */
    fun newSkeletonLoader(): SkeletonLoader
}
