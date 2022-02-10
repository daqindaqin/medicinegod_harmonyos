package com.daqin.medicinegod.utils.imageControler;

import ohos.aafwk.ability.Ability;

/**
 * 后台传输byte数据
 */
public class ImageSaver extends Ability {
    private static ImageSaver instance;

    public ImageSaver() {}

    public static ImageSaver getInstance() {
        return instance;
    }

    public void setInstance() {
        instance = this;
    }

    private byte[] pixelmap;

    public byte[] getByte() {
        return pixelmap;
    }

    public void setByte(byte[] img) {
        this.pixelmap = img;
    }

}

