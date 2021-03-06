package com.dj.im.sdk.net

import com.dj.im.sdk.Constant
import com.dj.im.sdk.service.ServiceManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Retrofit管理类
 */
internal class RetrofitManager private constructor() {

    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor {
        // 添加token到cookie中
        val token = try {
            ServiceManager.instance.dbDao.getConfigValue(Constant.Key.TOKEN) ?: ""
        } catch (e: Throwable) {
            e.printStackTrace()
            ""
        }
        it.proceed(
            it.request().newBuilder()
                .addHeader("cookie", "token=${token}")
                .build()
        )
    }.build()

    private val mRetrofit = Retrofit.Builder().baseUrl(Constant.URL.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .client(mOkHttpClient).build()

    val apiStore: ApiStore by lazy {
        mRetrofit.create(ApiStore::class.java)
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

}
