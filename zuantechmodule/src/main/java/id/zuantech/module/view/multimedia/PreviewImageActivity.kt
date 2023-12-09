package id.zuantech.module.view.multimedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import id.zuantech.module.base.BaseActivity
import id.zuantech.module.databinding.PreviewImageActivityBinding
import java.io.File

class PreviewImageActivity : BaseActivity() {

    companion object {
        val TAG = PreviewImageActivity::class.java.simpleName
        val IMAGE_FILE = "file"
        val IMAGE_URL = "url"

        fun getIntent(context: Context, file: File): Intent {
            return Intent(context, PreviewImageActivity::class.java)
                .putExtra(IMAGE_FILE, file.absolutePath)
        }

        fun getIntent(context: Context, url: String): Intent {
            return Intent(context, PreviewImageActivity::class.java)
                .putExtra(IMAGE_URL, url)
        }
    }

    private val mBinding by lazy { PreviewImageActivityBinding.inflate(layoutInflater) }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

//        mBinding.loadingView.isShow(true)

        var file: File? = null
        if (!intent?.extras?.getString(IMAGE_FILE).isNullOrEmpty()) {
            val path: String = intent?.extras?.getString(IMAGE_FILE) ?: ""
            file = File(path)
        }
        if(file != null){
            Glide.with(this)
                .asBitmap()
                .load(file)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(object: RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                        return true
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        mBinding.zoomableImageView.setImageBitmap(resource)
                        return true
                    }
                })
                .into(mBinding.zoomableImageView)
        } else {
            val url: String? = intent?.extras?.getString(IMAGE_URL)
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(object: RequestListener<Bitmap?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                            return true
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            mBinding.zoomableImageView.setImageBitmap(resource)
                            return true
                        }
                    })
                    .into(mBinding.zoomableImageView)
            }
        }


    }
}