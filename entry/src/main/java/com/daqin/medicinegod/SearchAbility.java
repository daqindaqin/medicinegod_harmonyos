package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.DetailAbilitySlice;
import com.daqin.medicinegod.slice.SearchAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class SearchAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(SearchAbilitySlice.class.getName());

    }
}
