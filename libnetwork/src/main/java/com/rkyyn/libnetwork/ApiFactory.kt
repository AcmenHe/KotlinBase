package com.rkyyn.libnetwork

import android.util.Log
import android.util.SparseArray
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  author : hepeng
 *  time   : 2022/07/19
 *  desc   :
 */
object ApiFactory {
    private val TAG: String = "HttpLogInfo"
    private val urls: SparseArray<String> = SparseArray()
    private val retrofits: SparseArray<Retrofit> = SparseArray()
    private var customHeaders: Map<String, String> ? = null
    private const val CONNECT_TIMEOUT = 60L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 10L

    /**
     * @param baseUrl 地址
     * @param clazz api
     * @param newHeaders 自定义请求头
     */
    fun <T> createService(
        baseUrl: String,
        clazz: Class<T>,
        newHeaders: Map<String, String>? = null
    ): T {
        //url是否存在缓存
        val indexOfValue = urls.indexOfValue(baseUrl)
        val retrofit =
            if (newHeaders != customHeaders) {//请求头不相同
                if (indexOfValue >= 0) {
                    //清除之前保存的url请求
                    urls.remove(indexOfValue)
                    retrofits.remove(indexOfValue)
                }
                customHeaders = newHeaders
                //重新获取retrofit
                getRetrofit(baseUrl, newClient(newHeaders))
            } else if (indexOfValue >= 0) {
                //内存中存在直接取出来使用
                retrofits.get(indexOfValue)
            } else {
                //内存中没有则重新获取
                getRetrofit(baseUrl, newClient())
            }
        return retrofit.create(clazz)
    }

    private fun getRetrofit(baseUrl: String, newClient: OkHttpClient): Retrofit =
        Retrofit.Builder().run {
            baseUrl(baseUrl)
            client(newClient)
            addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            val build = build()
            val index = urls.size()
            urls.append(index, baseUrl)
            retrofits.append(index, build)
            build
        }

    private fun newClient(headers: Map<String, String>? = null) =
        OkHttpClient.Builder().apply {
            retryOnConnectionFailure(true)
            connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor(HttpLog()).setLevel(HttpLoggingInterceptor.Level.BODY))
            headers?.let {
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
        }.build()

    class HttpLog : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d(TAG, message)
        }
    }

}