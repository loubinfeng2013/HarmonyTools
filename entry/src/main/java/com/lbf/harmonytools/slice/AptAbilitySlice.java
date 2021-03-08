package com.lbf.harmonytools.slice;

import com.lbf.annotations.BindViewV2;
import com.lbf.annotations.OnClickV2;
import com.lbf.annotations.UiContentV2;
import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.injectv2.InjectHelperV2;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

@UiContentV2(ResourceTable.Layout_ability_apt)
public class AptAbilitySlice extends AbilitySlice {

    @BindViewV2(ResourceTable.Id_text_apt)
    Text aptText;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelperV2.Inject(this);
        aptText.setText("apt222");
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelperV2.UnInject(this);
    }

    @OnClickV2(ResourceTable.Id_text_apt)
    void clickText(Component component) {
        ((Text) component).setText("apt33333");
    }
}
