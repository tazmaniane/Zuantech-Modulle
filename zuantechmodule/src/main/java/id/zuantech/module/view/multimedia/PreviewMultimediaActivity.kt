package id.zuantech.module.view.multimedia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.zuantech.module.base.BaseActivity
import id.zuantech.module.databinding.PreviewMultimediaActivityBinding
import java.io.File
import java.util.concurrent.TimeUnit


class PreviewMultimediaActivity : BaseActivity() {

    companion object {
        val TAG = PreviewMultimediaActivity::class.java.simpleName
        const val PREVIEW_MULTIMEDIA_DATA = "previewMultimediaData"
        const val POSITION = "position"
    }

    private val mBinding by lazy { PreviewMultimediaActivityBinding.inflate(layoutInflater) }

    private val mPreviewMultimediaData by lazy {
        intent?.getSerializableExtra(PREVIEW_MULTIMEDIA_DATA) as PreviewMultimedia?
    }

    private val mPosition by lazy { intent?.getIntExtra(POSITION, 0) ?: 0 }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initCarouselView()
    }

    private fun initCarouselView(){
        mPreviewMultimediaData?.let { data ->
            mBinding.carouselView.adapter = PreviewMultimediaAdapter(this, data.items){
                when(it.previewType){
                    PreviewType.IMAGE -> {
                        val intent: Intent? = if(!it.url.isNullOrEmpty()) PreviewImageActivity.getIntent(this, url = it.url)
                        else if(!it.filepath.isNullOrEmpty()) PreviewImageActivity.getIntent(this, file = File(it.filepath))
                        else null
                        intent?.let { it1 -> startActivityNormal(it1) }
                    }
                    PreviewType.VIDEO -> {
                        val intent: Intent? = if(!it.url.isNullOrEmpty()) PreviewVideoActivity.getIntent(this, url = it.url)
                        else if(!it.filepath.isNullOrEmpty()) PreviewVideoActivity.getIntent(this, file = File(it.filepath))
                        else null
                        intent?.let { it1 -> startActivityNormal(it1) }
                    }
                    else -> { }
                }
            }
            mBinding.carouselView.start(10, TimeUnit.SECONDS)
            mBinding.carouselView.stop()
            mBinding.indicator.setWithViewPager2(mBinding.carouselView.viewPager2, false)
            mBinding.indicator.itemCount = data.items.size
            if(mPosition != 0){
                mBinding.carouselView.viewPager2.setCurrentItem(mPosition, false)
            }
        }
    }
}