package skeletor

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleObserver
import com.facebook.shimmer.ShimmerFrameLayout
import skeletor.annotation.ExperimentalSkeletorApi
import skeletor.custom.*
import skeletor.memory.DelegateService
import skeletor.memory.SkeletonService
import skeletor.skeleton.RecyclerViewSkeleton
import skeletor.skeleton.Skeleton
import skeletor.skeleton.TextViewSkeleton
import skeletor.skeleton.ViewSkeleton
import skeletor.target.RecyclerViewTarget
import skeletor.target.SimpleViewTarget
import skeletor.target.TextViewTarget
import skeletor.target.ViewTarget
import skeletor.util.*
import kotlinx.coroutines.*

@OptIn(ExperimentalSkeletorApi::class)
internal class MainSkeletonLoader(
    private val context: Context,
    override val defaults: DefaultSkeletonOptions
) : SkeletonLoader {

    companion object {
        private const val TAG = "MainSkeletonLoader"
    }

    private val loaderScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message.orEmpty())
    }

    private val delegateService = DelegateService(this)

    override fun load(skeleton: Skeleton) {
        val job = loaderScope.launch(exceptionHandler) { loadInternal(skeleton) }
        val target = skeleton.target as? ViewTarget<*>
        target?.view?.skeletorManager?.setCurrentSkeletonJob(job)
    }

    override fun generate(skeleton: Skeleton): SkeletorView {
        return generateSkeletorView(skeleton)
    }

    private suspend fun loadInternal(skeleton: Skeleton) =
        withContext(Dispatchers.Main.immediate) {
            val (lifecycle, mainDispatcher) = SkeletonService().lifecycleInfo(skeleton)
            val targetDelegate = delegateService.createTargetDelegate(skeleton)
            val deferred = async(mainDispatcher, CoroutineStart.LAZY) {
                val target = skeleton.target
                if (target is ViewTarget<*> && target is LifecycleObserver) {
                    lifecycle.addObserver(target)
                    with(target.view) {
                        if (parent !is SkeletorView && isMeasured()) {
                            val skeletorView = generateSkeletorView(skeleton)
                            skeletorManager.setCurrentSkeletorView(skeletorView)
                            targetDelegate.success(skeletorView)
                        }
                    }
                }
            }
            val skeletonDelegate = delegateService.createSkeletonDelegate(
                skeleton,
                targetDelegate,
                lifecycle,
                mainDispatcher,
                deferred
            )
            deferred.invokeOnCompletion { throwable ->
                loaderScope.launch(Dispatchers.Main.immediate) { skeletonDelegate?.onComplete() }
            }
            deferred.await()
        }

    override fun hide(view: View, skeletorView: SkeletorView) {
        skeletorView.hideSkeleton()
        val skeletonView = skeletorView as ShimmerFrameLayout
        val originalParams = skeletonView.layoutParams
        val originalParent = skeletonView.getParentViewGroup()
        skeletonView.removeView(view)
        originalParent.removeView(skeletonView)
        view.cloneTranslations(skeletonView)
        originalParent.addView(view, originalParams)
    }

    private fun generateSkeletorView(skeleton: Skeleton): SkeletorView {
        return when (skeleton) {
            is RecyclerViewSkeleton -> generateRecyclerView(skeleton)
            is ViewSkeleton -> generateSimpleView(skeleton)
            is TextViewSkeleton -> generateTextView(skeleton)
        }
    }

    private fun generateTextView(skeleton: TextViewSkeleton) = with(skeleton) {
        return@with if (target is TextViewTarget) {
            val attributes = TextViewAttributes(
                    view = target.view,
                    color = color ?: defaults.color,
                    cornerRadius = cornerRadius ?: defaults.cornerRadius,
                    isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                    shimmer = shimmer ?: defaults.shimmer,
                    lineSpacing = lineSpacing ?: defaults.lineSpacing,
                    length = length,
                    invert = invert ?: defaults.invert
            )
            target.view.generateTextSkeletorView(attributes)
        } else {
            TextSkeletorView(context)
        }
    }

    private fun generateRecyclerView(skeleton: RecyclerViewSkeleton) = with(skeleton) {
        return@with if (target is RecyclerViewTarget) {
            val attributes = RecyclerViewAttributes(
                view = target.view,
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing,
                itemLayout = itemLayoutResId,
                itemCount = itemCount ?: defaults.itemCount,
                invert = invert ?: defaults.invert
            )
            target.view.generateRecyclerSkeletorView(attributes)
        } else {
            RecyclerSkeletorView(context)
        }
    }

    private fun generateSimpleView(skeleton: ViewSkeleton) = with(skeleton) {
        return@with if (target is SimpleViewTarget) {
            val attributes = SimpleViewAttributes(
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing,
                invert = invert ?: defaults.invert
            )
            target.view.generateSimpleSkeletorView(attributes)
        } else {
            SimpleSkeletorView(context)
        }
    }
}