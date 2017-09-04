package com.xsy.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xsy.rxjavademo.model.ZhuangbiImage;
import com.xsy.rxjavademo.retrofit.GifAPI;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private GridLayoutManager gridLayoutManager;

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
        gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        imageAdapter = new ImageAdapter(this, mList);
        Log.e("flag--", "onCreate(GifActivity.java:60)-->>" + imageAdapter.getCache().size());
        mRecyclerView.setAdapter(imageAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int p = gridLayoutManager.findFirstVisibleItemPosition();
                View itemView = gridLayoutManager.findViewByPosition(p);
                int[] xy = new int[2];
                itemView.getLocationInWindow(xy);
                Log.e("flag--", "onScrolled(GifActivity.java:76)-->>" + xy[1] + "====" + itemView.getHeight());
                Map<Integer, SoftReference<GifDrawable>> cache = imageAdapter.getCache();
                if (cache.get(p) != null && cache.get(p).get() != null) {
                    if (xy[1] > 0) {
                        cache.get(p).get().start();
                        if (cache.get(p + 1) != null && cache.get(p + 1).get() != null) {
                            cache.get(p + 1).get().stop();
                        }
                    } else {
                        cache.get(p).get().stop();
                        if (cache.get(p + 1) != null && cache.get(p + 1).get() != null) {
                            cache.get(p + 1).get().start();
                        }
                    }
                }
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
                        Log.e("flag--", "call(GifActivity.java:62)-->>" + mList.size() + mList.get(0).image_url);
                        imageAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("flag--", "onDestroy(GifActivity.java:84)-->>" + "OnDestory");
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        imageAdapter.clearGifCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
//            imageAdapter.notifyItemRangeChanged(0,mList.size());
//            Map<Integer, SoftReference<GifDrawable>> cache = imageAdapter.getCache();
//            for (SoftReference<GifDrawable> gif : cache.values()) {
//                if (gif.get()!=null){
//                    gif.get().stop();
//                }
//            }
        }
        Log.e("flag--", "onResume(GifActivity.java:94)-->>" + "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("flag--", "onPause(GifActivity.java:100)-->>" + "onPause");
    }
}
