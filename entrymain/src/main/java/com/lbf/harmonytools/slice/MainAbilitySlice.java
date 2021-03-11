package com.lbf.harmonytools.slice;

import com.lbf.annotations.inject.v1.BindView;
import com.lbf.annotations.inject.v1.OnClick;
import com.lbf.annotations.inject.v1.UiContent;
import com.lbf.annotations.router.HRouter;
import com.lbf.entrycommon.Constant;
import com.lbf.harmonytools.ResourceTable;
import com.lbf.lib.inject.InjectHelper;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

@HRouter(Constant.RouterName.EntryMainMainAbilitySlice)
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
        //com.lbf.lib.router.HRouter.NewInstance().abilityNavigation(getAbility(), Constant.RouterName.EntryBusiness01MainAbility, new Intent());

        com.lbf.lib.router.HRouter.NewInstance().abilitySliceNavigation(this, Constant.RouterName.EntryMainAptAbilitySlice, new Intent());
    }
}
