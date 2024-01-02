package com.rkyyn.libcommon.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.rkyyn.libcommon.R

abstract class BaseViewBindingFragment<T : ViewBinding> : Fragment() {
    protected var mViewBinding : T ?= null
    protected abstract fun getViewBinding(): T
    protected abstract fun initView()

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewBinding = getViewBinding()
        return mViewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewBinding = null
        hideLoading()
    }

    private var dialog: Dialog? = null

    open fun showLoading() {
        showLoadingDialog(requireActivity(),"")
    }

    /**
     * 显示加载
     */
    @Synchronized
    open fun showLoadingDialog(context: Context, title: String?) {
        try {
            val requireActivity = requireActivity()
            if(requireActivity is BaseViewBindingActivity<*>){
                requireActivity.showLoadingDialog(context,title)
                return
            }
//                if (dialog == null) {
//                dismissLoadingDialog()
            dialog = Dialog(context, R.style.dialog)
            dialog!!.setContentView(R.layout.dialog_loading)
            //            dialog.setCancelable(false);
            dialog!!.setCanceledOnTouchOutside(false)
//                }
            if (!TextUtils.isEmpty(title)) {
                val titleView = dialog!!.findViewById<View>(R.id.text) as TextView
                if (titleView != null) {
                    titleView.text = title
                }
            }
            dialog!!.show()
        } catch (e: Exception) {
            LogUtils.e(e.message)
        }
    }

    @Synchronized
    open fun hideLoading(){
        try {
            val requireActivity = requireActivity()
            if(requireActivity is BaseViewBindingActivity<*>){
                requireActivity.hideLoading()
                return
            }
            if (null != dialog) {
                dialog!!.dismiss()
                dialog = null
            }
        } catch (e: java.lang.Exception) {
            LogUtils.e(e.message)
        }
    }


    /**
     * 含有Bundle通过Class跳转界面
     */
    open fun startActivity(cls: Class<*>, bundle: Bundle?= null) {
        val intent = Intent(requireContext(), cls!!)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

}