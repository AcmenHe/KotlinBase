package com.rkyyn.libcommon.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import androidx.core.content.ContextCompat


/**
 *  author : hepeng
 *  time   : 2023/05/25
 *  desc   :
 */
object PermissonUtil {
    /**
     * 存储权限
     */
    private val getStoragePermissions = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * 相机权限
     */
    fun getCameraPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        } else arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /**
     * 判断是否缺少权限
     */
    private fun lacksPermission(mContexts: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED
    }

    /**
     * 判断权限集合
     * permissions 权限数组
     * return false-表示没有改权限  true-表示权限已开启
     */
    fun lacksPermissions(mContexts: Context, mPermissions: Array<String>): Boolean {
        mPermissions.forEach {
            if (lacksPermission(mContexts, it)) {
                //没有开启权限
                return false
            }
        }
        //权限已开启
        return true
    }
}