package com.xsy.rxjavademo;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xsy.rxjavademo.model.ZhuangbiImage;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

/**
 * Created by xsy on 2017/8/17.
 */

class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ZhuangbiImage> mList;
    private ArrayMap<Integer, SoftReference<GifDrawable>> map = new ArrayMap<>();
    public ImageAdapter(Context gifActivity, List<ZhuangbiImage> mList) {
        this.mContext = gifActivity;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.iamge_item, parent, false);
        return new MyViewHodler(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHodler myHolder = (MyViewHodler) holder;
        myHolder.setPosition(position);//position传给VIewHolder
        boolean visiable = mList.get(position).isVisiable();
        Glide.with(mContext).load(mList.get(position).image_url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.mipmap.ic_launcher).listener(new RequestListener<String, GifDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(final GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                myHolder.iv.post(new Runnable() {
                    @Override
                    public void run() {//GifDrawable要在UI线程才能操作
//                        resource.stop();//gif加载成功后，暂停所有播放
//                        if (!mList.get(position).isVisiable()){
//                        }
                        if (position!=1){
                            resource.stop();//gif加载成功后，暂停所有播放
//                            mList.get(position).setVisiable(true);
                        }
                    }
                });
                map.put(position, new SoftReference<>(resource));//缓存GifDrawable，如果存多了不知道会不会oom
                Log.e("flag--","onResourceReady(ImageAdapter.java:69)-->>"+position);
                return false;
            }
        }).into(myHolder.iv);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clearGifCache() {
        map.clear();//清除缓存
    }

    public Map<Integer, SoftReference<GifDrawable>> getCache(){
        return map;
    }

    public class MyViewHodler extends RecyclerView.ViewHolder {
        public ImageView iv;
        private int position;
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mList.get(position).setVisiable(true);//此处是为了防止gif加载时间大于延迟播放时间时，图片暂停的问题
                if (map.size() > 0 && map.get(position) != null) {
//                    tempMap.put(position,map.get(position));//可见时缓存
                    map.get(position).get().stop();//可见时，2s后播放
                }
            }
        };

        public MyViewHodler(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {//此方法会在Glide.listener之前运行
                    iv.postDelayed(runnable, 0);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    mList.get(position).setVisiable(false);
                    iv.removeCallbacks(runnable);//item不可见时，回收线程
                    if (map.size() > 0 && map.get(position) != null) {
//                        if (tempMap.size()>0&& tempMap.get(position)!=null){
//                            tempMap.remove(position);//不可见时从缓存移除
//                        }
                        map.get(position).get().stop();//不可见时暂停播放
//                        map.remove(position);
                    }
                }
            });
        }

        private void setPosition(int position) {
            this.position = position;
        }
    }
}
