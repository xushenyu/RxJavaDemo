package com.xsy.rxjavademo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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
 * Created by xsy on 2017/9/1.
 */

public class FrescoActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;
    private List<ZhuangbiImage> mList = new ArrayList<>();
    private FrescoAdapter frescoAdapter;
    private Subscription subscription;

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        frescoAdapter = new FrescoAdapter(this, mList);
        mRecyclerView.setAdapter(frescoAdapter);

        Retrofit retrofit = RetrofitUtil.getInstance().initRetrofit("http://www.zhuangbi.info/");
        GifAPI gifAPI = retrofit.create(GifAPI.class);
        subscription = gifAPI.get("动").observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Action1<List<ZhuangbiImage>>() {
                    @Override
                    public void call(List<ZhuangbiImage> zhuangbiImages) {
                        mRefresh.setRefreshing(false);
                        mList.addAll(zhuangbiImages);
                        frescoAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
