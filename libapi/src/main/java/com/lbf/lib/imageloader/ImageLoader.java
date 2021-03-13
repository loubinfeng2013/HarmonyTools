package com.lbf.lib.imageloader;

import ohos.app.AbilityContext;

/**
 * 调用者api类
 */
public class ImageLoader {

    /**
     * 返回一个图片请求
     *
     * @param abilityContext
     * @return
     */
    public static PixelMapRequest with(AbilityContext abilityContext) {
        return new PixelMapRequest(abilityContext);
    }

}
