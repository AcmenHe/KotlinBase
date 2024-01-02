package com.rkyyn.libnetwork.common

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.OutputStreamWriter
import java.nio.charset.Charset


/**
 *  author : hepeng
 *  time   : 2023/06/01
 *  desc   :
 */
class CustomGsonRequestBodyConverter<T> internal constructor(gson: Gson, adapter: TypeAdapter<T>) :
    Converter<T, RequestBody> {
    private val gson: Gson
    private val adapter: TypeAdapter<T>


    companion object {
        private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
        private val UTF_8: Charset = Charset.forName("UTF-8")
    }

    init {
        this.gson = gson
        this.adapter = adapter
    }


    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter: JsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }




}