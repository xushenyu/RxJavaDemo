package com.xsy.rxjavademo;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xsy.rxjavademo.model.ZhuangbiImage;

import java.util.List;

/**
 * Created by xsy on 2017/9/1.
 */

public class FrescoAdapter extends RecyclerView.Adapter {

    private final Context mContext;
    private  List<ZhuangbiImage> mList;

    public FrescoAdapter(Context context, List<ZhuangbiImage> mList){
        this.mContext = context;
        this.mList = mList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fresco_item, parent,false);
        return new FrescoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FrescoViewHolder freHolder = (FrescoViewHolder) holder;
        Uri uri = Uri.parse(mList.get(position).image_url);
        AbstractDraweeController build = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
        freHolder.iv.setController(build);
//        freHolder.iv.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class FrescoViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView iv;

        public FrescoViewHolder(View itemView) {
            super(itemView);
            iv = (SimpleDraweeView) itemView.findViewById(R.id.iv);
        }
    }
}
