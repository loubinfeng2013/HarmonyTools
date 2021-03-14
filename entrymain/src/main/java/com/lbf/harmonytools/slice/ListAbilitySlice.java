package com.lbf.harmonytools.slice;

import com.lbf.annotations.inject.v2.BindViewV2;
import com.lbf.annotations.inject.v2.UiContentV2;
import com.lbf.annotations.router.HRouter;
import com.lbf.entrycommon.Constant;
import com.lbf.harmonytools.ResourceTable;
import com.lbf.harmonytools.provider.ListItemProvider;
import com.lbf.lib.inject.InjectHelperV2;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ListContainer;

import java.util.ArrayList;
import java.util.List;

@HRouter(Constant.RouterName.EntryMainListAbilitySlice)
@UiContentV2(ResourceTable.Layout_ability_list)
public class ListAbilitySlice extends AbilitySlice {

    @BindViewV2(ResourceTable.Id_list)
    ListContainer listContainer;

    private List<String> urls = new ArrayList<>();

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        InjectHelperV2.Inject(this);

        urls.add("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        urls.add("https://sf3-ttcdn-tos.pstatp.com/img/mosaic-legacy/3792/5112637127~300x300.image");


        listContainer.setItemProvider(new ListItemProvider(urls, this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        InjectHelperV2.UnInject(this);
    }
}
