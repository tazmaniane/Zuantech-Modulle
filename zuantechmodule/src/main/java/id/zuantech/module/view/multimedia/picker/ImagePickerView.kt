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
import id.zuantech.module.view.multimedia.PreviewImageActivity
import java.io.File
import java.util.concurrent.Executors


class ImagePickerView : FrameLayout {

    companion object {
        val TAG = ImagePickerView::class.java.simpleName
    }

    private var mBinding = ImagePickerViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var adapter: PickerFileAdapter? = null
    private var mPopupWindow: PopupWindow? = null

    private var activity: Activity? = null
    fun with(activity: Activity): ImagePickerView {
        this.activity = activity
        return this
    }

    var onFilesChanged: (() -> Unit)? = null
    fun onFilesChanged(onFilesChanged: (() -> Unit)): ImagePickerView {
        this.onFilesChanged = onFilesChanged
//        mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
        return this
    }

    var onClickEdit: ((position: Int) -> Unit)? = null
    fun onClickEdit(onClickEdit: ((position: Int) -> Unit)) : ImagePickerView {
        this.onClickEdit = onClickEdit
        return this
    }

    var onClickPick: ((PickerSource) -> Unit)? = null
    fun onClickPick(onClickPick: ((PickerSource) -> Unit)) : ImagePickerView {
        this.onClickPick = onClickPick
        return this
    }

    var viewMode = ImageVideoPickerViewMode.NEW
    fun viewMode(viewMode: ImageVideoPickerViewMode): ImagePickerView {
        this.viewMode = viewMode
        return this
    }

    var gravity = RecyclerView.VERTICAL
    fun gravity(gravity: Int): ImagePickerView {
        this.gravity = gravity
        return this
    }

    var max = -1
    fun max(max: Int): ImagePickerView {
        this.max = max
        return this
    }

    var grid = 1
    fun grid(grid: Int) : ImagePickerView {
        this.grid = grid
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
//            mBinding.emptyView.setDescription(showEmoteIcon = false, description = "Tidak ada Foto")
        }
    }

    fun init(){
        initPopupWindow()

        if(gravity == RecyclerView.HORIZONTAL){
            mBinding.recyclerView.layoutManager = GridLayoutManager(context, grid, RecyclerView.VERTICAL, false)
        } else {
            mBinding.recyclerView.layoutManager = GridLayoutManager(context, grid)
        }

        adapter = PickerFileAdapter(viewMode)
            .gravity(gravity)
            .max(max)
            .onClickPick { view ->
                mPopupWindow?.let { popup ->
                    if (popup.isShowing) {
                        popup.dismiss()
                    } else {
                        popup.showAtLocation(view, Gravity.BOTTOM, 0, (view.height * 2).toInt())
                    }
                    context.hideKeyboard(view)
                }
            }
            .onClickEdit { onClickEdit?.let { it1 -> it1(it) } }
            .onClickPreview { pickerFile, position ->
                when(pickerFile.type){
                    PickerContentType.IMAGE -> {
                        val intent: Intent? = if(!pickerFile.url.isNullOrEmpty()) PreviewImageActivity.getIntent(context, url = pickerFile.url)
                            else if(!pickerFile.path.isNullOrEmpty()) PreviewImageActivity.getIntent(context, file = File(pickerFile.path))
                            else if(pickerFile.file != null) PreviewImageActivity.getIntent(context, file = pickerFile.file)
                            else null
                        intent?.let {
                            context.startActivity(it)
                            (context as Activity?)?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                    else -> { }
                }
            }
            .onClickRemove {
                onFilesChanged?.let { it() }
//                mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
            }
        mBinding.recyclerView.adapter = adapter
//        mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
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
                            adapter?.let { adapter ->
                                Log.w(TAG, "filePath: ${finalFile.absolutePath}")
                                val pickerFile = PickerFile(type = PickerContentType.IMAGE, path = finalFile.absolutePath, file = finalFile)
                                adapter.addPickerFile(pickerFile)
                                onFilesChanged?.let { it() }
                                mBinding.recyclerView.scrollToPosition(adapter.items.lastIndex)
//                            mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateImageFile(position: Int, file: File?){
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
                            adapter?.updatePickerFile(position, pickerFile)
                            onFilesChanged?.let { it() }
//                            mBinding.emptyView.isShow(adapter?.items.isNullOrEmpty())
                        }
                    }
                }
            }
        }
    }

    private fun initPopupWindow() {
        val popupViewBinding = PopupViewBinding.inflate(LayoutInflater.from(context))
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        mPopupWindow = PopupWindow(popupViewBinding.root, width, height, focusable)

        setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                mPopupWindow?.dismiss()
            }
        }

        val recyclerView: RecyclerView = popupViewBinding.root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PopupContentSourceAdapter {
            onClickPick?.let { it1 -> it1(it) }
            mPopupWindow?.dismiss()
        }
        recyclerView.adapter = adapter
        val contentsources = mutableListOf<PickerSource>()
        contentsources.add(PickerSource(source = PickerContentSource.UNKNOWN, title = "Pilih Foto dari", null))
        contentsources.add(PickerSource(source = PickerContentSource.CAMERA, title = "Kamera", R.drawable.ic_camera_new))
        contentsources.add(PickerSource(source = PickerContentSource.GALLERY, title = "Galeri", R.drawable.ic_gallery))
        adapter.items = contentsources
    }

}