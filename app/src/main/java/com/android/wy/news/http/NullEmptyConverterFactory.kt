package com.android.wy.news.http

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/8/30 10:03
  * @Version:        1.0
  * @Description:    修复 End of input at line 1 column 1 path $
 */
class NullEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, Any> =
            retrofit.nextResponseBodyConverter(this, type, annotations)
        return object : Converter<ResponseBody, Any> {
            override fun convert(body: ResponseBody): Any? {
                if (body.contentLength().toInt() == 0) return null
                return delegate.convert(body)
            }
        }
    }
}