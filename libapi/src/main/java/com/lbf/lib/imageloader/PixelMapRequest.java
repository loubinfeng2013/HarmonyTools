package com.lbf.lib.imageloader;

import com.lbf.utils.MD5Utils;
import com.lbf.utils.StringUtils;
import ohos.agp.components.Image;
import ohos.app.AbilityContext;

import java.lang.ref.SoftReference;

/**
 * 图片请求封装类
 */
public class PixelMapRequest {

    /**
     * 需要下载的url
     */
    private String mUrl;

    /**
     * 关联的image控件,使用软引用持有
     */
    private SoftReference<Image> mImage;

    /**
     * 上下文对象
     */
    private AbilityContext mContext;

    /**
     * 占位图资源id
     */
    private int mResId;

    /**
     * 请求监听
     */
    private RequestListener mListener;

    /**
     * 请求标志，用于防止图片错乱，和三级缓存
     */
    private String mUrlMd5;

    public PixelMapRequest(AbilityContext context) {
        this.mContext = context;
    }

    /**
     * 设置下载的路径
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
     * 设置默认图资源
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
     * 绑定关联的image控件，并将请求加入队列
     *
     * @param image
     */
    public void into(Image image) {
        image.setTag(mUrlMd5);
        this.mImage = new SoftReference<>(image);
        // 发起请求
        RequestManager.getInstance().addPixelMapRequest(this);
    }

    /**
     * 获取请求url
     *
     * @return
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 获取控件实例
     *
     * @return
     */
    public SoftReference<Image> getImage() {
        return mImage;
    }

    /**
     * 获取展位图资源id
     *
     * @return
     */
    public int getResId() {
        return mResId;
    }

    /**
     * 获取监听器实例
     *
     * @return
     */
    public RequestListener getListener() {
        return mListener;
    }

    /**
     * 获取请求标志位
     *
     * @return
     */
    public String getUrlMd5() {
        return mUrlMd5;
    }
}
