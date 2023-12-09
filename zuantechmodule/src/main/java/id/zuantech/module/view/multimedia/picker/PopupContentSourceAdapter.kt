package id.zuantech.module.view.multimedia.picker

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.zuantech.module.R
import id.zuantech.module.databinding.PopupViewItemBinding
import id.zuantech.module.ext.inflate

class PopupContentSourceAdapter(private val onClick:(PickerSource) -> Unit)
    : RecyclerView.Adapter<PopupContentSourceAdapter.ViewHolder>() {

    var items : List<PickerSource> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    var tintColor: Int? = R.color.primary_base
    fun tintColor(tintColor: Int?): PopupContentSourceAdapter {
        this.tintColor = tintColor
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.popup_view_item))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? ViewHolder)?.bind(items[position], position)
    }

    inner class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView){
        private var mBinding = PopupViewItemBinding.bind(containerView)

        fun bind(data: PickerSource, position: Int){
            with(mBinding){
                data.icon?.let { ivImage.setImageDrawable(ContextCompat.getDrawable(root.context, it)) }
                tvTitle.text = data.title
                tvSubtitle.visibility = GONE
                if(items.first() == data){
                    ivImage.visibility = GONE
                    ivImage.setColorFilter(ContextCompat.getColor(root.context, R.color.grey_dark))
                    tvTitle.setTextColor(ContextCompat.getColor(root.context, R.color.grey_dark))
                } else {
                    ivImage.visibility = VISIBLE
                    tvTitle.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                    if(tintColor != null){
                        ivImage.setColorFilter(ContextCompat.getColor(root.context, tintColor!!))
                    } else {
                        ivImage.colorFilter = null
                    }
                    cardView.setOnClickListener {
                        onClick(data)
                    }
                }
                lineView.visibility = if(items.last() == data) GONE else VISIBLE
            }
        }
    }
}