package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.imageControler.EditPhotoView;
import com.daqin.medicinegod.utils.imageControler.EditableImage;
import com.daqin.medicinegod.utils.imageControler.model.ScalableBox;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.utils.util;
import com.zzrv5.mylibrary.ZZRCallBack;
import com.zzrv5.mylibrary.ZZRHttp;
import ohos.aafwk.ability.*;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.media.image.PixelMap;

import java.util.*;

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

        boxes.add(new ScalableBox(20, 20, 800, 450));

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



//                String bs64 = util.pixelMap2Base64(croppedImage);
//                System.out.println(bs64);
//                String url = "https://api.cloudinary.com/v1_1/wfgmqhx/image/upload";
//                Map<String, String> map = new HashMap<>();
//                map.put("public_id", "222");
//                map.put("upload_preset", "mgupload");
//                map.put("file", "data:image/jpg;base64,"+bs64);
//
//                ZZRHttp.post(url,map, new ZZRCallBack.CallBackString() {
//                    @Override
//                    public void onFailure(int i, String s) {
//                        System.out.println("API返回失败\n"+s);
//                    }
//                    @Override
//                    public void onResponse(String s) {
//                        System.out.println("API返回成功\n"+s);
//                        // 如果返回成功，返回的结果就会保存在 String s 中。
//                        // s = {"code":0,"message":"0","ttl":1,"data":{"mid":383565952,"following":70,"whisper":0,"black":0,"follower":5384}}
//                    }
//                });


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
