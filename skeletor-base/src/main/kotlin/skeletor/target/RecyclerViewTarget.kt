package skeletor.target

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import skeletor.custom.SkeletorView

/** A [Target] that handles setting skeleton on a [RecyclerView]. */
open class RecyclerViewTarget(override val view: RecyclerView) : ViewTarget<RecyclerView>, DefaultLifecycleObserver {

    override fun onStart() = Unit

    /** Show the [skeleton] view */
    override fun onSuccess(skeleton: SkeletorView) = skeleton.showSkeleton()

    override fun onError() = Unit

    override fun onStart(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}
}
