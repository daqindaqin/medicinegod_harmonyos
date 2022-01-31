package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.EditAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class EditAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(EditAbilitySlice.class.getName());

    }
}
