/*
 *    Copyright 2016 Yu Lu
 *    Copyright 2021 Institute of Software Chinese Academy of Sciences, ISRC

 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.daqin.medicinegod.utils.imageControler;

import com.daqin.medicinegod.utils.imageControler.handler.OnBoxChangedListener;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.agp.window.service.DisplayManager;
import ohos.app.Context;

import java.util.Optional;

public class EditPhotoView extends StackLayout {
    private static final int LINE_WIDTH = 2;
    private static final int CORNER_LENGTH = 30;

    private int mWidth;
    private int mHeight;

    private Image imageView;
    private SelectionView selectionView;
    private EditableImage editableImage;

    private float lineWidth;
    private float cornerWidth;
    private float cornerLength;
    private Color lineColor;
    private Color cornerColor;
    private Color dotColor;
    private Color shadowColor;
    public EditPhotoView(Context context) {
        super(context);
        mContext = context;
    }

    public EditPhotoView(Context context, AttrSet attrs) {
        super(context, attrs);
        mContext = context;
        obtainAttributes(attrs);
    }


    public EditPhotoView(Context context, AttrSet attrs, String defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        obtainAttributes(attrs);
    }

    /**
     * update view with editable image
     *
     * @param context       activity
     * @param editableImage image to be edited
     */
    public void initView(Context context, EditableImage editableImage) {
        this.editableImage = editableImage;
        this.mContext = context;
        selectionView = new SelectionView(context,
                lineWidth, cornerWidth, cornerLength,
                lineColor, cornerColor, dotColor, shadowColor, editableImage);
        imageView = new Image(context);

        imageView.setLayoutConfig(new LayoutConfig(LayoutConfig.MATCH_PARENT, LayoutConfig.MATCH_PARENT));
        selectionView.setLayoutConfig(new LayoutConfig(LayoutConfig.MATCH_PARENT, LayoutConfig.MATCH_PARENT));

        addComponent(imageView, 0);
        addComponent(selectionView, 1);
//        this.setLayoutRefreshedListener(this);

        if (editableImage != null) {
            editableImage.setViewSize(mWidth, mHeight);
            imageView.setPixelMap(editableImage.getOriginalPixelMap());
            imageView.setScaleMode(Image.ScaleMode.ZOOM_CENTER);
            selectionView.setBoxSize(editableImage, editableImage.getBoxes(), mWidth, mHeight);
        }
    }

    public void setOnBoxChangedListener(OnBoxChangedListener onBoxChangedListener) {
        selectionView.setOnBoxChangedListener(onBoxChangedListener);
    }

    /**
     * rotate image
     */
    public void rotateImageView(byte[] img,int i) {
        if (editableImage.getActiveBoxIdx() >= 0) {
            //rotate bitmap
            editableImage.rotateOriginalImage(img, i);
            //re-calculate and draw selection box
            editableImage.getActiveBox().setX1(0);
            editableImage.getActiveBox().setY1(0);
            editableImage.getActiveBox().setX2(editableImage.getActualSize()[0]);
            editableImage.getActiveBox().setY2(editableImage.getActualSize()[1]);
            selectionView.setBoxSize(editableImage, editableImage.getBoxes(), editableImage.getViewWidth(), editableImage.getViewHeight());
            imageView.setPixelMap(editableImage.getOriginalPixelMap());

        }
    }

    private void obtainAttributes(AttrSet attrs) {
        if (0 == (mHeight = getHeight())) {    //height为match_parent时
            mHeight = DisplayManager.getInstance().
                    getDefaultDisplay(mContext).get().getAttributes().height;
        }
        if (0 == (mWidth = getWidth())) { //width为match_parent时
            mWidth = DisplayManager.getInstance().
                    getDefaultDisplay(mContext).get().getAttributes().width;
        }
        lineWidth = AttrHelper.vp2px(LINE_WIDTH, mContext);
        lineColor = new Color(0xFFFFFF);
        dotColor = new Color(0xFFFFFF);
        cornerWidth = AttrHelper.vp2px(LINE_WIDTH * 2, mContext);
        cornerLength = AttrHelper.vp2px(CORNER_LENGTH, mContext);
        cornerColor = new Color(0xFFFFFF);
        shadowColor = new Color(0xAA111111);

        for (int i = 0; i < attrs.getLength(); i++) {
            Optional<Attr> attr = attrs.getAttr(i);
            if (attr.isPresent()) {
                switch (attr.get().getName()) {
                    case "left_padding":
                    case "right_padding":
                        mWidth -= attr.get().getDimensionValue();
                        break;
                    case "crop_line_width":
                        lineWidth = attr.get().getDimensionValue();
                        break;
                    case "crop_line_color":
                        lineColor = attr.get().getColorValue();
                        break;
                    case "crop_dot_color":
                        dotColor = attr.get().getColorValue();
                        break;
                    case "crop_corner_width":
                        cornerWidth = attr.get().getDimensionValue();
                        break;
                    case "crop_corner_length":
                        cornerLength = attr.get().getDimensionValue();
                        break;
                    case "crop_corner_color":
                        cornerColor = attr.get().getColorValue();
                        break;
                    case "crop_shadow_color":
                        shadowColor = attr.get().getColorValue();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

