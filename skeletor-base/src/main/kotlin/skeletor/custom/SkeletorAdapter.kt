package skeletor.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import skeletor.util.generateSimpleSkeletorView

internal class SkeletorAdapter(
    @LayoutRes private val layoutResId: Int,
    private val itemCount: Int,
    private val attributes: Attributes
) : RecyclerView.Adapter<SkeletorAdapter.SkeletorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletorViewHolder {
        val originView = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val skeleton = originView.generateSimpleSkeletorView(attributes)
        return SkeletorViewHolder(skeleton.also { it.showSkeleton() })
    }

    override fun onBindViewHolder(holder: SkeletorViewHolder, position: Int) = Unit

    override fun getItemCount() = itemCount

    internal class SkeletorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
