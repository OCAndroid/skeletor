package skeletor.custom

import android.content.Context
import android.util.AttributeSet

internal class RecyclerSkeletorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkeletorView(context, attrs, defStyleAttr) {

    override var isSkeletonShown: Boolean = false

    var attributes: RecyclerViewAttributes? = null
        set(value) {
            field = value
            value?.let { applyAttributes() }
        }

    private var originalAdapter = attributes?.view?.adapter

    private var originalLayoutManager = attributes?.view?.layoutManager

    private var skeletonAdapter: SkeletorAdapter? = null

    override fun hideSkeleton() {
        isSkeletonShown = false
        hideShimmer()
        attributes?.run {
            view.adapter = originalAdapter
            view.layoutManager = originalLayoutManager
        }
    }

    override fun showSkeleton() {
        isSkeletonShown = true
        attributes?.view?.adapter = skeletonAdapter
    }

    override fun applyAttributes() {
        attributes?.run {
            originalAdapter = view.adapter
            skeletonAdapter = SkeletorAdapter(itemLayout, itemCount, this)

            originalLayoutManager = view.layoutManager
            layoutManager?.let { view.layoutManager = it }

            if (!isShimmerEnabled) hideShimmer() else setShimmer(shimmer)

            if (isSkeletonShown) {
                showSkeleton()
            }
        }
    }
}