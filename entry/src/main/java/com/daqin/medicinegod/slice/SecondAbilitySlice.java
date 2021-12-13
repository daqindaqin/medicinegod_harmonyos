package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;

public class SecondAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_second);

        Image cropped_image = (Image) findComponentById(ResourceTable.Id_cropped_image);
        cropped_image.setPixelMap(intent.getSequenceableParam("cropedimage"));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
