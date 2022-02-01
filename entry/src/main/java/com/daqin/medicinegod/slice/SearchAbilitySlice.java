package com.daqin.medicinegod.slice;
import com.daqin.medicinegod.ResourceTable;


import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;


public class SearchAbilitySlice extends AbilitySlice {
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_search);
    }
}
