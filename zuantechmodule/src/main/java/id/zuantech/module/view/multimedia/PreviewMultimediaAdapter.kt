package id.zuantech.module.view.multimedia

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.annotations.SerializedName
import id.zuantech.module.R
import io.github.vejei.carouselview.CarouselAdapter
import java.io.File
import java.io.Serializable

enum class PreviewType {
    UNKNOWN, IMAGE, VIDEO
}

@Keep
data class PreviewMultimedia(
    @SerializedName("items") val items: List<PreviewMultimediaItem>,
) : Serializable

@Keep
data class PreviewMultimediaItem(
    @SerializedName("position") val position: Int = 0,
    @SerializedName("url") val url: String,
    @SerializedName("filepath") val filepath: String,
    @SerializedName("previewType") val previewType: PreviewType = PreviewType.UNKNOWN
) : Serializable

class PreviewMultimediaAdapter(
    private val activity: Activity,
    private val items: List<PreviewMultimediaItem>,
    private val onClickItem: ((PreviewMultimediaItem) -> Unit)?
) : CarouselAdapter<PreviewMultimediaAdapter.MyViewHolder>() {

    private val TAG = this::class.java.simpleName

    override fun onCreatePageViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder? {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.preview_multimedia_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindPageViewHolder(holder: MyViewHolder, position: Int) {
        val data = items[position]
        holder.imageView.visibility = GONE
        holder.lineVideo.visibility = GONE
        when(data.previewType){
            PreviewType.VIDEO -> {
                if(!data.url.isNullOrEmpty()) {
////                    holder.videoView.setMediaController(MediaController(activity))
//                    holder.videoView.setVideoURI(Uri.parse(data.url))
//                    holder.videoView.requestFocus()
//                    holder.videoView.setOnPreparedListener {
//                        it.start()
//                        it.stop()
//                    }
                    holder.lineVideo.visibility = VISIBLE
                } else if(!data.filepath.isNullOrEmpty()) {
//                    val uri: Uri = Uri.fromFile(File(data.filepath))
////                    holder.videoView.setMediaController(MediaController(activity))
//                    holder.videoView.setVideoURI(uri)
//                    holder.videoView.requestFocus()
//                    holder.videoView.setOnPreparedListener {
//                        it.start()
//                        it.stop()
//                    }
                    holder.lineVideo.visibility = VISIBLE
                }
            }

            PreviewType.IMAGE -> {
                if(!data.url.isNullOrEmpty()) {
                    Glide.with(activity)
                        .load(data.url)
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.imageView)
                    holder.imageView.visibility = VISIBLE
                } else if(!data.filepath.isNullOrEmpty()) {
                    Glide.with(activity)
                        .load(File(data.filepath))
                        .thumbnail(0.1f)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(holder.imageView)
                    holder.imageView.visibility = VISIBLE
                }
            }

            else -> { }
        }

        holder.imageView.setOnClickListener { onClickItem?.let { it1 -> it1(data) } }
        holder.btnPlayVideo.setOnClickListener { onClickItem?.let { it1 -> it1(data) } }

//        holder.ivImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.sample_banner_1))
////        Glide.with(context)
////            .load(data.url)
////            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
////            .skipMemoryCache(true)
////            .thumbnail(0.1f)
////            .into(holder.ivImage)
//        holder.cardView.setOnClickListener {
//            onClick(data)
//        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView
//        var videoView: VideoView
        var lineVideo: View
        var btnPlayVideo: View

        init {
            imageView = view.findViewById(R.id.imageView)
//            videoView = view.findViewById(R.id.videoView)
            lineVideo = view.findViewById(R.id.lineVideo)
            btnPlayVideo = view.findViewById(R.id.btnPlayVideo)
        }
    }

    override fun getPageCount(): Int {
        return items.size
    }
}