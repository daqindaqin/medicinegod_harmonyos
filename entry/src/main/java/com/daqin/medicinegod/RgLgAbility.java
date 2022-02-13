package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.RgLgAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class RgLgAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(RgLgAbilitySlice.class.getName());
    }
}
