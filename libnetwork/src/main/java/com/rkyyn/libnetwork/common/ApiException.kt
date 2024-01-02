package com.rkyyn.libnetwork.common

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
class ApiException(val errorCode: Int,val msg: String):Throwable(msg)