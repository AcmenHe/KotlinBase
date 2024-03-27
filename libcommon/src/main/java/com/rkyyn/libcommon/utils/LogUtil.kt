package com.rkyyn.libcommon.utils

import android.Manifest
import android.util.Log
import com.rkyyn.libcommon.base.BaseApplication
import com.rkyyn.libcommon.utils.FileUtil.createSDFile
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 *  author : hepeng
 *  time   : 2023/05/25
 *  desc   :
 */
object LogUtil {
    private var isPrint = true // 是否需要打印bug
    private var isWriteFile :Boolean?= null // 是否需要写入文件
    private var clearLogFileTime = -1L //清除多少秒前的本地日志文件
    var isPermissionWrite = false //是否有读写权限


    private val TAG = "=====LogByApp====="
    val logSavePath:String?
    get() {
       return if(BaseApplication.instance.externalCacheDir !=null) BaseApplication.instance.externalCacheDir!!.path + "/log/" else null
    }

    /**
     * 初始化
     */
//    fun initLog(context: Context): Boolean {
//        isPrint = try {
//            val info = context.applicationInfo
//            info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
//        } catch (x: Exception) {
//            false
//        }
//        return isPrint
//    }

    private val mPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    /**
     * @param isPrintLog 是否打印日志
     * @param writeFile 是否将日志全部写入本地文件中
     * @param clearTime 清理多少秒之前的本地日志文件（单位秒）
     */
    fun initLog(isPrintLog: Boolean= true,writeFile:Boolean? = null,clearTime:Long = -1) {
        isPrint = isPrintLog
        isWriteFile = writeFile
        isPermissionWrite = PermissonUtil.lacksPermissions(BaseApplication.instance, mPermissions)
        clearLogFileTime = clearTime
        clearLogFile()
    }

    /**
     * 清除日志文件
     */
    fun clearLogFile(){
        logSavePath?.let {
            if(clearLogFileTime >-1){
                FileUtil.removeFileByTime(it, clearLogFileTime)
            }else{
                FileUtil.removeFileByTime(it)
            }
        }
    }

    private fun isPrint(): Boolean {
        return isPrint
    }

    //region 日志打印
    // 下面四个是默认tag的函数
    @JvmStatic
    fun i(msg: String?,writeFile:Boolean? = null) {
        i(TAG,msg,writeFile)
    }
    @JvmStatic
    fun d(msg: String?,writeFile:Boolean? = null) {
        d(TAG,msg,writeFile)
    }
    @JvmStatic
    fun e(msg: String?,writeFile:Boolean? = null) {
        e(TAG,msg,writeFile)
    }
    @JvmStatic
    fun v(msg: String?,writeFile:Boolean? = null) {
        v(TAG,msg,writeFile)
    }
    @JvmStatic
    fun i(tag: String?, msg: String?) {
        i(tag, msg,null)
    }
    @JvmStatic
    fun d(tag: String?, msg: String?) {
        i(tag, msg,null)
    }
    @JvmStatic
    fun e(tag: String?, msg: String?) {
        i(tag, msg,null)
    }
    @JvmStatic
    fun v(tag: String?, msg: String?) {
        i(tag, msg,null)
    }
    // 下面是传入自定义tag的函数
    @JvmStatic
    fun i(tag: String?, msg: String?,writeFile:Boolean? = null) {
        if (isPrint()) {
            if (msg != null) {
                Log.i(tag, msg)
            }else{
                Log.i(tag, "null")
            }
            if((isWriteFile ?: writeFile) == true) writeLog("$tag:$msg")
        }
    }

    @JvmStatic
    fun d(tag: String?, msg: String?,writeFile:Boolean? = null) {
        if (isPrint()){
            if (msg != null) {
                Log.d(tag, msg)
            }else{
                Log.d(tag, "null")
            }
            if((isWriteFile ?: writeFile) == true) writeLog("$tag:$msg")
        }
    }

    @JvmStatic
    fun e(tag: String?, msg: String?,writeFile:Boolean? = null) {
        if (isPrint()){
            if (msg != null) {
                Log.e(tag, msg)
            }else{
                Log.e(tag, "null")
            }
            if((isWriteFile ?: writeFile) == true) writeLog("$tag:$msg")
        }
    }

    @JvmStatic
    fun v(tag: String?, msg: String?,writeFile:Boolean? = null) {
        if (isPrint()){
            if (msg != null) {
                Log.v(tag, msg)
            }else{
                Log.v(tag, "null")
            }
            if((isWriteFile ?: writeFile) == true) writeLog("$tag:$msg")
        }
    }
    //endregion

    /**
     * 写日志到文件
     * @param sLog
     */
    @JvmStatic
    fun writeLog(sLog: String) {
        if(logSavePath ==null || logSavePath!!.isEmpty() ) return
        if(!isPermissionWrite){
            isPermissionWrite =
                PermissonUtil.lacksPermissions(BaseApplication.instance, mPermissions)
            if(!isPermissionWrite) return
        }
        var sLog = sLog
        var sFileName = ""
        val date = Date() // 获取当前日期对象
        val formatFile = SimpleDateFormat("yyyyMMdd") // 设置格式
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss:SSS"
        ) // 设置格式
        sFileName = formatFile.format(date).toString()
        sFileName = "klog$sFileName.txt"

        val path = logSavePath + "/" + sFileName
        val file = File(path)
        if (!file.exists()) {
            createSDFile(sFileName, logSavePath!!)
        }
        try {
            val buf = BufferedWriter(FileWriter(file, true))
            sLog = format.format(date).toString() + " " + sLog
            buf.append(sLog)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}