package com.xsy.rxjavademo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xsy.rxjavademo.model.BaseAction;
import com.xsy.rxjavademo.model.ZhuangbiImage;
import com.xsy.rxjavademo.retrofit.GetAPI;
import com.xsy.rxjavademo.retrofit.PostAPI;
import com.xsy.rxjavademo.retrofit.RetrofitAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxJava:一个在Java VM上使用可观测的序列来组成异步的、基于事件的程序的库
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static OkHttpClient okHttpClient;
    private Subscription subscription;
    private GetAPI getAPI;
    private PostAPI postAPI;
    private RetrofitAPI retrofitAPI;
    private SwipeRefreshLayout mRefresh;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefresh.setColorSchemeColors(Color.BLACK, Color.GREEN, Color.RED);
        mRefresh.setEnabled(false);
        findViewById(R.id.bt_one).setOnClickListener(this);
        findViewById(R.id.bt_two).setOnClickListener(this);
        findViewById(R.id.bt_three).setOnClickListener(this);
        findViewById(R.id.bt_four).setOnClickListener(this);
        findViewById(R.id.bt_five).setOnClickListener(this);
        findViewById(R.id.bt_six).setOnClickListener(this);
        findViewById(R.id.bt_seven).setOnClickListener(this);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(60, TimeUnit.SECONDS)//写操作 超时时间
                .readTimeout(60, TimeUnit.SECONDS)//读操作超时时间
                .build();
        retrofit = RetrofitUtil.getInstance().initRetrofit("http://www.zhuangbi.info/");
        getAPI = retrofit.create(GetAPI.class);
        postAPI = retrofit.create(PostAPI.class);
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    @Override
    public void onClick(View v) {
        mRefresh.setRefreshing(true);
        switch (v.getId()) {
            case R.id.bt_one://RxJava基本练习
                subscription = Observable.just("这是just发送的数据1", "这是just发送的数据2").subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mRefresh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(false);
                            }
                        },500);
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.bt_two://RxJava+Retrofit+OkHttp练习
                subscription = getAPI.get("lllllllll")
                        /**
                         * subscribeOn(): 指定 subscribe() 所发生的线程，
                         * 即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程
                         */
                        .subscribeOn(Schedulers.io())//让被观察者在IO线程运行，被观察者做的操作是网络请求
                        /**
                         * observeOn(): 指定 Subscriber 所运行在的线程。
                         * 或者叫做事件消费的线程。
                         */
                        .observeOn(AndroidSchedulers.mainThread())//让观察者在主线程运行，观察者操作是请求成功后的处理
//                        .subscribe(new Subscriber<List<ZhuangbiImage>>() {//订阅
//                            @Override
//                            public void onCompleted() {
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                mRefresh.setRefreshing(false);
//                                Log.e("flag--", "onError(MainActivity.java:97)-->>" + e.getMessage());
//                            }
//
//                            @Override
//                            public void onNext(List<ZhuangbiImage> s) {
//                                mRefresh.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mRefresh.setRefreshing(false);
//                                    }
//                                },500);
//                                Toast.makeText(MainActivity.this, "get请求成功--" + s.get(0).description, Toast.LENGTH_SHORT).show();
//                            }
//                        });
                .subscribe(new BaseAction<List<ZhuangbiImage>>(new CallbackListener() {
                    @Override
                    public void onSuccess(Object data) {
                        mRefresh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(false);
                            }
                        },500);
//                        List<ZhuangbiImage> list = (List<ZhuangbiImage>) data;
//                        String description = list.get(0).description;
//                        Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mRefresh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(false);
                            }
                        },500);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }));
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
                                mRefresh.setRefreshing(false);
                            }

                            @Override
                            public void onNext(List<ZhuangbiImage> zhuangbiImages) {
                                mRefresh.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRefresh.setRefreshing(false);
                                    }
                                },500);
                                Toast.makeText(MainActivity.this, "post请求成功--" + zhuangbiImages.get(0).description, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.bt_four:
                Call<List<ZhuangbiImage>> call = retrofitAPI.normal("在下");
                call.enqueue(new Callback<List<ZhuangbiImage>>() {
                    @Override
                    public void onResponse(Call<List<ZhuangbiImage>> call, Response<List<ZhuangbiImage>> response) {
                        mRefresh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(false);
                            }
                        },500);
                        Toast.makeText(MainActivity.this, "普通Retrofit请求--" + response.body().get(0).description, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<List<ZhuangbiImage>> call, Throwable t) {
                        mRefresh.setRefreshing(false);
                        Log.e("flag--", "onFailure(MainActivity.java:126)-->>" + t.getMessage());
                    }
                });
                break;
            case R.id.bt_five://map转换 List<ZhuangbiImage>转换成String
                 subscription = getAPI.get("可爱").map(new Func1<List<ZhuangbiImage>, String>() {
                    @Override
                    public String call(List<ZhuangbiImage> zhuangbiImages) {
                        String description = zhuangbiImages.get(0).description;
                        return description;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mRefresh.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefresh.setRefreshing(false);
                            }
                        },500);
                        Toast.makeText(MainActivity.this, "RxJava的map转换--"+s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.bt_six:
                startActivity(new Intent(this,GifActivity.class));
                break;
            case R.id.bt_seven:
                startActivity(new Intent(this,FrescoActivity.class));
                break;
        }
    }

    /**
     * 在 subscribe() 之后， Observable 会持有 Subscriber 的引用，
     * 这个引用如果不能及时被释放，将有内存泄露的风险
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
