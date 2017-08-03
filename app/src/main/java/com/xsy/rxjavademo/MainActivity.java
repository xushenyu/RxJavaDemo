package com.xsy.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xsy.rxjavademo.model.ZhuangbiImage;
import com.xsy.rxjavademo.retrofit.GetAPI;
import com.xsy.rxjavademo.retrofit.PostAPI;
import com.xsy.rxjavademo.retrofit.RetrofitAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * RxJava:一个在Java VM上使用可观测的序列来组成异步的、基于事件的程序的库
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static OkHttpClient okHttpClient = new OkHttpClient();
    private Subscription subscription;
    private GetAPI getAPI;
    private PostAPI postAPI;
    private RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_one).setOnClickListener(this);
        findViewById(R.id.bt_two).setOnClickListener(this);
        findViewById(R.id.bt_three).setOnClickListener(this);
        findViewById(R.id.bt_four).setOnClickListener(this);

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                .baseUrl("http://www.zhuangbi.info/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        getAPI = retrofit.create(GetAPI.class);
        postAPI = retrofit.create(PostAPI.class);
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_one://RxJava基本练习
                Observable.just("这是just发送的数据1", "这是just发送的数据2").subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.bt_two://RxJava+Retrofit+OkHttp练习
                subscription = getAPI.get("110")
                        .subscribeOn(Schedulers.io())//让被观察者在IO线程运行，被观察者做的操作是网络请求
                        .observeOn(AndroidSchedulers.mainThread())//让观察者在主线程运行，观察者操作是请求成功后的处理
                        .subscribe(new Subscriber<List<ZhuangbiImage>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("flag--", "onError(MainActivity.java:97)-->>" + e.getMessage());
                            }

                            @Override
                            public void onNext(List<ZhuangbiImage> s) {
                                Toast.makeText(MainActivity.this, "get请求成功--" + s.get(0).description, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.bt_three:////RxJava+Retrofit+OkHttp Post练习
                Map<String, String> map = new HashMap<>();
                map.put("q", "可爱");
                subscription = postAPI.post(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<ZhuangbiImage>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<ZhuangbiImage> zhuangbiImages) {
                                Toast.makeText(MainActivity.this, "post请求成功--" + zhuangbiImages.get(0).description, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.bt_four:
                Call<List<ZhuangbiImage>> call = retrofitAPI.normal("在下");
                call.enqueue(new Callback<List<ZhuangbiImage>>() {
                    @Override
                    public void onResponse(Call<List<ZhuangbiImage>> call, Response<List<ZhuangbiImage>> response) {
                        Toast.makeText(MainActivity.this, "普通Retrofit请求--" + response.body().get(0).description, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<List<ZhuangbiImage>> call, Throwable t) {
                        Log.e("flag--","onFailure(MainActivity.java:126)-->>"+t.getMessage());
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
