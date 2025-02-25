package skeletor.memory

import android.view.ViewTreeObserver
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import skeletor.custom.SkeletorView
import skeletor.util.isMainThread
import skeletor.util.isMeasured
import skeletor.util.notNull
import skeletor.util.removeOnGlobalLayoutListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


internal class ViewTargetSkeletonManager : ViewTreeObserver.OnGlobalLayoutListener {

    private var currentSkeleton: ViewTargetSkeletonDelegate? = null

    private var currentSkeletorView: SkeletorView? = null

    private var pendingBlock: () -> Unit = {}

    @Volatile
    private var pendingClear: Job? = null

    @Volatile
    var currentSkeletonId: UUID? = null
        private set

    @Volatile
    var currentSkeletonJob: Job? = null
        private set

    private var isRestart = false
    private var skipAttach = true

    @MainThread
    fun setCurrentSkeletorView(skeletorView: SkeletorView?) {
        currentSkeletorView = skeletorView
    }

    /** Attach [skeleton] to this view and dispose the old skeleton. */
    @MainThread
    fun setCurrentSkeleton(skeleton: ViewTargetSkeletonDelegate?) {
        if (isRestart) {
            isRestart = false
        } else {
            pendingClear?.cancel()
            pendingClear = null
        }

        currentSkeleton?.dispose()
        currentSkeleton = skeleton
        skipAttach = true
    }

    /** Hide and cancel any skeleton attached to this view. */
    @MainThread
    fun hideSkeleton() {
        currentSkeletonId = null
        currentSkeletonJob = null
        pendingClear?.cancel()
        pendingClear = CoroutineScope(Dispatchers.Main.immediate).launch {
            currentSkeleton.notNull { currentSkeletorView.notNull { view -> it.hideSkeleton(view) } }
            setCurrentSkeleton(null)
            setCurrentSkeletorView(null)
        }
        pendingBlock()
        pendingBlock = {}
    }

    /** Returns the visibility of the skeleton. */
    @MainThread
    fun isSkeletonShown(): Boolean {
        return currentSkeletorView?.isSkeletonShown ?: false
    }

    /** Set the current [job] attached to this view and assign it an ID. */
    @AnyThread
    fun setCurrentSkeletonJob(job: Job): UUID {
        val skeletonId = newSkeletonId()
        currentSkeletonId = skeletonId
        currentSkeletonJob = job
        return skeletonId
    }

    /** Return an ID to use for the next skeleton attached to this manager. */
    @AnyThread
    private fun newSkeletonId(): UUID {
        val skeletonId = currentSkeletonId
        if (skeletonId != null && isRestart && isMainThread()) {
            return skeletonId
        }
        return UUID.randomUUID()
    }

    override fun onGlobalLayout() {
        currentSkeleton.notNull { skeleton ->
            skeleton.getViewTarget().notNull {
                if (it.isMeasured()) {
                    it.removeOnGlobalLayoutListener(this)
                    isRestart = true
                    skeleton.restart()
                }
            }
        }
    }

    /** Calls the specified function [block] after the skeleton is hidden. */
    fun afterHide(block: () -> Unit) {
        if (isSkeletonShown()) pendingBlock = block
        else block()
    }
}
