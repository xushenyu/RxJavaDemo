package com.xsy.rxjavademo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xsy on 2017/8/17.
 */

public class RetrofitUtil {

    private static volatile RetrofitUtil instance;
    private OkHttpClient okHttpClient;

    public static synchronized RetrofitUtil getInstance() {
        if (instance == null) {
            instance = new RetrofitUtil();
        }
        return instance;
    }

    public Retrofit initRetrofit(String baseUrl) {
        /**
         * 初始化Retrofit,使用okHttp请求，支持Gson解析，支持RxJava
         */
        Retrofit retrofit = new Retrofit.Builder().client(initHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public OkHttpClient initHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)//连接超时时间
                    .writeTimeout(60, TimeUnit.SECONDS)//写操作 超时时间
                    .readTimeout(60, TimeUnit.SECONDS)//读操作超时时间
                    .build();
        }
        return okHttpClient;
    }
}
