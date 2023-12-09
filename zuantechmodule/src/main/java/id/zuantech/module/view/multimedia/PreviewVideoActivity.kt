package id.zuantech.module.view.multimedia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import id.zuantech.module.databinding.PreviewVideoActivityBinding
import java.io.File


class PreviewVideoActivity : AppCompatActivity() {

    companion object {
        val TAG = PreviewVideoActivity::class.java.simpleName
        val VIDEO_FILE = "file"
        val VIDEO_URL = "url"

        fun getIntent(context: Context, file: File): Intent {
            return Intent(context, PreviewVideoActivity::class.java)
                .putExtra(VIDEO_FILE, file.absolutePath)
        }

        fun getIntent(context: Context, url: String): Intent {
            return Intent(context, PreviewVideoActivity::class.java)
                .putExtra(VIDEO_URL, url)
        }
    }

    private val mBinding by lazy { PreviewVideoActivityBinding.inflate(layoutInflater) }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
//        mBinding.loadingView.isShow(false)

        var file: File? = null
        if (!intent?.extras?.getString(VIDEO_FILE).isNullOrEmpty()) {
            val path: String = intent?.extras?.getString(VIDEO_FILE) ?: ""
            file = File(path)
        }

        mBinding.videoView.setOnErrorListener { mp, what, extra ->
//            PopupError(this)
//                .popupMessage("Video tidak dapat diputar")
//                .onClickPositive { onClickBack() }
//                .show()
            true
        }

//        mBinding.videoView.setOnCompletionListener {
//            onClickBack()
//        }

//        mBinding.loadingView.isShow(true)

        if (file != null) {
            Log.w(TAG, "file: ${file.absolutePath}")
            val uri: Uri = Uri.fromFile(file)
            mBinding.videoView.setMediaController(MediaController(this))
            mBinding.videoView.setVideoURI(uri)
            mBinding.videoView.requestFocus()
            mBinding.videoView.setOnPreparedListener {
                it.start()
//                mBinding.loadingView.isShow(!it.isPlaying)
            }
        } else {
            val url: String? = intent?.extras?.getString(VIDEO_URL)
            Log.w(TAG, "url: $url")
            if(url != null){
                mBinding.videoView.setMediaController(MediaController(this))
                mBinding.videoView.setVideoURI(Uri.parse(url))
                mBinding.videoView.requestFocus()
                mBinding.videoView.setOnPreparedListener {
                    it.start()
//                    mBinding.loadingView.isShow(!it.isPlaying)
                }
            }
        }

    }

    override fun onDestroy() {
        mBinding.videoView.pause()
        mBinding.videoView.suspend()
        super.onDestroy()
    }
}