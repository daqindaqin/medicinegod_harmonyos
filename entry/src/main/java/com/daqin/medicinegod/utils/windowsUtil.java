package com.daqin.medicinegod.utils;

import ohos.aafwk.ability.AbilitySlice;
import ohos.app.Context;


public class windowsUtil extends AbilitySlice {
    public static int getWindowHeightPx(Context context) {
        return context.getResourceManager().getDeviceCapability().height * context.getResourceManager().getDeviceCapability().screenDensity / 160;
    }
    public static int getWindowWidthPx(Context context) {
        return context.getResourceManager().getDeviceCapability().width * context.getResourceManager().getDeviceCapability().screenDensity / 160;
    }
}
