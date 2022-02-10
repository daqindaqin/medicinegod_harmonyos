package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.imageControler.EditPhotoView;
import com.daqin.medicinegod.utils.imageControler.EditableImage;
import com.daqin.medicinegod.utils.imageControler.model.ScalableBox;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.*;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.media.image.PixelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageControlAbilitySlice extends AbilitySlice {

    private static final float ROTATION_180 = 180;
    private static final float ROTATION_0 = 0;
    private int whirlCount = 0;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_imagecontrol);


//        final byte[] bytes = intent.getByteArrayParam("startcropimage");
        final byte[] bytes = ImageSaver.getInstance().getByte();

        System.out.println("已经传输" + Arrays.toString(bytes));
        final EditPhotoView imageView = (EditPhotoView) findComponentById(ResourceTable.Id_editable_image);

        final EditableImage editimage = new EditableImage(util.byte2PixelMap(bytes));
        List<ScalableBox> boxes = new ArrayList<>();

        boxes.add(new ScalableBox(2, 18, 880, 680));

        editimage.setBoxes(boxes);

        imageView.initView(this, editimage);
        Button rotateButton_left = (Button) findComponentById(ResourceTable.Id_imageControl_btn_rotate);
        rotateButton_left.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                whirlCount++;
                if (whirlCount > 4) {
                    whirlCount = 1;
                }
                imageView.rotateImageView(bytes, whirlCount);
            }
        });
        Button backButton = (Button) findComponentById(ResourceTable.Id_imageControl_btn_back);
        backButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                terminate();
            }
        });
        Button cropButton = (Button) findComponentById(ResourceTable.Id_imageControl_btn_crop);
        cropButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                PixelMap croppedImage = editimage.cropOriginalImage();
//                ((Image) findComponentById(ResourceTable.Id_cropped_image)).setPixelMap(croppedImage);
                Intent intent = new Intent();
                intent.setParam("cropedimage", "ok");
                ImageSaver.getInstance().setByte(util.pixelMap2byte(croppedImage));
                getAbility().setResult(101, intent);
                terminate();
            }
        });
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
