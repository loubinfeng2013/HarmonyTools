package com.lbf.lib.imageloader;

import ohos.agp.components.Image;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 真正处理请求的处理类
 */
public class PixelMapDispatcher extends Thread {

    //用来更新ui的handler
    private EventHandler handler = new EventHandler(EventRunner.getMainEventRunner());

    private LinkedBlockingQueue<PixelMapRequest> mRequestQueue;

    public PixelMapDispatcher(LinkedBlockingQueue<PixelMapRequest> queue) {
        this.mRequestQueue = queue;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            if (mRequestQueue == null)
                continue;
            try {
                PixelMapRequest request = mRequestQueue.take();
                if (request == null) {
                    continue;
                }
                // 设置占位图片
                showLoadingImg(request);
                // 网络加载获取图片资源
                PixelMap pixelMap = findPixelMap(request);
                // 将图片显示到Image
                showImageView(request, pixelMap);
            } catch (Exception e) {

            }
        }
    }

    /**
     * 设置默认图
     *
     * @param request
     */
    private void showLoadingImg(PixelMapRequest request) {
        Image image = request.getImage().get();
        int resId = request.getResId();
        if (image != null && resId != 0) {
            handler.postTask(() -> {
                image.setPixelMap(request.getResId());
            });
        }
    }

    /**
     * 查找PixelMap的逻辑（包含三级缓存）
     *
     * @param request
     * @return
     */
    private PixelMap findPixelMap(PixelMapRequest request) {
        //先不处理缓存逻辑
        PixelMap pixelMap = downloadPixelMap(request);
        return pixelMap;
    }

    /**
     * 根据url下载PixelMap
     *
     * @param request
     * @return
     */
    private PixelMap downloadPixelMap(PixelMapRequest request) {
        InputStream is = null;
        PixelMap pixelMap = null;
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = urlConnection.getInputStream();
            ImageSource imageSource = ImageSource.create(is, new ImageSource.SourceOptions());
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;
            pixelMap = imageSource.createPixelmap(decodingOptions);
            return pixelMap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将结果设置到控件上
     *
     * @param request
     * @param pixelMap
     */
    private void showImageView(PixelMapRequest request, PixelMap pixelMap) {
        Image image = request.getImage().get();
        RequestListener listener = request.getListener();
        if (image != null && pixelMap != null && request.getUrlMd5() != null && request.getUrlMd5().equals(image.getTag())) {
            handler.postTask(() -> {
                image.setPixelMap(pixelMap);
            });
            if (listener != null)
                listener.onSuccess(pixelMap);
        } else {
            if (listener != null)
                listener.onFail();
        }
    }
}
