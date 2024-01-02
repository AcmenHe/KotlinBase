package com.rkyyn.libcommon.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.rkyyn.libcommon.R


/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
abstract class BaseViewBindingActivity<T : ViewBinding> : AppCompatActivity() {

    open var useSharedViewModel: Boolean = false
    protected lateinit var mViewBinding: T
    protected abstract fun getViewBinding(): T


    /**
     * 初始化View控件
     */
    abstract fun initView(savedInstanceState: Bundle?)

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.addActivity(this)
        mViewBinding =  getViewBinding()
        setContentView(mViewBinding.root)
        initView(savedInstanceState)
    }

    override fun onDestroy() {
        AppManager.removeActivity(this)
        hideLoading()
        super.onDestroy()
    }



    private var dialog: Dialog? = null

    open fun showLoading() {
        showLoadingDialog(this,"")
    }
    @Synchronized
    open fun hideLoading(){
        try {
            if (null != dialog) {
                dialog!!.dismiss()
                dialog = null
            }
        } catch (e: java.lang.Exception) {
            LogUtils.e(e.message)
        }
    }

    /**
     * 显示加载
     */
    @Synchronized
    open fun showLoadingDialog(context: Context, title: String?) {
        try {
                if (dialog == null) {
            dialog = Dialog(context, R.style.dialog)
            dialog!!.setContentView(R.layout.dialog_loading)
            //            dialog.setCancelable(false);
            dialog!!.setCanceledOnTouchOutside(false)
                }
            if (!TextUtils.isEmpty(title)) {
                val titleView = dialog!!.findViewById<View>(R.id.text) as TextView
                titleView.text = title
            }
            dialog!!.show()
        } catch (e: Exception) {
            LogUtils.e(e.message)
        }
    }


    /**
     * 含有Bundle通过Class跳转界面
     */
    open fun startActivity(cls: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, cls)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

}