package com.rkyyn.libcommon.base

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.lang.ref.WeakReference
import java.util.*

/**
 *  author : hepeng
 *  time   : 2022/09/14
 *  desc   :
 */
object AppManager  {
    private val mActivities = Stack<Activity>()
//    companion object{
//        private var instance:AppManager ?= null
//        @Synchronized
//        fun getInstance(): AppManager{
//            return instance ?:AppManager()
//        }
//
//    }

    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity?{
        var activity = sCurrentActivityWeakRef?.get()
        if(activity ==null && !mActivities.isEmpty()){
            activity = mActivities.peek()
        }
        return activity
    }

    fun setCurrentActivity(activity: Activity) {
        sCurrentActivityWeakRef = WeakReference(activity)
    }
    /**
     * 添加activity
     */
    fun addActivity(activity: Activity?) {
        mActivities.add(activity)
        activity?.let {
            setCurrentActivity(it)
        }
    }

    /**
     * 删除堆栈某个activity
     */
    fun removeActivity(activity: Activity) {
        hideSoftKeyBoard(activity)
        activity.finish()
        mActivities.remove(activity)
    }

    /**
     * 删除堆栈所有activity
     */
    fun removeAllActivity() {
        for (activity in mActivities) {
            hideSoftKeyBoard(activity)
            activity.finish()
        }
        mActivities.clear()
    }

    /**
     * 判断该activity是否存在堆栈
     */
    fun <T : Activity?> hasActivity(tClass: Class<T>): Boolean {
        for (activity in mActivities) {
            if (tClass.name == activity.javaClass.name) {
                return !activity.isDestroyed || !activity.isFinishing
            }
        }
        return false
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyBoard(activity: Activity) {
        val localView = activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (localView != null) {
            imm.hideSoftInputFromWindow(localView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}