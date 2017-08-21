package com.xsy.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.xsy.rxjavademo.model.ZhuangbiImage;
import com.xsy.rxjavademo.retrofit.GifAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xsy on 2017/8/17.
 */

public class GifActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private Retrofit retrofit;
    private GifAPI gifAPI;
    private Subscription subscription;
    private List<ZhuangbiImage> mList = new ArrayList<>();
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true);
            }
        });

        mRefresh.setEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imageAdapter = new ImageAdapter(this, mList);
        mRecyclerView.setAdapter(imageAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        retrofit = RetrofitUtil.getInstance().initRetrofit("http://www.zhuangbi.info/");
        gifAPI = retrofit.create(GifAPI.class);
        subscription = gifAPI.get("动").observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<ZhuangbiImage>>() {
                    @Override
                    public void call(List<ZhuangbiImage> url) {
                        mRefresh.setRefreshing(false);
                        mList.addAll(url);
                        Log.e("flag--","call(GifActivity.java:62)-->>"+mList.size()+mList.get(0).image_url);
                        imageAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        imageAdapter.clearGifCache();
    }
}
