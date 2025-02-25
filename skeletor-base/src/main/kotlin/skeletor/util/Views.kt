package skeletor.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewParent
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import skeletor.base.R
import skeletor.custom.*
import skeletor.custom.RecyclerSkeletorView
import skeletor.custom.SimpleSkeletorView
import skeletor.custom.TextSkeletorView
import skeletor.memory.ViewTargetSkeletonManager

internal fun View.visible() {
    visibility = View.VISIBLE
}

internal fun View.invisible() {
    visibility = View.INVISIBLE
}

internal fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

internal fun View.gone() {
    visibility = View.GONE
}

internal fun View.getParentView(): ViewParent {
    return checkNotNull(parent) { "The view has not attach to any view" }
}

internal fun View.getParentViewGroup(): ViewGroup {
    return getParentView() as ViewGroup
}

internal fun ViewGroup.children(): List<View> {
    return (0 until childCount).map { child -> getChildAt(child) }
}

internal fun View.generateSimpleSkeletorView(attributes: Attributes): SimpleSkeletorView {
    val parent = parent as? ViewGroup
    return SimpleSkeletorView(context).also {
        validateBackground()
        it.id = id
        it.layoutParams = layoutParams
        it.cloneTranslations(this)
        parent?.removeView(this)
        ViewCompat.setLayoutDirection(it, ViewCompat.getLayoutDirection(this))
        it.addView(this.lparams(layoutParams))
        parent?.addView(it)
        it.attributes = attributes
    }
}

internal fun View.validateBackground() {
    if (this is FrameLayout) setBackgroundColor(Color.TRANSPARENT)
}

internal fun RecyclerView.generateRecyclerSkeletorView(attributes: RecyclerViewAttributes): RecyclerSkeletorView {
    val parent = parent as? ViewGroup
    return RecyclerSkeletorView(context).also {
        it.id = id
        it.layoutParams = layoutParams
        it.cloneTranslations(this)
        parent?.removeView(this)
        ViewCompat.setLayoutDirection(it, ViewCompat.getLayoutDirection(this))
        it.addView(this.lparams(layoutParams))
        parent?.addView(it)
        it.attributes = attributes
    }
}

internal fun TextView.generateTextSkeletorView(attributes: TextViewAttributes): TextSkeletorView {
    val parent = parent as? ViewGroup
    return TextSkeletorView(context).also {
        it.id = id
        it.layoutParams = layoutParams
        it.cloneTranslations(this)
        parent?.removeView(this)
        ViewCompat.setLayoutDirection(it, ViewCompat.getLayoutDirection(this))
        it.addView(this.lparams(layoutParams))
        parent?.addView(it)
        it.attributes = attributes
    }
}

internal fun <T: View> T.lparams(source: ViewGroup.LayoutParams): T {
    val layoutParams = FrameLayout.LayoutParams(source).apply {
        if (width.isZero()) {
            width = if (this@lparams.width.isZero() && source is ConstraintLayout.LayoutParams) MATCH_PARENT
            else this@lparams.width
        }
        if (height.isZero()) {
            height = if (this@lparams.height.isZero() && source is ConstraintLayout.LayoutParams) MATCH_PARENT
            else this@lparams.height
        }
    }
    this@lparams.layoutParams = layoutParams
    return this
}

internal fun View.removeOnGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        @Suppress("DEPRECATION")
        this.viewTreeObserver.removeGlobalOnLayoutListener(listener)
    } else {
        this.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}

internal fun View.isMeasured() = layoutParams.width == WRAP_CONTENT || layoutParams.height == WRAP_CONTENT || (measuredWidth > 0 && measuredHeight > 0)

internal val View.skeletorManager: ViewTargetSkeletonManager
    get() {
        var manager = getTag(R.id.skeletor_manager) as? ViewTargetSkeletonManager
        if (manager == null) {
            manager = ViewTargetSkeletonManager().apply {
                viewTreeObserver.addOnGlobalLayoutListener(this)
                setTag(R.id.skeletor_manager, this)
            }
        }
        return manager
    }

internal fun View.cloneTranslations(view: View) {
    translationX = view.translationX
    translationY = view.translationY
    view.clearTranslations()
}

internal fun View.clearTranslations() {
    translationX = 0f
    translationY = 0f
}