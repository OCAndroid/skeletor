package skeletor.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import skeletor.lifecycle.GlobalLifecycle
import skeletor.lifecycle.LifecycleCoroutineDispatcher
import skeletor.skeleton.RecyclerViewSkeleton
import skeletor.skeleton.Skeleton
import skeletor.skeleton.TextViewSkeleton
import skeletor.skeleton.ViewSkeleton
import skeletor.target.Target
import skeletor.target.ViewTarget
import skeletor.util.getLifecycle
import skeletor.util.isNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SkeletonService {

    @MainThread
    fun lifecycleInfo(skeleton: Skeleton): LifecycleInfo {
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> {
                val lifecycle = skeleton.getLifecycle()
                return if (lifecycle != null) {
                    val mainDispatcher = LifecycleCoroutineDispatcher
                        .createUnlessStarted(Dispatchers.Main.immediate, lifecycle)
                    LifecycleInfo(lifecycle, mainDispatcher)
                } else {
                    LifecycleInfo.GLOBAL
                }
            }
        }
    }

    private fun Skeleton.getLifecycle(): Lifecycle? {
        return when {
            lifecycle.isNotNull() -> lifecycle
            this is ViewSkeleton || this is RecyclerViewSkeleton -> target?.getLifecycle()
            else -> context.getLifecycle()
        }
    }

    private fun Target.getLifecycle(): Lifecycle? {
        return (this as? ViewTarget<*>)?.view?.context?.getLifecycle()
    }

    data class LifecycleInfo(
        val lifecycle: Lifecycle,
        val mainDispatcher: CoroutineDispatcher
    ) {

        companion object {
            val GLOBAL = LifecycleInfo(
                lifecycle = GlobalLifecycle,
                mainDispatcher = Dispatchers.Main.immediate
            )
        }
    }
}