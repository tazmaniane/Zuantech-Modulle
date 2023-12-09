package id.zuantech.module.view.multimedia.picker

import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import id.zuantech.module.R
import id.zuantech.module.databinding.ImagePickerViewItemBinding
import id.zuantech.module.ext.inflate
import id.zuantech.module.ext.toSizeKB
import java.io.File

class PickerFileAdapter(private val viewMode: ImageVideoPickerViewMode) : RecyclerView.Adapter<PickerFileAdapter.ViewHolder>() {

    var items : MutableList<PickerFile> = mutableListOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    fun addPickerFile(data: PickerFile) : PickerFileAdapter {
        Log.w("notifyItemInserted", Gson().toJson(data))
        if(items.isEmpty()){
            items.add(0, data)
            notifyItemInserted(0)
        } else {
            val pos = items.lastIndex
            items.add(pos, data)
            notifyItemInserted(pos)
        }
        displayAddButton()
        return this
    }

    private fun displayAddButton(){
        if(gravity == RecyclerView.HORIZONTAL){
            if(max > 0 && items.size >= (max + 1)) {
                notifyItemChanged(items.lastIndex)
            }
        } else {
            if(max > 0 && max == items.size) {
                notifyItemChanged(items.lastIndex)
            }
        }
    }

    fun updatePickerFile(position: Int, newdata: PickerFile) : PickerFileAdapter {
        items[position].file?.delete()
        items.removeAt(position)
        items.add(position, newdata)
        notifyItemChanged(position)
        return this
    }

    var gravity: Int = RecyclerView.VERTICAL
    fun gravity(gravity: Int) : PickerFileAdapter {
        this.gravity = gravity
        this.items.add(PickerFile(type = PickerContentType.PICKER))
        return this
    }

    var max = 0
    fun max(max: Int): PickerFileAdapter {
        this.max = max
        return this
    }

    var onClickPreview: ((PickerFile, position: Int) -> Unit)? = null
    fun onClickPreview(onClickPreview: ((PickerFile, position: Int) -> Unit)) : PickerFileAdapter {
        this.onClickPreview = onClickPreview
        return this
    }

    var onClickRemove: (() -> Unit)? = null
    fun onClickRemove(onClickRemove: (() -> Unit)) : PickerFileAdapter {
        this.onClickRemove = onClickRemove
        return this
    }

    var onClickEdit: ((position: Int) -> Unit)? = null
    fun onClickEdit(onClickEdit: ((position: Int) -> Unit)) : PickerFileAdapter {
        this.onClickEdit = onClickEdit
        return this
    }

    var onClickPick: ((View) -> Unit)? = null
    fun onClickPick(onClickPick: ((View) -> Unit)) : PickerFileAdapter {
        this.onClickPick = onClickPick
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.image_picker_view_item))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? ViewHolder)?.bind(items[position], position)
    }

    inner class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView){
        private var mBinding = ImagePickerViewItemBinding.bind(containerView)

        fun bind(data: PickerFile, position: Int){
            with(mBinding){
                if(data.type == PickerContentType.PICKER) {
                    if(max > 0 && items.size >= (max + 1)) {
                        lineContent.visibility = GONE
                        cardPicker.visibility = GONE
                    } else {
                        lineContent.visibility = GONE
                        cardPicker.visibility = VISIBLE
                        cardPicker.setOnClickListener { onClickPick?.let { it1 -> it1(it) } }
                    }
                    return
                } else {
                    lineContent.visibility = VISIBLE
                    cardPicker.visibility = GONE
                    when(data.type){
                        PickerContentType.VIDEO -> Glide.with(root.context).load(R.drawable.ic_video).into(ivImageTypeView)
                        PickerContentType.IMAGE -> Glide.with(root.context).load(R.drawable.ic_gallery).into(ivImageTypeView)
                        else -> ivImage.setImageDrawable(null)
                    }
                }

                if(data.file != null) {
                    root.visibility = VISIBLE
                    tvSize.visibility = VISIBLE
                    tvSize.text = data.file.toSizeKB()
                    Glide.with(root.context)
                        .load(data.file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(.1f)
                        .into(ivImage)
                } else if(!data.path.isNullOrEmpty()) {
                    root.visibility = VISIBLE
                    val file = File(data.path)
                    tvSize.visibility = VISIBLE
                    tvSize.text = file.toSizeKB()
                    Glide.with(root.context)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(.1f)
                        .into(ivImage)
                } else if(!data.url.isNullOrEmpty()) {
                    root.visibility = VISIBLE
                    tvSize.visibility = GONE
                    Glide.with(root.context)
                        .load(data.url)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .thumbnail(.1f)
                        .into(ivImage)
                } else {
                    root.visibility = INVISIBLE
                    tvSize.visibility = GONE
                }

                when(viewMode){
                    ImageVideoPickerViewMode.NEW -> {
                        btnRemove.setOnClickListener {
                            data.file?.delete()
                            val pos = items.indexOf(data)
                            items.removeAt(pos)
                            notifyItemRemoved(pos)
                            onClickRemove?.let { it1 -> it1() }
                            notifyItemChanged(items.lastIndex)
                        }
                        btnEdit.visibility = GONE
                        btnRemove.visibility = VISIBLE
                    }
                    ImageVideoPickerViewMode.EDITABLE -> {
//                        if(!data.url.isNullOrEmpty()) {
                            btnEdit.setOnClickListener {
                                val pos = items.indexOf(data)
                                onClickEdit?.let { it1 -> it1(pos) }
                            }
                            btnEdit.visibility = VISIBLE
                            btnRemove.visibility = GONE
//                        } else {
//                            btnRemove.setOnClickListener {
//                                data.file?.delete()
//                                val replacefile = PickerFile(type = data.type, url = "dummyurl")
//                                val pos = items.indexOf(data)
//                                items.removeAt(pos)
//                                items.add(pos, replacefile)
//                                notifyItemChanged(pos)
//                                onClickRemove?.let { it1 -> it1() }
//                            }
//                            btnEdit.visibility = GONE
//                            btnRemove.visibility = VISIBLE
//                        }
                    }
                    else -> {
                        btnEdit.visibility = GONE
                        btnRemove.visibility = GONE
                    }
                }

                btnPreview.setOnClickListener {
                    onClickPreview?.let { it1 -> it1(data, position) }
                }
            }
        }
    }
}