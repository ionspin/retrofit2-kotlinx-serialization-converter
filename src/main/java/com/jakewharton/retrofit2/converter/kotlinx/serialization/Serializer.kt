package com.jakewharton.retrofit2.converter.kotlinx.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

@Suppress("UNCHECKED_CAST") // Widening T to Any
internal sealed class Serializer {
  abstract fun <T> fromResponseBody(deserializationStrategy: DeserializationStrategy<T>, body: ResponseBody): T
  abstract fun <T> toRequestBody(contentType: MediaType, serializationStrategy: SerializationStrategy<T>, value: T): RequestBody

  class FromString(
      private val loader: Loader<String>,
      private val saver: Saver<String>
  ) : Serializer() {
    override fun <T> fromResponseBody(deserializationStrategy: DeserializationStrategy<T>, body: ResponseBody): T {
      val string = body.string()
      return loader(deserializationStrategy as DeserializationStrategy<Any>, string) as T
    }

    override fun <T> toRequestBody(contentType: MediaType, serializationStrategy: SerializationStrategy<T>, value: T): RequestBody {
      val string = saver(serializationStrategy as SerializationStrategy<Any>, value as Any)
      return RequestBody.create(contentType, string)
    }
  }

  class FromBytes(
      private val loader: Loader<ByteArray>,
      private val saver: Saver<ByteArray>
  ): Serializer() {
    override fun <T> fromResponseBody(deserializationStrategy: DeserializationStrategy<T>, body: ResponseBody): T {
      val bytes = body.bytes()
      return loader(deserializationStrategy as DeserializationStrategy<Any>, bytes) as T
    }

    override fun <T> toRequestBody(contentType: MediaType, serializationStrategy: SerializationStrategy<T>, value: T): RequestBody {
      val bytes = saver(serializationStrategy as SerializationStrategy<Any>, value as Any)
      return RequestBody.create(contentType, bytes)
    }
  }
}
