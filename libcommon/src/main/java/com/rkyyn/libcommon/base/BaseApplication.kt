package com.rkyyn.libcommon.base

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.tencent.mmkv.MMKV

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
open class BaseApplication : Application() {


    companion object {
        lateinit var instance: BaseApplication
        fun dismissLoadingDialog(){
            AppManager.getCurrentActivity()?.let {
                if(it is BaseViewBindingActivity<*>){
                    it.hideLoading()
                }
            }
        }
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        Utils.init(this)
        MMKV.initialize(this)
    }


//    private var sApplication: Application? = null
//
//    open fun getApplication(): Application? {
//        if (sApplication == null) {
//            try {
//                sApplication = Class.forName("android.app.ActivityThread")
//                    .getMethod("currentApplication")
//                    .invoke(null, null as Array<Any?>?) as Application
//            } catch (e: IllegalAccessException) {
//                e.printStackTrace()
//            } catch (e: InvocationTargetException) {
//                e.printStackTrace()
//            } catch (e: NoSuchMethodException) {
//                e.printStackTrace()
//            } catch (e: ClassNotFoundException) {
//                e.printStackTrace()
//            }
//        }
//        return sApplication
//    }

}