package com.sctuopuyi.packageinfos

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.sctuopuyi.packageinfos.bean.AppInfo
import com.sctuopuyi.packageinfos.updateApk.CheckUpdateRequest
import com.sctuopuyi.packageinfos.updateApk.Updater
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File


class PackageinfosPlugin : MethodCallHandler {

    private val MOTION_SENSOR = "MOTION_SENSOR"
    private var applicationContext: Context? = null
    private var methodChannel: MethodChannel? = null
    private var apkFileUrl: String? = null
    private var packageTag: String? = null
    private var registrar: Registrar? = null
    private var result: Result? = null
//    override fun onAttachedToEngine(p0: FlutterPlugin.FlutterPluginBinding) {
//        this.applicationContext = p0.applicationContext
//    }

//    override fun onDetachedFromEngine(p0: FlutterPlugin.FlutterPluginBinding) {
//        this.applicationContext = null
//    }


    constructor(context: Context, registrar: Registrar) {
        this.applicationContext = context
        this.registrar = registrar
    }


    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "packageinfos")
            channel.setMethodCallHandler(PackageinfosPlugin(registrar.context(), registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        var permission: String? = ""
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android__ ${android.os.Build.VERSION.RELEASE}")
            }
            "getAppInfos" -> {
                //获取新软件的安装列表
                Thread().run {
                    val list = ArrayList<AppInfo>()
                    val pm = applicationContext?.packageManager ?: return result.success(list)
                    val installedPackages = pm.getInstalledPackages(0)
                    val appInfos = installedPackages.asSequence().map { pi ->
                        getBean(pm, pi)
                    }.toList()
                    val objStr = Gson().toJson(appInfos)
                    Log.i("getAppInfos获取信息:", objStr)
                    result.success(
                            objStr
                    )
                }

            }
            "updateApk" -> {
                try {
                    Log.i("updateApk", "开始下载软件包")
                    //更新APK
                    apkFileUrl = call.argument("downloadUrl")
//                    apkFileUrl = "/storage/emulated/0/Android/data/com.sctuopuyi.hcms/files/app-release.apk"
                    Log.i("updateApk", "获取参数成功")
                    val file = File(apkFileUrl)
                    Updater.install(this.applicationContext, file, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                result.success(null)
            }
            "getExternalStorageDirectory" -> {
                try {
                    val path = Environment.getDataDirectory().absolutePath
                    result.success(path)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "getPermissionStatus" -> {
                permission = call.argument("permission")
                this.result = result
                if (MOTION_SENSOR.equals(permission, ignoreCase = true)) {
                    result.success(3)
                } else {
                }
            }
            "checkPermission" -> {
                permission = call.argument<String>("permission")
                if (MOTION_SENSOR.equals(permission, ignoreCase = true)) {
                    result.success(true)
                } else {
                }
            }

            "requestPermission" -> {
                permission = call.argument<String>("permission")
                if (MOTION_SENSOR.equals(permission, ignoreCase = true)) {
                    result.success(3)
                } else {
                    this.result = result
                }
            }
            "openSettings" -> {
                result.success(true)
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    private fun getBean(pm: PackageManager, pi: PackageInfo?): AppInfo? {
        if (pi == null) return null
        val ai = pi.applicationInfo
        val packageName = pi.packageName
        val name = ai.loadLabel(pm).toString()
//        val icon = ai.loadIcon(pm)
        val packagePath = ai.sourceDir
        val versionName = pi.versionName ?: "未知版本"
        val versionCode = pi.versionCode
        val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
        val isRunning = getStatus(packageName, applicationContext!!)
        return AppInfo(packageName, name, packagePath, versionName, versionCode, isSystem, isRunning)
    }


    private fun getStatus(packageName: String, context: Context): Boolean {
        val uid = getPackageUid(packageName, context)
        if (uid > 0) {
            val rstA = isAppRunning(packageName, context)
            val rstB = isProcessRunning(uid, context)
            return rstA || rstB
        }
        return false
    }


    private fun getPackageUid(packageName: String, context: Context): Int {
        try {
            val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            if (applicationInfo != null) {
                return applicationInfo.uid
            }
            return -1
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    private fun isAppRunning(packageName: String, context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(100)
        if (runningTasks.isEmpty()) {
            return false
        }
        for (info in runningTasks) {
            if (info.baseActivity.packageName == packageName) {
                return true
            }
        }
        return false
    }

    private fun isProcessRunning(uid: Int, context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = am.getRunningServices(200)
        if (runningServiceInfos.size > 0) {
            for (appProcess in runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true
                }
            }
        }
        return false
    }


}
