package com.xsy.rxjavademo;

import android.content.Context;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xsy on 2017/8/17.
 */

class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ZhuangbiImage> mList;
    private Map<Integer,GifDrawable> map = new HashMap<>();

    public ImageAdapter(Context gifActivity, List<ZhuangbiImage> mList) {
        this.mContext = gifActivity;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.iamge_item, parent, false);
        MyViewHodler myViewHodler = new MyViewHodler(view);
        return myViewHodler;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHodler myHolder = (MyViewHodler) holder;
        myHolder.setPosition(position);
        Glide.with(mContext).load(mList.get(position).image_url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.mipmap.ic_launcher).listener(new RequestListener<String, GifDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                map.put(position,resource);
                return false;
            }
        }).into(myHolder.iv);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clearGifCache(){
        map.clear();
    }

    class MyViewHodler extends RecyclerView.ViewHolder {
        public ImageView iv;
        private int position;

        public MyViewHodler(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (map.size()>0&&map.get(position)!=null) {
                        map.get(position).start();
                        Log.e("flag--","onViewAttachedToWindow(ImageAdapter.java:105)-->>"+"开始");
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (map.size()>0&&map.get(position)!=null) {
                        map.get(position).stop();
                        Log.e("flag--","onViewDetachedFromWindow(ImageAdapter.java:112)-->>"+"暂停");
                    }
                }
            });
        }

        private void setPosition(int position) {
            this.position = position;
        }
    }
}
