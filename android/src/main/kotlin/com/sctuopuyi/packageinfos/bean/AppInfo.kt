package com.sctuopuyi.packageinfos.bean

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class AppInfo(
        @SerializedName("packageName") val packageName: String,
        @SerializedName("name") val name: String,
        @SerializedName("packagePath") val packagePath: String,
        @SerializedName("versionName") val versionName: String,
        @SerializedName("versionCode") val versionCode: Int,
        @SerializedName("isSystem") val isSystem: Boolean,
        @SerializedName("isRunning") val isRunning: Boolean
)