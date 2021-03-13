package com.lbf.lib.imageloader;

import ohos.app.AbilityContext;

public class ImageLoader {

    public static PixelMapRequest with(AbilityContext abilityContext) {
        return new PixelMapRequest(abilityContext);
    }

}
