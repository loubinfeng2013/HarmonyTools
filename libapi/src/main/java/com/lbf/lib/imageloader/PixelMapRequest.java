package com.lbf.lib.imageloader;

import com.lbf.utils.MD5Utils;
import com.lbf.utils.StringUtils;
import ohos.agp.components.Image;
import ohos.app.AbilityContext;

import java.lang.ref.SoftReference;

/**
 * 封装图片请求
 */
public class PixelMapRequest {

    //需要下载的url
    private String mUrl;

    //关联的image控件
    private SoftReference<Image> mImage;

    //上下文对象
    private AbilityContext mContext;

    //占位图
    private int mResId;

    //请求回调监听
    private RequestListener mListener;

    //请求标志
    private String mUrlMd5;

    public PixelMapRequest(AbilityContext context) {
        this.mContext = context;
    }

    /**
     * 加载url
     *
     * @param url
     * @return
     */
    public PixelMapRequest load(String url) {
        this.mUrl = url;
        if (!StringUtils.isEmpty(url)) {
            this.mUrlMd5 = MD5Utils.encrypt(url);
        }
        return this;
    }

    /**
     * 设置默认图
     *
     * @param resId
     * @return
     */
    public PixelMapRequest loading(int resId) {
        this.mResId = resId;
        return this;
    }

    /**
     * 设置请求监听
     *
     * @param listener
     * @return
     */
    public PixelMapRequest setListener(RequestListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 绑定关联的image控件
     *
     * @param image
     */
    public void into(Image image) {
        image.setTag(mUrlMd5);
        this.mImage = new SoftReference<>(image);
        // 发起请求
        RequestManager.getInstance().addBitmapRequest(this);
    }

    public String getUrl() {
        return mUrl;
    }

    public SoftReference<Image> getImage() {
        return mImage;
    }

    public int getResId() {
        return mResId;
    }

    public RequestListener getListener() {
        return mListener;
    }

    public String getUrlMd5() {
        return mUrlMd5;
    }
}
