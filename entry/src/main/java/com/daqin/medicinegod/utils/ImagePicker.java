package com.daqin.medicinegod.utils;

import com.zzti.fengyongge.imagepicker.ImagePickerInstance;
import com.zzti.fengyongge.imagepicker.PhotoPreviewAbility;
import com.zzti.fengyongge.imagepicker.PhotoSelectorAbility;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.app.Context;

import java.util.ArrayList;

import static com.zzti.fengyongge.imagepicker.ImagePickerInstance.*;


public class ImagePicker {
    //获取单例，调用下面方法即可，具体可参考源码sample
//    ImagePickerInstance.getInstance ();

    /**
     * 对外图库选择图片,或者拍照选择图片方法
     * @param abilitySlice
     * @param limit  选择图片张数
     * @param isShowCamera 是否支持拍照
     * @param requestCode
     */
    public static void photoSelect(AbilitySlice abilitySlice, int limit, boolean isShowCamera, int requestCode) {
        Intent intent = new Intent();
        intent.setParam(LIMIT, limit);
        intent.setParam(IS_SHOW_CAMERA, false);
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(abilitySlice.getBundleName())
                .withAbilityName(PhotoSelectorAbility.class)
                .build();
        intent.setOperation(operation);
        abilitySlice.startAbilityForResult(intent, requestCode);
    }


    /**
     * 对外开放的图片预览方法
     * @param context
     * @param tempList 浏览图片集合，注意！必须封装成imagepicker的bean，url支持网络或者本地
     * @param position  角标
     * @param isSave 是否支持保存
     */
    public void photoPreview(Context context, ArrayList<PhotoModel> tempList, int position, boolean isSave) {
        Intent intent = new Intent();
        intent.setSequenceableArrayListParam(PHOTOS, tempList);
        intent.setParam(POSITION, position);
        intent.setParam(IS_SAVE, isSave);
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(context.getBundleName())
                .withAbilityName(PhotoPreviewAbility.class)
                .build();
        intent.setOperation(operation);
        context.startAbility(intent, 0);
    }

}
