package com.rkyyn.libcommon.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.rkyyn.libcommon.base.BaseApplication
import java.io.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 *  author : hepeng
 *  time   : 2023/05/25
 *  desc   :
 */
object FileUtil {

    /**
     * 获取Cache路径
     */
    @JvmStatic
    fun getCachePath(): String = BaseApplication.instance.externalCacheDir?.path ?: BaseApplication.instance.cacheDir.path
    /**
     * 获取图片路径
     */
    @JvmStatic
    fun getPicturesPath(): String {
        val path = BaseApplication.instance.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val file = File(path)
        if (!file.exists())file.mkdirs()
        return path
    }

    /**
     * 获取下载目录
     * @return 下载目录
     */
    @JvmStatic
    fun getExtDownloadsPath(): String {
        val path = BaseApplication.instance.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
        val file = File(path)
        if (!file.exists())file.mkdirs()
        return path
    }

    /**
     * 获取照片uri
     */
    fun getPhotoUri(context: Context, fileProviderString:String,outputImage:File): Uri {
        //创建file对象，用于存储拍照后的图片；
//        val outputImage = File(FileUtil.getPicturesPath(), "${Date().time}.jpg")
        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                context,
                fileProviderString, outputImage);
        } else {
            Uri.fromFile(outputImage);
        }
    }

    /**
     * 文件创建
     * @param  fileName  文件名称
     * @param  filePath  文件路径
     * @return
     */
    fun createSDFile(fileName:String,filePath: String? = null): File {
        var path = filePath
        if(path ==null){
            path = getCachePath()
        }

        val dir = File(path)
        if(!dir.exists()){
            dir.mkdirs()
        }
        val file = File("${path}//$fileName")
        if (!file.exists()) {
            try {
                //文件夹创建
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    /**
     * 删除文件
     * @param  filePath
     * @return
     */
    fun deleteSDFile(filePath: String): Boolean {
        val file = File("${getCachePath()}//$filePath")
        return if (file == null || !file.exists() || file.isDirectory) false else file.delete()
    }

    /**
     * 本地文件写入
     * @param  str  内容
     * @param  fileName    目录文件
     * @throws  IOException
     */
    fun writeSDFile(str: String?, fileName: String) {
        var fw: FileWriter? = null
        var file: File? = null
        var out: DataOutputStream? = null
        var os: FileOutputStream? = null
        //写入文件具体位置
        try {
            fw = FileWriter("${getCachePath()}//$fileName")
            file = File("${getCachePath()}//$fileName")
            fw.write(str)
            os = FileOutputStream(file)
            out = DataOutputStream(os)
            out.writeShort(2)
            out.writeUTF("utf-8")
            fw.flush()
        } catch (e: IOException) {
            LogUtil.e("writeSDFile", e.message)
        } finally {
            try {
                fw?.close()
                out?.close()
                os?.close()
            } catch (e: IOException) {
                LogUtil.e("writeSDFile", e.message)
            }
        }
    }

    /**
     * 文件资源读取
     * @param  fileName
     * @return
     */
    fun readSDFile(fileName: String): String? {
        val sb = StringBuffer()
        var fis: FileInputStream? = null
        val file = File("${getCachePath()}//$fileName")
        try {
            fis = FileInputStream(file)
            var size: Int
            while (fis.read().also { size = it } != -1) {
                sb.append(size.toChar())
            }
        } catch (e: FileNotFoundException) {
            LogUtil.e("readSDFile", e.message)
        } catch (e: IOException) {
            LogUtil.e("readSDFile", e.message)
        } finally {
            try {
                fis?.close()
            } catch (e: IOException) {
                LogUtil.e("readSDFile", e.message)
            }
        }
        return sb.toString()
    }

    /**
     * 文件拷贝
     * @param  srcFile  源文件
     * @param  destFile  目标文件夹
     * @return
     */
    fun copyFile(srcFile: File, destFile: File): Boolean {
        var fis: FileInputStream? = null
        var fos: FileOutputStream? = null
        try {
            if (srcFile.isDirectory || destFile.isDirectory) return false //  判断是否是文件
            fis = FileInputStream(srcFile) //读
            fos = FileOutputStream(destFile) //写
            var readLen = 0
            val buf = ByteArray(1024)
            while (fis.read(buf).also { readLen = it } != -1) {
                fos.write(buf, 0, readLen)
            }
            fos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                fos?.close()
                fis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }


    /**
     * 获取视频第一帧图片
     * @param file 视频文件
     * @return bitmap
     */
    fun getVideoFirstFrame(file: File): Bitmap? {
        return try {
            if (!file.exists()) {
                return null
            }
            val media = MediaMetadataRetriever()
            media.setDataSource(file.absolutePath)
            media.frameAtTime
        } catch (e: IllegalArgumentException) {
            LogUtil.e(e.message)
            null
        }
    }


    /**
     * 移除文件，获取文件时间与当前时间对比，我这时间定位4天，删除4天前的文件
     * @param secondsAgo 多少秒之前,默认值为 4 * 60 * 60 * 24 = （4天）
     */
    @JvmStatic
    fun removeFileByTime(dirPath: String, secondsAgo:Long = 4 * 60 * 60 * 24) {
        //获取目录下所有文件
        val allFile = getDirAllFile(File(dirPath))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        //获取当前时间
        var end = Date(System.currentTimeMillis())
        try {
            end = dateFormat.parse(dateFormat.format(Date(System.currentTimeMillis())))
        } catch (e: Exception) {
            LogUtil.d("dataformat exeption e $e")
        }
        LogUtil.d("getNeedRemoveFile  dirPath = $dirPath")
        for (file in allFile) { //ComDef
            try {
                //文件时间减去当前时间
                val start: Date = dateFormat.parse(dateFormat.format(Date(file.lastModified())))
                val diff: Long = end.time - start.time //这样得到的差值是微秒级别
                if (diff > (secondsAgo*1000)) {
                    deleteFolderFile(file)
                }
            } catch (e: Exception) {
                LogUtil.d("dataformat exeption e $e")
            }
        }
    }

    /**
     * 删除文件夹及文件夹下所有文件
     */
    fun deleteFolderFile(file: File) :Boolean{
        try {
            if (file.isDirectory) {
                val files = file.listFiles()
                if (files != null) {
                    for (i in files.indices) {
                        val f = files[i]
                        deleteFolderFile(f)
                    }
                }
                file.delete()
            } else if (file.exists()) {
                file.delete()
            }
        }catch (e:Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * 获取指定目录下一级文件
     */
    fun getDirAllFile(file: File): List<File> {
        val fileList: MutableList<File> = ArrayList()
        val fileArray = file.listFiles() ?: return fileList
        for (f in fileArray) {
            fileList.add(f)
        }
        fileSortByTime(fileList)
        return fileList
    }

    /**
     * 对文件进行时间排序
     */
    fun fileSortByTime(fileList: List<File>?) {
        if (fileList != null) {
            Collections.sort(fileList) { p1, p2 ->
                if (p1!!.lastModified() < p2!!.lastModified()) {
                    -1
                } else 1
            }
        }
    }

    /**
     * @param bm   位图
     * @param path 路径
     * @return 返回文件
     */
    @Throws(IOException::class)
    fun saveFile(bm: Bitmap, path: String?): File {
        val mFile = File(path)
        val bos = BufferedOutputStream(FileOutputStream(mFile))
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
        bos.close()
        return mFile
    }

    /**
     * 回收bitmap
     *
     * @param bitmap 回收的bitmap
     */
    fun recycleBitmap(bitmap: Bitmap?) {
        if (null != bitmap && !bitmap.isRecycled) {
            bitmap.recycle()
        }
        System.gc()
    }

    /**
     * 复制单个文件
     *
     * @param oldPathName String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPathName String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return `true` if and only if the file was copied;
     * `false` otherwise
     */
    fun copyFile(oldPathName: String, newPathName: String): Boolean {
        return try {
            val oldFile = File(oldPathName)
            //如果不需要打log，可以使用下面的语句
            if (!oldFile.exists() || !oldFile.isFile || !oldFile.canRead()) {
                return false
            }
            val fileInputStream = FileInputStream(oldPathName) //读入原文件
            val fileOutputStream = FileOutputStream(newPathName)
            val buffer = ByteArray(1024)
            var byteRead: Int
            while (fileInputStream.read(buffer).also { byteRead = it } != -1) {
                fileOutputStream.write(buffer, 0, byteRead)
            }
            fileInputStream.close()
            fileOutputStream.flush()
            fileOutputStream.close()
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取缓存大小
     * @param context
     * @return
     */
    fun getCacheSize(): String {
        val toDouble = getFolderSize(File(getPicturesPath())).toDouble()
        val toDouble1 = getFolderSize(File(getCachePath())).toDouble()
        val toDouble2 = getFolderSize(File(getExtDownloadsPath())).toDouble()
        return getFormatSize(toDouble+toDouble1+toDouble2)
    }

    /**
     * 清除缓存
     */
    fun clearCache(){
       deleteFolderFile(File(getPicturesPath()))
       deleteFolderFile(File(getCachePath()))
       deleteFolderFile(File(getExtDownloadsPath()))
    }

    /**
     * 格式化单位
     * @param size
     * @return
     */
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        //        if (kiloByte < 1) {
//            return size + "Byte(s)";
//        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        //        double teraBytes = gigaByte / 1024;
//        if (teraBytes < 1) {
        val result3 = BigDecimal(gigaByte.toString())
        return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
//        }
//        BigDecimal result4 = new BigDecimal(teraBytes);
//        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 获取文件夹大小(递归)
     * @param file File实例
     * @return long
     */
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                size = if (fileList[i].isDirectory) {
                    size + getFolderSize(fileList[i])
                } else {
                    size + fileList[i].length()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return size
    }



}