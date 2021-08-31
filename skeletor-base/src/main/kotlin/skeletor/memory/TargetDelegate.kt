@file:OptIn(ExperimentalSkeletorApi::class)

package skeletor.memory

import androidx.annotation.MainThread
import skeletor.annotation.ExperimentalSkeletorApi
import skeletor.custom.SkeletorView
import skeletor.skeleton.Skeleton
import skeletor.target.Target

internal sealed class TargetDelegate {

    @MainThread
    open fun start() {}

    @MainThread
    open fun success(skeleton: SkeletorView) {}

    @MainThread
    open fun error() {}

    @MainThread
    open fun clear() {}
}

internal class ViewTargetDelegate(
    private val skeleton: Skeleton,
    private val target: Target?
) : TargetDelegate() {

    override fun start() {
        target?.onStart()
    }

    override fun success(skeleton: SkeletorView) {
        target?.onSuccess(skeleton)
    }

    override fun error() {
        target?.onError()
    }

    override fun clear() {}
}