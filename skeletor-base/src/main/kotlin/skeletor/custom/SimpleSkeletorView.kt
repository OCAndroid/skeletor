package skeletor.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import skeletor.mask.SkeletorMask
import skeletor.util.*

internal class SimpleSkeletorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkeletorView(context, attrs, defStyleAttr) {

    private var skeletorMask: SkeletorMask? = null
    override var isSkeletonShown: Boolean = false
    private var isMeasured: Boolean = false
    private val viewList = arrayListOf<View>()

    var attributes: Attributes? = null
        set(value) {
            field = value
            value?.let { applyAttributes() }
        }

    private fun hideVisibleChildren(view: View) {
        when (view) {
            is ViewGroup -> view.children().forEach { hideVisibleChildren(it) }
            else -> hideVisibleChild(view)
        }
    }

    private fun hideVisibleChild(view: View) {
        if (view.isVisible()) {
            view.invisible()
            viewList.add(view)
        }
    }

    override fun showSkeleton() {
        isSkeletonShown = true
        if (isMeasured && childCount > 0) {
            hideVisibleChildren(this)
            applyAttributes()
        }
    }

    override fun hideSkeleton() {
        isSkeletonShown = false
        if (childCount > 0) {
            viewList.forEach { it.visible() }
            hideShimmer()
            skeletorMask = null
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isMeasured = width > NUMBER_ZERO && height > NUMBER_ZERO
        if (isSkeletonShown) {
            showSkeleton()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { skeletorMask?.draw(it) }
    }

    override fun applyAttributes() {
        if (isMeasured) {
            attributes?.let { attrs ->
                if (attrs !is SimpleViewAttributes || !attrs.isShimmerEnabled) {
                    hideShimmer()
                } else {
                    setShimmer(attrs.shimmer)
                }
                skeletorMask = SkeletorMask(this, attrs)
            }
        }
    }

}