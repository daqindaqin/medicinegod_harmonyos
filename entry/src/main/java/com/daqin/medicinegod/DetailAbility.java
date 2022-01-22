package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.DetailAbilitySlice;
import com.daqin.medicinegod.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class DetailAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(DetailAbilitySlice.class.getName());

    }
}
