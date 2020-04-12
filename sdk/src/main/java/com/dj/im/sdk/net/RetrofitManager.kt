package com.dj.im.sdk.net

import com.dj.im.sdk.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Create by ChenLei on 2020/4/11
 * Describe: Retrofit管理类
 */
internal class RetrofitManager private constructor() {

    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder().build()
    private val mRetrofit = Retrofit.Builder().baseUrl(Constant.URL.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
