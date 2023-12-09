package id.zuantech.module.view.multimedia.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.zuantech.module.R
import id.zuantech.module.databinding.ImagePickerViewBinding
import id.zuantech.module.databinding.PopupViewBinding
import id.zuantech.module.ext.hideKeyboard
import id.zuantech.module.ext.saveImageToStorage
import id.zuantech.module.view.multimedia.*
import id.zuantech.module.view.multimedia.PreviewMultimediaActivity.Companion.POSITION
import id.zuantech.module.view.multimedia.PreviewMultimediaActivity.Companion.PREVIEW_MULTIMEDIA_DATA
import java.io.File
import java.util.concurrent.Executors


class ImageVideoPickerView : FrameLayout {

    companion object {
        val TAG = ImageVideoPickerView::class.java.simpleName
    }

    private var mBinding = ImagePickerViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var mPopupWindow: PopupWindow? = null

    private var adapter: PickerFileAdapter? = null

    private var activity: Activity? = null
    fun with(activity: Activity): ImageVideoPickerView {
        this.activity = activity
        return this
    }

    var onClickAddImageOrVideo: ((PickerType) -> Unit)? = null
    fun onClickAddImageOrVideo(onClickAddImageOrVideo: ((PickerType) -> Unit)): ImageVideoPickerView {
        this.onClickAddImageOrVideo = onClickAddImageOrVideo
        return this
    }

    var onFilesChanged: (() -> Unit)? = null
    fun onFilesChanged(onFilesChanged: (() -> Unit)): ImageVideoPickerView {
        this.onFilesChanged = onFilesChanged
//        mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
        return this
    }

    var pickerContentType = PickerContentType.UNKNOWN
    fun pickerContentType(pickerContentType: PickerContentType): ImageVideoPickerView {
        this.pickerContentType = pickerContentType
        return this
    }

    var viewMode = ImageVideoPickerViewMode.EDITABLE
    fun viewMode(viewMode: ImageVideoPickerViewMode): ImageVideoPickerView {
        this.viewMode = viewMode
        return this
    }

    var grid = 1
    fun grid(grid: Int) : ImageVideoPickerView {
        this.grid = grid
        return this
    }

    var isWithPreviewMultiMedia = false
    fun isWithPreviewMultiMedia(isWithPreviewMultiMedia: Boolean) : ImageVideoPickerView {
        this.isWithPreviewMultiMedia = isWithPreviewMultiMedia
        return this
    }

    fun getSelectedFiles(): List<PickerFile> {
        return adapter?.items ?: listOf()
    }

    fun setSelectedFiles(data: List<PickerFile>) {
        adapter?.items = data as MutableList<PickerFile>
//        mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
    }

    fun getFileCount() : Int {
        return adapter?.itemCount ?: 0
    }

    fun deleteAllFile(){
        adapter?.items?.let {
            it.forEach { data ->
                if(data.file != null && data.file.exists()) data.file.delete()
            }
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initComponent()
    }

    private fun initComponent(){
        if (!this.isInEditMode) {
//            mBinding.emptyView.setDescription(showEmoteIcon = false, description = "Tidak ada Foto / Video")
        }
    }

    fun init(){
        mBinding.recyclerView.layoutManager = GridLayoutManager(context, grid)
        adapter = PickerFileAdapter(viewMode)
            .onClickPreview { pickerFile, position ->

                if(isWithPreviewMultiMedia){
                    val previewMultimediaItems = mutableListOf<PreviewMultimediaItem>()
                    adapter?.items?.let { data ->
                        for(i in 0 until data.size)
                            previewMultimediaItems.add(
                                PreviewMultimediaItem(
                                    position = i, url = data[i].url,
                                    filepath = data[i].path,
                                    previewType = when(data[i].type){
                                        PickerContentType.IMAGE -> PreviewType.IMAGE
                                        PickerContentType.VIDEO -> PreviewType.VIDEO
                                        else -> PreviewType.UNKNOWN
                                    }
                                )
                            )
                    }
                    activity?.let { activity ->
                        val intent = Intent(activity, PreviewMultimediaActivity::class.java)
                        intent.putExtra(PREVIEW_MULTIMEDIA_DATA, PreviewMultimedia(items = previewMultimediaItems))
                        intent.putExtra(POSITION, position)
                        activity.startActivity(intent)
                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                } else {
                    when(pickerFile.type){
                        PickerContentType.IMAGE -> {
                            val intent: Intent? = if(!pickerFile.url.isNullOrEmpty()) PreviewImageActivity.getIntent(context, url = pickerFile.url)
                            else if(!pickerFile.path.isNullOrEmpty()) PreviewImageActivity.getIntent(context, file = File(pickerFile.path))
                            else if(pickerFile.file != null) PreviewImageActivity.getIntent(context, file = pickerFile.file)
                            else null
                            context.startActivity(intent)
                            (context as Activity?)?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                        PickerContentType.VIDEO -> {
                            val intent: Intent? = if(!pickerFile.url.isNullOrEmpty()) PreviewVideoActivity.getIntent(context, url = pickerFile.url)
                            else if(!pickerFile.path.isNullOrEmpty()) PreviewVideoActivity.getIntent(context, file = File(pickerFile.path))
                            else if(pickerFile.file != null) PreviewVideoActivity.getIntent(context, file = pickerFile.file)
                            else null
                            context.startActivity(intent)
                            (context as Activity?)?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                        else -> { }
                    }
                }
            }
            .onClickRemove {
                onFilesChanged?.let { it() }
//                mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
            }
        mBinding.recyclerView.adapter = adapter
//        mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
        initPopupWindow()
    }

    fun addFile(){
        mPopupWindow?.let { popup ->
            if (popup.isShowing) {
                popup.dismiss()
            } else {
                popup.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
            }
            hideKeyboard()
        }
    }

    fun addImageFile(file: File?){
        if(file != null && file.exists()){
            Log.w(TAG, "file: ${file.absolutePath}")
            val excutor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            excutor.execute {
                val bitmapImage = BitmapFactory.decodeFile(file.absolutePath)
                handler.post {
                    if(bitmapImage != null){
                        val finalFile = bitmapImage.saveImageToStorage(
                            filename = "${context.getString(R.string.app_name)}_${System.currentTimeMillis()}"
                        )
                        if(finalFile != null && finalFile.exists()){
                            Log.w(TAG, "filePath: ${finalFile.absolutePath}")
                            val pickerFile = PickerFile(type = PickerContentType.IMAGE, path = finalFile.absolutePath, file = finalFile)
                            adapter?.addPickerFile(pickerFile)
                            onFilesChanged?.let { it() }
//                            mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
                        }
                    }
                }
            }
        }
    }

    fun addVideoFile(file: File?){
        if(file != null && file.exists()) {
            val pickerFile = PickerFile(type = PickerContentType.VIDEO, path = file.absolutePath, file = file)
            adapter?.addPickerFile(pickerFile)
            onFilesChanged?.let { it() }
//            mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
        }
    }

    private fun initPopupWindow() {
        val popupViewBinding = PopupViewBinding.inflate(LayoutInflater.from(context))
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        mPopupWindow = PopupWindow(popupViewBinding.root, width, height, focusable)

        setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                mPopupWindow?.dismiss()
            }
        }

        val recyclerView: RecyclerView = popupViewBinding.root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PopupContentTypeAdapter {
            onClickAddImageOrVideo?.let { it1 -> it1(it) }
            mPopupWindow?.dismiss()
        }
        recyclerView.adapter = adapter

        val contenttypes = mutableListOf<PickerType>()

        contenttypes.add(PickerType(type = PickerContentType.UNKNOWN, title = "Pilih Tipe", null))
        if(pickerContentType != PickerContentType.UNKNOWN) {
            if(pickerContentType == PickerContentType.IMAGE) {
                contenttypes.add(PickerType(type = PickerContentType.IMAGE, title = "Image", R.drawable.ic_gallery))
            } else {
                contenttypes.add(PickerType(type = PickerContentType.VIDEO, title = "Video", R.drawable.ic_video))
            }
        } else {
            contenttypes.add(PickerType(type = PickerContentType.IMAGE, title = "Image", R.drawable.ic_gallery))
            contenttypes.add(PickerType(type = PickerContentType.VIDEO, title = "Video", R.drawable.ic_video))
        }

        adapter.items = contenttypes
    }
}