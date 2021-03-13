package com.lbf.lib.imageloader;

import ohos.media.image.PixelMap;

public interface RequestListener {

    public void onSuccess(PixelMap pixelMap);

    public void onFail();
}
