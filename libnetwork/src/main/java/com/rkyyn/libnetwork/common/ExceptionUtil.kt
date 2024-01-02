package com.rkyyn.libnetwork.common

import com.rkyyn.libcommon.base.BaseApplication
import com.rkyyn.libnetwork.R
import com.rkyyn.libnetwork.utils.ToastUtils.Companion.showToast

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
object ExceptionUtil {


    /**
     * 处理异常，toast提示错误信息
     */
    fun catchException(e: Throwable) {
        e.printStackTrace()
//        when (e) {
//            is HttpException -> {
//                catchHttpException(e.code())
//            }
//            is ConnectException -> showToast(
//                R.string.common_error_net
//            )
//            is SocketTimeoutException -> showToast(
//                R.string.common_error_net_time_out
//            )
//            is UnknownHostException, is NetworkErrorException -> showToast(
//                R.string.common_error_net
//            )
//            is MalformedJsonException, is JsonSyntaxException -> showToast(
//                R.string.common_error_server_json
//            )
//            // 接口异常
//            is ApiException -> showToast(
//                e.msg,
//                e.errorCode
//            )
//            else -> {
//                showToast(
////                    "${
////                        BaseApplication.instance.getString(
////                            R.string.common_error_do_something_fail
////                        )
////                    }：${e::class.java.name}"
//                    R.string.common_error_net
//                )
//            }
//        }
        BaseApplication.dismissLoadingDialog()
    }

    /**
     * 处理网络异常
     */
    fun catchHttpException(errorCode: Int) {
        if (errorCode in 200 until 300) return// 成功code则不处理
        showToast(
            catchHttpExceptionCode(
                errorCode
            ), errorCode
        )
    }

    /**
     * 处理网络异常
     */
    private fun catchHttpExceptionCode(errorCode: Int): Int = when (errorCode) {
        in 500..600 -> R.string.common_error_server
        in 400 until 500 -> R.string.common_error_request
        else -> R.string.common_error_request
    }
}
