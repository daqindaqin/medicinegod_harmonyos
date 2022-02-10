package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.ImageControlAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class ImageControlAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(ImageControlAbilitySlice.class.getName());

    }
}
