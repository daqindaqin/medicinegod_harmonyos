package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.imagecrop.EditPhotoView;
import com.daqin.medicinegod.imagecrop.EditableImage;
import com.daqin.medicinegod.imagecrop.model.ScalableBox;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.*;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageCropAbilitySlice extends AbilitySlice {

    private static final float ROTATION_180 = 180;
    private static final float ROTATION_0 = 0;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_imagecrop);
        final byte[] bytes = intent.getByteArrayParam("startcropimage");
        final EditPhotoView imageView = (EditPhotoView) findComponentById(ResourceTable.Id_editable_image);

        final EditableImage editimage = new EditableImage(util.byte2PixelmapImage(bytes)) ;
        List<ScalableBox> boxes = new ArrayList<>();

        boxes.add(new ScalableBox(2, 18, 880, 680));

        editimage.setBoxes(boxes);

        imageView.initView(this, editimage);
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
//                Intent newIntent = new Intent();
//                newIntent.setParam("cropedimage", croppedImage);
//                present(new SecondAbilitySlice(), newIntent);
                //TODO:修复这里不能用的BUG
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String param = "public_id=1234&upload_preset=mghead&file=data:image/jpg;base64," +
                                util.pixelMap2BASE64(croppedImage);
                        System.out.println(param);
                        String sr = util.sendPost("https://api.cloudinary.com/v1_1/wfgmqhx/image/upload", param);
                        System.out.println("post=" + sr);
                    }
                }).start();




                Intent intent = new Intent();
                intent.setParam("cropedimage", croppedImage);
                getAbility().setResult(101, intent);
                terminate();
//                Intent intent = new Intent();
//                intent.setParam("cropedimage", croppedImage);
//                getContext().setResult(101, intent);
//                terminate();
            }
        });
    }

    /*public final PixelMap displayImage(byte[] bytes){
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();

        srcOpts.formatHint = "image/jpg";
        decodingOptions.rotateDegrees = 0.0f;
        decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;

        return ImageSource.create(bytes, srcOpts).createPixelmap(decodingOptions);

    }*/



    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
