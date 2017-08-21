package com.xsy.rxjavademo;

/**
 * Created by xsy on 2017/8/15.
 */

public interface CallbackListener {
    void onSuccess(Object data);

    void onFailure(String msg);
}
