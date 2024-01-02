package com.rkyyn.libcommon.utils

import android.widget.Toast
import com.rkyyn.libcommon.base.BaseApplication

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
object  ToastUtils {
    fun showShort(msg: String){
        Toast.makeText(BaseApplication.instance,msg,Toast.LENGTH_SHORT).show()
    }
}