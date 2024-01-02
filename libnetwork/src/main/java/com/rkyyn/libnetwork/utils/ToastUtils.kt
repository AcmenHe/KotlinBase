package com.rkyyn.libnetwork.utils

import androidx.annotation.StringRes
import com.rkyyn.libcommon.base.BaseApplication
import com.rkyyn.libcommon.utils.ToastUtils

/**
 *  author : hepeng
 *  time   : 2023/11/22
 *  desc   :
 */
class ToastUtils {
    companion object{
        /**
         * toast提示
         */
        fun showToast(@StringRes errorMsg: Int, errorCode: Int = -1) {
            showToast(
                BaseApplication.instance.getString(
                    errorMsg
                ), errorCode
            )
        }

        /**
         * toast提示
         */
        fun showToast(errorMsg: String, errorCode: Int = -1) {
            if (errorCode == -1) {
                ToastUtils.showShort(errorMsg)
            } else {
                ToastUtils.showShort("$errorCode：$errorMsg")
            }
        }
    }


}