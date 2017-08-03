package com.xsy.rxjavademo.retrofit;

import com.xsy.rxjavademo.model.ZhuangbiImage;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xsy on 2017/8/2.
 * retrofit需要定义一个接口
 */
public interface GetAPI {
    @GET("search")//接口的尾址
    /**
     * @Query请求参数的key
     * Observable<List<ZhuangbiImage>>为返回值，是RxJava中的被观察者
     * Observable<T>里面的类一定要和json串对应，这里就相当于json解析了
     */
    Observable<List<ZhuangbiImage>> get(@Query("q") String uid);
}
