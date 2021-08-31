package skeletor.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import skeletor.SkeletonLoader
import skeletor.annotation.ExperimentalSkeletorApi
import skeletor.skeleton.RecyclerViewSkeleton
import skeletor.skeleton.Skeleton
import skeletor.skeleton.TextViewSkeleton
import skeletor.skeleton.ViewSkeleton
import skeletor.target.ViewTarget
import skeletor.util.skeletorManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred

@OptIn(ExperimentalSkeletorApi::class)
internal class DelegateService(
    private val imageLoader: SkeletonLoader
) {

    fun createTargetDelegate(
        skeleton: Skeleton
    ): TargetDelegate {
        return ViewTargetDelegate(skeleton, skeleton.target)
    }

    @MainThread
    fun createSkeletonDelegate(
        skeleton: Skeleton,
        targetDelegate: TargetDelegate,
        lifecycle: Lifecycle,
        mainDispatcher: CoroutineDispatcher,
        deferred: Deferred<*>
    ): SkeletonDelegate? {
        val skeletonDelegate: SkeletonDelegate
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> when (val target = skeleton.target) {
                is ViewTarget<*> -> {
                    skeletonDelegate = ViewTargetSkeletonDelegate(
                        imageLoader = imageLoader,
                        skeleton = skeleton,
                        target = targetDelegate,
                        lifecycle = lifecycle,
                        dispatcher = mainDispatcher,
                        job = deferred
                    )
                    lifecycle.addObserver(skeletonDelegate)
                    target.view.skeletorManager.setCurrentSkeleton(skeletonDelegate)
                }
                else -> {
                    skeletonDelegate = BaseRequestDelegate(lifecycle, mainDispatcher, deferred)
                    lifecycle.addObserver(skeletonDelegate)
                }
            }
        }
        return skeletonDelegate
    }
}
