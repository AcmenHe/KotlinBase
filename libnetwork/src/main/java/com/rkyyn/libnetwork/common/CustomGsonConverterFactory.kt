package com.rkyyn.libnetwork.common

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 *  author : hepeng
 *  time   : 2023/06/01
 *  desc   :
 */
class  CustomGsonConverterFactory constructor(gson: Gson,var getResponseBodyConverter:((Gson, TypeAdapter<out Any>)->CustomGsonResponseBodyConverter<*>?)?= null) : Converter.Factory() {
    private val gson: Gson
    init {
        this.gson = gson
    }
    companion object {
        @JvmOverloads  // Guarding public API nullability.
        fun create(gson: Gson? = Gson(),getResponseBodyConverter:((Gson, TypeAdapter<out Any>)->CustomGsonResponseBodyConverter<*>?)?= null): CustomGsonConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            return CustomGsonConverterFactory(gson,getResponseBodyConverter)
        }
    }
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))

        val responseBodyConverter1 = getResponseBodyConverter?.let { it(gson, adapter) }
        return  responseBodyConverter1 ?: CustomGsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(type: Type,
                                      parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>,
                                      retrofit: Retrofit?): Converter<*, RequestBody> {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return CustomGsonRequestBodyConverter(gson, adapter)
    }
}
