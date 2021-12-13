package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.imagecrop.EditPhotoView;
import com.daqin.medicinegod.imagecrop.EditableImage;
import com.daqin.medicinegod.imagecrop.model.ScalableBox;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.ability.PathMatcher;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageCropAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_imagecrop);
        final String path = intent.getStringParam("startcropimage");
        final EditPhotoView imageView = (EditPhotoView) findComponentById(ResourceTable.Id_editable_image);

        final EditableImage editimage = new EditableImage(path);
//        List<ScalableBox> boxes = new ArrayList<>();

//        boxes.add(new ScalableBox(2, 18, 880, 680));

//        editimage.setBoxes(boxes);

//        imageView.initView(this, editimage);

    }

/*
        Button rotateButton = (Button) findComponentById(ResourceTable.Id_imagecrop_btn_rotate);
        rotateButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                imageView.rotateImageView();
            }
        });

        Button cropButton = (Button) findComponentById(ResourceTable.Id_imagecrop_btn_crop);
        cropButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                PixelMap croppedImage = editimage.cropOriginalImage();
//                ((Image) findComponentById(ResourceTable.Id_cropped_image)).setPixelMap(croppedImage);
                Intent newIntent = new Intent();
                newIntent.setParam("cropedimage", croppedImage);
                present(new SecondAbilitySlice(), newIntent);
            }
        });
    }
*/

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
