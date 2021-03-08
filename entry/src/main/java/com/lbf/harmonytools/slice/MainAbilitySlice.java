package com.lbf.harmonytools.slice;

import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.inject.InjectHelper;
import com.lbf.lib.inject.annotations.BindView;
import com.lbf.lib.inject.annotations.OnClick;
import com.lbf.lib.inject.annotations.UiContent;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

@UiContent(ResourceTable.Layout_ability_main)
public class MainAbilitySlice extends AbilitySlice {

    @BindView(ResourceTable.Id_text_helloworld)
    private Text text;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelper.Inject(this);
        text.setText("hm2021");
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelper.UnInject(this);
    }

    @OnClick(ResourceTable.Id_text_helloworld)
    private void clickText(Component component) {
        present(new AptAbilitySlice(), new Intent());
    }
}
