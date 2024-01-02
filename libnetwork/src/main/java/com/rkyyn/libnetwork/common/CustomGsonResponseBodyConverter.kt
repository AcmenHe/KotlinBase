package com.rkyyn.libnetwork.common

import android.security.identity.ResultData
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonToken
import okhttp3.ResponseBody
import retrofit2.Converter


/**
 *  author : hepeng
 *  time   : 2023/06/01
 *  desc   :
 */
open class CustomGsonResponseBodyConverter<T> constructor(gson: Gson, adapter: TypeAdapter<T>) :
    Converter<ResponseBody,T> {
    protected val gson: Gson
    protected val adapter: TypeAdapter<T>
    init {
        this.gson = gson
        this.adapter = adapter
    }

    override fun convert(value: ResponseBody): T {
        val jsonReader = gson.newJsonReader(value.charStream())
        return try {
            val result = adapter.read(jsonReader)
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw JsonIOException("JSON document was not fully consumed.")
            }
            result
        } finally {
            value.close()
        }
    }

    /*//重写参考
    override fun convert(value: ResponseBody): T {
        val json = value.string().replace(",\"data\":\"{}\",",",\"data\":null,")
        val baseResponse = gson.fromJson(json, ResultData::class.java)
        if (!baseResponse.isSuccess()) {
            value.close()
            throw ApiException(baseResponse.code, baseResponse.message ?:"")
        }
        //第二次解析
        val result= adapter.fromJson(json)
        value.close()
        return result
    }*/
}
