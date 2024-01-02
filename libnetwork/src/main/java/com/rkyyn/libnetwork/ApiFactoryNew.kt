package com.rkyyn.libnetwork

import android.util.Log
import android.util.SparseArray
import com.blankj.utilcode.util.LogUtils
import com.rkyyn.libcommon.base.BaseApplication
import com.rkyyn.libcommon.utils.GsonUtils
import com.rkyyn.libnetwork.bean.BaseResult
import com.rkyyn.libnetwork.common.CustomGsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
object ApiFactoryNew {
    private val TAG: String = "HttpLogInfo"
    private val urls: SparseArray<String> = SparseArray()
    private val retrofits: SparseArray<Retrofit> = SparseArray()
    private var customHeaders: Map<String, String>? = null
    private const val CONNECT_TIMEOUT = 60L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 10L
    private val UTF8 = Charset.forName("UTF-8")

    /**
     * @param baseUrl 地址
     * @param apiClass api
     * @param apiConfig
     */
    fun <T> createService(
        baseUrl: String,
        apiClass: Class<T>,
        apiConfig: ApiConfig? = null
    ): T {
        //url是否存在缓存
        val indexOfValue = urls.indexOfValue(baseUrl)
        val retrofit =
            if (apiConfig?.getHeader() != customHeaders) {//请求头不相同
                if (indexOfValue >= 0) {
                    //清除之前保存的url请求
                    urls.remove(indexOfValue)
                    retrofits.remove(indexOfValue)
                }
                customHeaders = apiConfig?.getHeader()
                //重新获取retrofit
                getRetrofit(baseUrl, apiConfig)
            } else if (indexOfValue >= 0) {
                //内存中存在直接取出来使用
                retrofits.get(indexOfValue)
            } else {
                //内存中没有则重新获取
                getRetrofit(baseUrl, apiConfig)
            }
        return retrofit.create(apiClass)
    }

    private fun getRetrofit(baseUrl: String, apiConfig: ApiConfig? = null): Retrofit =
        Retrofit.Builder().run {
            baseUrl(baseUrl)
            client(newClient(apiConfig = apiConfig))
            addConverterFactory(
//                GsonConverterFactory.create(
////                    GsonBuilder().serializeNulls().create()
//                    GsonUtils.buildGson()
//                )
                CustomGsonConverterFactory.create(GsonUtils.buildGson()) { gson, adapter ->
                    return@create apiConfig?.getMyResponseBodyConverter(gson, adapter)
                }
            )
            val build = build()
            val index = urls.size()
            urls.append(index, baseUrl)
            retrofits.append(index, build)
            build
        }

    private fun newClient(apiConfig: ApiConfig? = null) =
        OkHttpClient.Builder().apply {
            retryOnConnectionFailure(true)
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor(HttpLog()).setLevel(HttpLoggingInterceptor.Level.BODY))
            apiConfig?.apply {
                addInterceptor(ResultInterceptor(this))
                getHeader()?.let {
                    //添加自定义请求头
                    addInterceptor(Interceptor { chain ->
                        val original: Request = chain.request()
                        val requestBuilder: Request.Builder = original.newBuilder()
                        for ((key, value) in it) {
                            requestBuilder.addHeader(key, value)
                        }
                        val request: Request = requestBuilder.build()
                        chain.proceed(request)
                    })
                }
            }
        }.build()

    class HttpLog : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d(TAG, message)
        }
    }

    /**
     * 返回结果监听
     */
    class ResultInterceptor(private val apiConfig: ApiConfig? = null) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            apiConfig?.apply {
                getResult(response, getResultClass())?.let {
                    //根据返回的code和message自己做相应处理
                    dealCode(it)
                }
            }
            return response
        }
    }

    /**
     * 处理返回结果，返回指定的基类
     */
    private fun getResult(
        originalResponse: Response,
        resultClass: Class<out BaseResult>?
    ): BaseResult? {
        if (resultClass == null) return null
//        if(!originalResponse.isSuccessful){
//            LogUtils.e("网络请求出错："+originalResponse.code.toString()+","+originalResponse.message)
//            BaseApplication.dismissLoadingDialog()
//            return null
//        }
        try {
            val responseBody = originalResponse.body
            val source = responseBody!!.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer()
            var charset: Charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)!!
            }
            val bodyString = buffer.clone().readString(charset)
            val gson = GsonUtils.buildGson()
            return gson.fromJson(bodyString, resultClass)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(e.message)
//                    e.message?.let { ToastUtils.showToast(it) }
            BaseApplication.dismissLoadingDialog()
            return null
        }
    }
}