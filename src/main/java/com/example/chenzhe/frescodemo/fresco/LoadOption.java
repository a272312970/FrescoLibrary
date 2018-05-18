package com.example.chenzhe.frescodemo.fresco;

import android.graphics.Point;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.request.Postprocessor;


/**
 * 配置
 * User:chenzhe
 * Date: 2018/3/29
 * Time:12:13
 */
public class LoadOption {

    //默认图片
    int defaultImg;

    //错误图片
    int failImg;

    //空图片
    int emptyImg;

    //圆角
    int cornerRadius;

    //是否圆形
    boolean isCircle;

    //是否本地路径
    boolean loadLocalPath;

    //是否gif
    boolean isAnima;

    //大小
    Point size;

    //图片地址
    String uri;

    //处理器
    Postprocessor postprocessor;

    ControllerListener controllerListener;

    public LoadOption(Builder builder) {
        defaultImg = builder.defaultImg;
        failImg = builder.failImg;
        emptyImg = builder.emptyImg;
        cornerRadius = builder.cornerRadius;
        isCircle = builder.isCircle;
        loadLocalPath = builder.loadLocalPath;
        isAnima = builder.isAnima;
        size = builder.size;
        uri = builder.uri;
        postprocessor = builder.postprocessor;
        controllerListener = builder.controllerListener;
    }


    public void setEmptyImg(int emptyImg) {
        this.emptyImg = emptyImg;
    }

    public void setDefaultImg(int defaultImg) {
        this.defaultImg = defaultImg;
    }


    public void setFailImg(int failImg) {
        this.failImg = failImg;
    }


    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }



    public void setCircle(boolean circle) {
        isCircle = circle;
    }


    public void setLoadLocalPath(boolean loadLocalPath) {
        this.loadLocalPath = loadLocalPath;
    }

    public void setAnima(boolean anima) {
        isAnima = anima;
    }


    public void setSize(Point size) {
        this.size = size;
    }


    public void setUri(String uri) {
        this.uri = uri;
    }


    public void setPostprocessor(Postprocessor postprocessor) {
        this.postprocessor = postprocessor;
    }

    public void setControllerListener(ControllerListener controllerListener) {
        this.controllerListener = controllerListener;
    }

    public static class Builder {
        //默认图片
        private int defaultImg = 0;

        //错误图片
        private int failImg = 0;

        //空图片
        private int emptyImg = 0;

        //圆角
        private int cornerRadius = 0;

        //是否圆形
        private boolean isCircle = false;

        //是否本地路径
        private boolean loadLocalPath = false;

        //是否gif
        private boolean isAnima = true;

        //大小
        private Point size = null;

        //图片地址
        private String uri = null;

        //处理器
        private Postprocessor postprocessor;

        private ControllerListener controllerListener;

        public Builder setControllerListener(ControllerListener baseControllerListener) {
            this.controllerListener = baseControllerListener;
            return this;
        }

        public void setEmptyImg(int emptyImg) {
            this.emptyImg = emptyImg;
        }

        public Builder setFailImg(int failImg) {
            this.failImg = failImg;
            return this;
        }

        public Builder setDefaultImg(int defaultImg) {
            this.defaultImg = defaultImg;
            return this;
        }

        public Builder setCornerRadius(int cornerRadius) {
            this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder setCircle(boolean circle) {
            isCircle = circle;
            return this;
        }

        public Builder setLoadLocalPath(boolean loadLocalPath) {
            this.loadLocalPath = loadLocalPath;
            return this;
        }

        public Builder setAnima(boolean anima) {
            isAnima = anima;
            return this;
        }

        public Builder setSize(Point size) {
            this.size = size;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setPostprocessor(Postprocessor postprocessor) {
            this.postprocessor = postprocessor;
            return this;
        }

        public LoadOption build() {
            return new LoadOption(this);
        }
    }

}
