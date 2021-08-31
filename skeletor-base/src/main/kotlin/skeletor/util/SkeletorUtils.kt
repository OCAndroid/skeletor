package skeletor.util

import android.view.View

object SkeletorUtils {

    /**
     * Hide and cancel any skeleton attached to [view].
     */
    @JvmStatic
    fun hide(view: View) {
        view.skeletorManager.hideSkeleton()
    }

    /**
     * Calls the specified function [block] after the skeleton attached to [view] is hidden.
     */
    @JvmStatic
    fun afterHide(view: View, block: () -> Unit) {
        view.skeletorManager.afterHide(block)
    }

    /**
     * @return True if the [view] is hidden by the skeleton.
     */
    @JvmStatic
    fun isSkeletonShown(view: View): Boolean {
        return view.skeletorManager.isSkeletonShown()
    }
}