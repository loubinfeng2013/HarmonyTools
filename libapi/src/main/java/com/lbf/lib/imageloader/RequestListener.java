package com.lbf.lib.imageloader;

import ohos.media.image.PixelMap;

/**
 * 请求监听
 */
public interface RequestListener {

    public void onSuccess(String url, PixelMap pixelMap);

    public void onFail(String url);
}
