package com.rkyyn.libnetwork.bean

/**
 *  author : hepeng
 *  time   : 2022/09/07
 *  desc   : 公共返回值基类
 */
abstract class BaseResult  {
    //后端返回的code
    abstract fun getResultCode():Int
    //后端返回的信息
    abstract fun getResultMessage():String?
    //接口请求是否成功
    abstract fun isSuccess():Boolean
}