package com.shidqi.movieassestment.di

import android.content.Context
import com.shidqi.movieassestment.network.NetworkConnectionInterceptor
import com.shidqi.movieassestment.others.BASE_URL
import com.shidqi.movieassestment.service.IRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Providing retrofit instance for repositories to use
     *  **/
    @Singleton
    @Provides
    fun provideRetrofitService(@ApplicationContext context: Context, ): IRetrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val client = OkHttpClient.Builder().connectTimeout(150, TimeUnit.SECONDS).readTimeout(150, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(Interceptor {
                val builder = it
                    .request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")


                return@Interceptor it.proceed(
                    builder.build()
                )
            }).addInterceptor(Interceptor { chain ->
                // Inject query parameters
                val original: Request = chain.request()
                val originalHttpUrl: HttpUrl = original.url
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", "11008596816214d9513b4d3e381263d7")
                    .build()

                // Request customization: add request headers
                val requestBuilder: Request.Builder = original.newBuilder()
                    .url(url)
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }).addInterceptor(NetworkConnectionInterceptor(context))
            .build()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build().create(IRetrofit::class.java)
    }
}