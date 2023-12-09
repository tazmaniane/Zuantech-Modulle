package id.zuantech.module.view.multimedia.picker

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.File

enum class PickerContentSource {
    UNKNOWN, GALLERY, CAMERA
}

enum class PickerContentType {
    UNKNOWN, IMAGE, VIDEO, PICKER
}

enum class ImageVideoPickerViewMode {
    NEW, EDITABLE, NONEDITABLE
}

@Keep
data class PickerSource(
    @SerializedName("source") val source: PickerContentSource,
    @SerializedName("title") val title: String,
    @SerializedName("icon") val icon: Int? = null,
)

@Keep
data class PickerType(
    @SerializedName("type") val type: PickerContentType,
    @SerializedName("title") val title: String,
    @SerializedName("icon") val icon: Int? = null,
)

@Keep
data class PickerFile(
    @SerializedName("type") val type: PickerContentType,
    @SerializedName("path") val path: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("file") val file: File? = null,
)