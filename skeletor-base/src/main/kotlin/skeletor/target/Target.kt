package skeletor.target

import androidx.annotation.MainThread
import skeletor.custom.SkeletorView

/**
 * A listener that accepts the result of a view skeleton.
 */
interface Target {

    /**
     * Called when the skeleton starts.
     */
    @MainThread
    fun onStart() {}

    /**
     * Called if the skeleton completes successfully.
     */
    @MainThread
    fun onSuccess(skeleton: SkeletorView) {}

    /**
     * Called if an error occurs while loading the skeleton.
     */
    @MainThread
    fun onError() {}
}
