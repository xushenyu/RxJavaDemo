package com.xsy.rxjavademo.retrofit;

import com.xsy.rxjavademo.model.ZhuangbiImage;

import java.util.List;
import java.util.Map;

import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by xsy on 2017/8/3.
 */

public interface PostAPI {

    @POST("search")
    /**
     * @QueryMap可以传Map
     */
    Observable<List<ZhuangbiImage>> post(@QueryMap Map<String, String> map);
}
