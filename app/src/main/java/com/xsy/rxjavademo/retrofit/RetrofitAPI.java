package com.xsy.rxjavademo.retrofit;

import com.xsy.rxjavademo.model.ZhuangbiImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by xsy on 2017/8/3.
 * 普通Retrofit接口
 */

public interface RetrofitAPI {
    @GET("search")
    Call<List<ZhuangbiImage>> normal(@Query("q")String key);
}
