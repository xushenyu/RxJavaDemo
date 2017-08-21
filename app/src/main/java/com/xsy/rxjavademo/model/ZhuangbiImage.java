// (c)2016 Flipboard Inc, All Rights Reserved.

package com.xsy.rxjavademo.model;

public class ZhuangbiImage {
    public String description;
    public String image_url;
    private boolean isVisiable;

    public void setVisiable(boolean visiable) {
        isVisiable = visiable;
    }

    public boolean isVisiable() {

        return isVisiable;
    }
}
