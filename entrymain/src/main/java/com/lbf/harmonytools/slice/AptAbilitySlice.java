package com.lbf.harmonytools.slice;

import com.lbf.annotations.inject.v2.BindViewV2;
import com.lbf.annotations.inject.v2.UiContentV2;
import com.lbf.annotations.router.HRouter;
import com.lbf.entrycommon.Constant;
import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.imageloader.ImageLoader;
import com.lbf.lib.inject.InjectHelperV2;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.Text;

@HRouter(Constant.RouterName.EntryMainAptAbilitySlice)
@UiContentV2(ResourceTable.Layout_ability_apt)
public class AptAbilitySlice extends AbilitySlice {

    @BindViewV2(ResourceTable.Id_img)
    Image image;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelperV2.Inject(this);
        ImageLoader.with(this).load("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png").loading(ResourceTable.Media_icon).into(image);
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelperV2.UnInject(this);
    }
}
