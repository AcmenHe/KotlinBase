package com.rkyyn.libnetwork

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.rkyyn.libnetwork.bean.BaseResult
import com.rkyyn.libnetwork.common.CustomGsonResponseBodyConverter

/**
 *  author : hepeng
 *  time   : 2022/09/07
 *  desc   : 网络访问配置类
 */
open class ApiConfig {

    /**
     * 自定义请求头
     */
    open fun getHeader(): Map<String, String> ?= null
    /**
     * 自定义数据接收的基类
     */
    open fun getResultClass():Class<out BaseResult>?=null

    /**
     * 根据返回值做统一处理
     */
    open fun dealCode(result:BaseResult){}

    /**
     * 自定义Gson转换返回值
     */
    open fun  getMyResponseBodyConverter(gson:Gson,adapter: TypeAdapter<out Any>):CustomGsonResponseBodyConverter<*>?{
        return null
    }

    /**
     * 是否使用缓存
     */
    open fun useCache(): Boolean = true

}