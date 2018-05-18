package com.example.chenzhe.frescodemo.fresco.listener;

import android.graphics.Bitmap;
/**
 * User:chenzhe
 * Date: 2018/5/13
 * Time:19:00
 */
public interface LoadFrescoListener {
    void onSuccess(Bitmap bitmap);

    void onFail();
}