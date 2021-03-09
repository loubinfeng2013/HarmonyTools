package com.lbf.harmonytools.slice;

import com.lbf.annotations.V2.BindViewV2;
import com.lbf.annotations.V2.OnClickV2;
import com.lbf.annotations.V2.UiContentV2;
import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.inject.InjectHelperV2;
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
