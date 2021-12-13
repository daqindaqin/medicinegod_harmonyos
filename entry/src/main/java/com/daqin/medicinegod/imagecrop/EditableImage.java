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
package com.daqin.medicinegod.imagecrop;

import com.daqin.medicinegod.imagecrop.model.ScalableBox;
import com.daqin.medicinegod.imagecrop.util.ImageHelper;
import ohos.app.Context;
import ohos.media.image.PixelMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Editable image, manage bitmap rotation, calculate fit size and search box size
 */
public class EditableImage {
    private PixelMap originalPixelMap;
    private int originalPixelMapId = 0;
    private String originalPixelMapPath;
    private List<ScalableBox> originalBoxes;
    private int activeBoxIdx = -1;
    private ScalableBox copyOfActiveBox;

    private int viewWidth;
    private int viewHeight;

    public EditableImage(PixelMap image) {
        originalPixelMap = image;

        //init the search box
        originalBoxes = new ArrayList<>();
    }

    public EditableImage(String localPath) {
        //load image from path to pixel map
        originalPixelMap = ImageHelper.getPixelMapFromPath(localPath);
        originalPixelMapPath = localPath;

        //init the search box
        originalBoxes = new ArrayList<>();
    }



    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public PixelMap getOriginalPixelMap() {
        return originalPixelMap;
    }

    public void setBoxes(List<ScalableBox> boxes) {
        if (boxes != null && boxes.size() > 0) {
            this.activeBoxIdx = 0;
            setBoxes(boxes, this.activeBoxIdx);
        }
    }

    public void setBoxes(List<ScalableBox> boxes, int activeBoxIdx) {
        if (boxes != null && boxes.size() > 0) {
            this.originalBoxes = boxes;
            try {
                copyOfActiveBox = (ScalableBox) originalBoxes.get(activeBoxIdx).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                copyOfActiveBox = new ScalableBox();
                copyOfActiveBox.setX1(originalBoxes.get(activeBoxIdx).getX1());
                copyOfActiveBox.setX2(originalBoxes.get(activeBoxIdx).getX2());
                copyOfActiveBox.setY1(originalBoxes.get(activeBoxIdx).getY1());
                copyOfActiveBox.setY2(originalBoxes.get(activeBoxIdx).getY2());
            }
        }
    }

    public List<ScalableBox> getBoxes() {
        return originalBoxes;
    }

    public int getActiveBoxIdx() {
        return activeBoxIdx;
    }

    public void setActiveBoxIdx(int activeBoxIdx) {
        this.activeBoxIdx = activeBoxIdx;
        try {
            copyOfActiveBox = (ScalableBox) originalBoxes.get(activeBoxIdx).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            copyOfActiveBox = new ScalableBox();
            copyOfActiveBox.setX1(originalBoxes.get(activeBoxIdx).getX1());
            copyOfActiveBox.setX2(originalBoxes.get(activeBoxIdx).getX2());
            copyOfActiveBox.setY1(originalBoxes.get(activeBoxIdx).getY1());
            copyOfActiveBox.setY2(originalBoxes.get(activeBoxIdx).getY2());
        }
    }

    public ScalableBox getActiveBox() {
        return copyOfActiveBox;
    }

    public void rotateOriginalImage(Context context, int degree) {
        if (originalPixelMapId != 0) {
            originalPixelMap = ImageHelper.rotateImage(originalPixelMapId, context, degree);
        } else if (!originalPixelMapPath.equals("")) {
            originalPixelMap = ImageHelper.rotateImage(originalPixelMapPath, context, degree);
        }
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    /**
     * get the size of the bitmap when it is fit to view display
     *
     * @return width and height as int[]
     */
    public int[] getFitSize() {
        int[] fitSize = new int[2];

        float ratio = originalPixelMap.getImageInfo().size.width / (float) originalPixelMap.getImageInfo().size.height;
        float viewRatio = viewWidth / (float) viewHeight;

        //width dominate, fit w
        if (ratio > viewRatio) {
            float factor = viewWidth / (float) originalPixelMap.getImageInfo().size.width;
            fitSize[0] = viewWidth;
            fitSize[1] = (int) (originalPixelMap.getImageInfo().size.height * factor);
        } else {
            //height dominate, fit h
            float factor = viewHeight / (float) originalPixelMap.getImageInfo().size.height;
            fitSize[0] = (int) (originalPixelMap.getImageInfo().size.width * factor);
            fitSize[1] = viewHeight;
        }

        return fitSize;
    }

    /**
     * get actual size of the image
     *
     * @return int array size[0] is width, size[1] is height
     */
    public int[] getActualSize() {
        int[] actualSize = new int[2];

        actualSize[0] = originalPixelMap.getImageInfo().size.width;
        actualSize[1] = originalPixelMap.getImageInfo().size.height;

        return actualSize;
    }

    public String cropOriginalImage(String path, String imageName) {
        ScalableBox relativeBox = getActiveBox();
        return ImageHelper.saveImageCropToPath(originalPixelMap,
                relativeBox.getX1(), relativeBox.getY1(), relativeBox.getX2(), relativeBox.getY2(),
                path, imageName);
    }

    public PixelMap cropOriginalImage() {
        ScalableBox relativeBox = getActiveBox();
        return ImageHelper.cropImage(originalPixelMap,
                relativeBox.getX1(), relativeBox.getY1(), relativeBox.getX2(), relativeBox.getY2());
    }
}
