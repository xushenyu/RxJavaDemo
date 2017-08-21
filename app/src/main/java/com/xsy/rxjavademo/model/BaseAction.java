package com.xsy.rxjavademo.model;

import android.util.Log;

import com.xsy.rxjavademo.CallbackListener;

import rx.Observer;

/**
 * Created by xsy on 2017/8/15.
 */

public class BaseAction<T> implements Observer<T> {

    private final CallbackListener listener;

    public BaseAction(CallbackListener listener){
        this.listener = listener;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        listener.onFailure(e.getMessage());
        Log.e("flag--","onError(BaseAction.java:27)-->>"+e.toString());
    }

    @Override
    public void onNext(T t) {
        if (t!=null){
            listener.onSuccess(t);
        }else{
            listener.onFailure("无数据");
        }
    }
}
