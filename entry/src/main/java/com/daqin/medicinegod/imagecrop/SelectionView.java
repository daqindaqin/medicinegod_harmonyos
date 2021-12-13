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
import com.daqin.medicinegod.imagecrop.*;
import com.daqin.medicinegod.imagecrop.handler.OnBoxChangedListener;
import com.daqin.medicinegod.imagecrop.model.ScalableBox;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

import java.util.ArrayList;
import java.util.List;

public class SelectionView extends Component implements Component.LayoutRefreshedListener, Component.TouchEventListener, Component.DrawTask {
    private static final HiLogLabel SELECTION_VIEW = new HiLogLabel(HiLog.LOG_APP, 0x00201, "BOX SELECTION VIEW");
    private OnBoxChangedListener onBoxChangedListener;
    private com.daqin.medicinegod.imagecrop.EditableImage editableImage;

    private int pixelMapWidth;
    private int pixelMapHeight;
    private int originX;
    private int originY;

    private List<ScalableBox> displayBoxes;
    private int prevX;
    private int prevY;

    private int prevBoxX1;
    private int prevBoxX2;
    private int prevBoxY1;
    private int prevBoxY2;

    private final Paint mPaint = new Paint();

    private float lineWidth;
    private float cornerWidth;
    private float cornerLength;
    private float offset;
    private float offset_2;
    private Color lineColor;
    private Color cornerColor;
    private Color dotColor;
    private Color shadowColor;

    // animating parameters
    private boolean animatingExpanding = false;
    private int[] startingCenter = new int[4];
    private int[] targetingBoxes = new int[4];

    public SelectionView(Context context,
                         float lineWidth, float cornerWidth, float cornerLength,
                         Color lineColor, Color cornerColor, Color dotColor, Color shadowColor,
                         com.daqin.medicinegod.imagecrop.EditableImage editableImage) {
        super(context);
        addDrawTask(this);

        setTouchEventListener(this);

        this.editableImage = editableImage;
        this.lineWidth = lineWidth;
        this.cornerWidth = cornerWidth;
        this.cornerLength = cornerLength;
        this.lineColor = lineColor;
        this.cornerColor = cornerColor;
        this.dotColor = dotColor;
        this.shadowColor = shadowColor;

        mPaint.setAntiAlias(true);

        this.displayBoxes = new ArrayList<>();

        offset = lineWidth / 4;
        offset_2 = lineWidth;
    }

    public void resetBoxSize(int pixelMapWidth, int pixelMapHeight) {
        this.pixelMapWidth = pixelMapWidth;
        this.pixelMapHeight = pixelMapHeight;

        int size = Math.min(pixelMapWidth, pixelMapHeight);

        for (ScalableBox displayBox : displayBoxes) {
            displayBox.setX1((getWidth() - size) / 2);
            displayBox.setX2((getWidth() + size) / 2);
            displayBox.setY1((getHeight() - size) / 2);
            displayBox.setY2((getHeight() + size) / 2);
        }
        invalidate();
    }

    public void setBoxSize(com.daqin.medicinegod.imagecrop.EditableImage editableImage, List<ScalableBox> originalBoxes, int widthX, int heightY) {
        int[] fitSize = editableImage.getFitSize();
        this.pixelMapWidth = fitSize[0];
        this.pixelMapHeight = fitSize[1];
        int originX = (widthX - pixelMapWidth) / 2;
        int originY = (heightY - pixelMapHeight) / 2;
        this.originX = originX;
        this.originY = originY;

        setDisplayBoxes(originalBoxes);

        invalidate();
    }

    public void setOnBoxChangedListener(OnBoxChangedListener listener) {
        this.onBoxChangedListener = listener;
    }

    @Override
    public void onRefreshed(Component component) {
        this.setLayoutRefreshedListener(this);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        if (animatingExpanding) {
            expandBox(canvas);
        } else {
            drawShadow(canvas);
            drawLines(canvas);
            drawCorner(canvas);
//            drawDot(canvas); //TODO:drawDot
        }
    }

    private void drawShadow(Canvas canvas) {
        mPaint.setStrokeWidth(0.0f);
        mPaint.setColor(shadowColor);

        if (displayBoxes != null && displayBoxes.size() > 0) {
            ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
            canvas.drawRect(originX, originY, originX + pixelMapWidth, displayBox.getY1(), mPaint);
            canvas.drawRect(originX, displayBox.getY1(), displayBox.getX1(), displayBox.getY2(), mPaint);
            canvas.drawRect(displayBox.getX2(), displayBox.getY1(), originX + pixelMapWidth, displayBox.getY2(), mPaint);
            canvas.drawRect(originX, displayBox.getY2(), originX + pixelMapWidth, originY + pixelMapWidth, mPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);

        if (displayBoxes != null && displayBoxes.size() > 0) {
            ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
            canvas.drawLine(displayBox.getX1(), displayBox.getY1(), displayBox.getX2(), displayBox.getY1(), mPaint);
            canvas.drawLine(displayBox.getX2(), displayBox.getY1(), displayBox.getX2(), displayBox.getY2(), mPaint);
            canvas.drawLine(displayBox.getX2(), displayBox.getY2(), displayBox.getX1(), displayBox.getY2(), mPaint);
            canvas.drawLine(displayBox.getX1(), displayBox.getY2(), displayBox.getX1(), displayBox.getY1(), mPaint);
        }
    }

    private void drawCorner(Canvas canvas) {
        mPaint.setStrokeWidth(cornerWidth);
        mPaint.setColor(cornerColor);

        if (displayBoxes != null && displayBoxes.size() > 0) {
            ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());
            int x1 = displayBox.getX1();
            int x2 = displayBox.getX2();
            int y1 = displayBox.getY1();
            int y2 = displayBox.getY2();

            int minSize = (int) cornerLength;

            canvas.drawLine(x1 - offset_2, y1 - offset, x1 - offset + minSize, y1 - offset, mPaint);
            canvas.drawLine(x1 - offset, y1 - offset_2, x1 - offset, y1 - offset + minSize, mPaint);

            canvas.drawLine(x2 + offset_2, y1 - offset, x2 + offset - minSize, y1 - offset, mPaint);
            canvas.drawLine(x2 + offset, y1 - offset_2, x2 + offset, y1 - offset + minSize, mPaint);

            canvas.drawLine(x1 - offset_2, y2 + offset, x1 - offset + minSize, y2 + offset, mPaint);
            canvas.drawLine(x1 - offset, y2 + offset_2, x1 - offset, y2 + offset - minSize, mPaint);

            canvas.drawLine(x2 + offset_2, y2 + offset, x2 + offset - minSize, y2 + offset, mPaint);
            canvas.drawLine(x2 + offset, y2 + offset_2, x2 + offset, y2 + offset - minSize, mPaint);
        }
    }

    private void expandBox(Canvas canvas) {
        int step = 10;
        float aspectRation = (targetingBoxes[3] - targetingBoxes[2]) * 1.0f / (targetingBoxes[1] - targetingBoxes[0]);
        startingCenter[0] = startingCenter[0] - step;
        startingCenter[1] = startingCenter[1] + step;
        startingCenter[2] = (int) (startingCenter[2] - step * aspectRation);
        startingCenter[3] = (int) (startingCenter[3] + step * aspectRation);


        if (startingCenter[0] <= targetingBoxes[0] || startingCenter[1] >= targetingBoxes[1]
                || startingCenter[2] <= targetingBoxes[2] || startingCenter[3] >= targetingBoxes[3]) {
            startingCenter[0] = targetingBoxes[0];
            startingCenter[1] = targetingBoxes[1];
            startingCenter[2] = targetingBoxes[2];
            startingCenter[3] = targetingBoxes[3];

            animatingExpanding = false;
        }

        // add shade
        mPaint.setStrokeWidth(0.0f);
        mPaint.setColor(shadowColor);
        canvas.drawRect(new RectFloat(originX, originY, originX + pixelMapWidth, originY + pixelMapHeight), mPaint);

        // draw box
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setColor(lineColor);
        int x1 = startingCenter[0];
        int x2 = startingCenter[1];
        int y1 = startingCenter[2];
        int y2 = startingCenter[3];
        int minSize = (int) cornerLength;
        canvas.drawLine(x1, y1, x2, y1, mPaint);
        canvas.drawLine(x2, y1, x2, y2, mPaint);
        canvas.drawLine(x2, y2, x1, y2, mPaint);
        canvas.drawLine(x1, y2, x1, y1, mPaint);

        // draw corner
        mPaint.setColor(cornerColor);
        canvas.drawLine(x1 - offset_2, y1 - offset, x1 - offset + minSize, y1 - offset, mPaint);
        canvas.drawLine(x1 - offset, y1 - offset_2, x1 - offset, y1 - offset + minSize, mPaint);
        canvas.drawLine(x2 + offset_2, y1 - offset, x2 + offset - minSize, y1 - offset, mPaint);
        canvas.drawLine(x2 + offset, y1 - offset_2, x2 + offset, y1 - offset + minSize, mPaint);
        canvas.drawLine(x1 - offset_2, y2 + offset, x1 - offset + minSize, y2 + offset, mPaint);
        canvas.drawLine(x1 - offset, y2 + offset_2, x1 - offset, y2 + offset - minSize, mPaint);
        canvas.drawLine(x2 + offset_2, y2 + offset, x2 + offset - minSize, y2 + offset, mPaint);
        canvas.drawLine(x2 + offset, y2 + offset_2, x2 + offset, y2 + offset - minSize, mPaint);
        invalidate();
    }

    private void setUpExpanding(int centerX, int centerY, ScalableBox dot) {
        animatingExpanding = true;
        int step = 1;

        float aspectRation = (targetingBoxes[3] - targetingBoxes[2]) * 1.0f / (targetingBoxes[1] - targetingBoxes[0]);
        startingCenter[0] = centerX - step;
        startingCenter[1] = centerX + step;
        startingCenter[2] = (int) (centerY - step * aspectRation);
        startingCenter[3] = (int) (centerY + step * aspectRation);

        targetingBoxes[0] = dot.getX1();
        targetingBoxes[1] = dot.getX2();
        targetingBoxes[2] = dot.getY1();
        targetingBoxes[3] = dot.getY2();
    }

    private void setDisplayBoxes(List<ScalableBox> originalBoxes) {
        displayBoxes.clear();
        for (ScalableBox originalBox : originalBoxes) {
            ScalableBox displayBox = new ScalableBox(originalBox.getX1(), originalBox.getY1(), originalBox.getX2(), originalBox.getY2());

            if (originalBox.getX1() >= 0
                    && originalBox.getX2() > 0
                    && originalBox.getY1() >= 0
                    && originalBox.getY2() > 0) {

                HiLog.debug(SELECTION_VIEW,
                        "original box: + (" + originalBox.getX1() + " " + originalBox.getY1() + ")"
                                + " (" + originalBox.getX2() + " " + originalBox.getY2() + ")");


                float scale = ((float) editableImage.getFitSize()[0]) / editableImage.getActualSize()[0];
                int scaleX1 = (int) Math.ceil((originalBox.getX1() * scale) + originX);
                int scaleX2 = (int) Math.ceil((originalBox.getX2() * scale) + originX);
                int scaleY1 = (int) Math.ceil((originalBox.getY1() * scale) + originY);
                int scaleY2 = (int) Math.ceil((originalBox.getY2() * scale) + originY);

                //resize the box size to image
                displayBox.setX1(scaleX1);
                displayBox.setX2(scaleX2);
                displayBox.setY1(scaleY1);
                displayBox.setY2(scaleY2);
            } else {
                displayBox.setX1(originX);
                displayBox.setX2(originX + pixelMapWidth);
                displayBox.setY1(originY);
                displayBox.setY2(originY + pixelMapHeight);
            }
            displayBoxes.add(displayBox);
        }
    }

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void drawDot(Canvas canvas) {
        mPaint.setStrokeWidth(cornerWidth);
        int dotSize = 20;
        float dotSizeOuterRatio = 1.5f;

        for (ScalableBox dot : displayBoxes) {
            if (displayBoxes.indexOf(dot) != editableImage.getActiveBoxIdx()) {
                int centerX = (dot.getX1() + dot.getX2()) / 2;
                int centerY = (dot.getY1() + dot.getY2()) / 2;

                mPaint.setColor(dotColor);
                canvas.drawOval(
                        new RectFloat(centerX - dotSize * dotSizeOuterRatio,
                                centerY - dotSize * dotSizeOuterRatio,
                                centerX + dotSize * dotSizeOuterRatio,
                                centerY + dotSize * dotSizeOuterRatio), mPaint);
                mPaint.setColor(new Color(0xffffff));
                canvas.drawOval(new RectFloat(centerX - dotSize, centerY - dotSize,
                        centerX + dotSize, centerY + dotSize), mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        int[] loc = getLocationOnScreen();
        int curX = (int) touchEvent.getPointerScreenPosition(0).getX();
        int curY = (int) touchEvent.getPointerScreenPosition(0).getY();

        if (animatingExpanding) {
            return false;
        }

        // or box scaling and moving
        int activeIdx = editableImage.getActiveBoxIdx();
        if (activeIdx < 0) {
            return false;
        }

        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                prevX = curX;
                prevY = curY;

                prevBoxX1 = editableImage.getActiveBox().getX1();
                prevBoxX2 = editableImage.getActiveBox().getX2();
                prevBoxY1 = editableImage.getActiveBox().getY1();
                prevBoxY2 = editableImage.getActiveBox().getY2();

                return true;

            case TouchEvent.POINT_MOVE:
                int diffX = curX - prevX;
                int diffY = curY - prevY;

                displayBoxes.get(activeIdx).resizeBox(curX - loc[0], curY - loc[1], diffX, diffY,
                        (getWidth() - pixelMapWidth) / 2,
                        (getHeight() - pixelMapHeight) / 2,
                        (getWidth() + pixelMapWidth) / 2,
                        (getHeight() + pixelMapHeight) / 2,
                        (int) cornerLength);
                updateOriginalBox();

                invalidate();

                prevX = curX;
                prevY = curY;
                return true;

            case TouchEvent.PRIMARY_POINT_UP:
                // check click on dot
                for (ScalableBox dot : displayBoxes) {
                    if (displayBoxes.indexOf(dot) != editableImage.getActiveBoxIdx()) {
                        int buffer = 25;
                        int x1 = dot.getX1();
                        int x2 = dot.getX2();
                        int y1 = dot.getY1();
                        int y2 = dot.getY2();
                        int dotX = (x1 + x2) / 2;
                        int dotY = (y1 + y2) / 2;
                        int pointX = curX - loc[0];
                        int pointY = curY - loc[1];

                        if ((dotX - buffer <= pointX) && (pointX <= dotX + buffer) &&
                                (dotY - buffer <= pointY) && (pointY <= dotY + buffer)
                        ) {
                            // expand the box
                            setUpExpanding(dotX, dotY, dot);
                            editableImage.setActiveBoxIdx(displayBoxes.indexOf(dot));
                            invalidate();

                            if (onBoxChangedListener != null) {
                                onBoxChangedListener.onChanged(
                                        editableImage.getActiveBox().getX1(),
                                        editableImage.getActiveBox().getY1(),
                                        editableImage.getActiveBox().getX2(),
                                        editableImage.getActiveBox().getY2());
                            }
                            // reset the display box
                            setDisplayBoxes(editableImage.getBoxes());
                            return false;
                        }
                    }
                }
                ScalableBox originalBox = editableImage.getActiveBox();
                if (onBoxChangedListener != null
                        && (prevBoxX1 != originalBox.getX1()
                        || prevBoxX2 != originalBox.getX2()
                        || prevBoxY1 != originalBox.getY1()
                        || prevBoxY2 != originalBox.getY2())) {
                    onBoxChangedListener.onChanged(originalBox.getX1(), originalBox.getY1(), originalBox.getX2(), originalBox.getY2());
                }

                prevBoxX1 = originalBox.getX1();
                prevBoxX2 = originalBox.getX2();
                prevBoxY1 = originalBox.getY1();
                prevBoxY2 = originalBox.getY2();
                return true;
            default:
                return false;
        }
    }

    /**
     * Calculate the relative position of the box w.r.t the bitmap size
     * Return a new box that can be used in uploading
     */
    public void updateOriginalBox() {
        int viewWidth = editableImage.getViewWidth();
        int viewHeight = editableImage.getViewHeight();
        int width = editableImage.getOriginalPixelMap().getImageInfo().size.width;
        int height = editableImage.getOriginalPixelMap().getImageInfo().size.height;
        ScalableBox displayBox = displayBoxes.get(editableImage.getActiveBoxIdx());

        float ratio = width / (float) height;
        float viewRatio = viewWidth / (float) viewHeight;
        float factor;

        //width dominate, fit w
        if (ratio > viewRatio) {
            factor = viewWidth / (float) width;
        } else {
            //height dominate, fit h
            factor = viewHeight / (float) height;
        }

        float coorX, coorY;
        coorX = (viewWidth - width * factor) / 2f;
        coorY = (viewHeight - height * factor) / 2f;

        int originX1 = (displayBox.getX1() - coorX) / factor <= width ? (int) ((displayBox.getX1() - coorX) / factor) : width;
        int originY1 = (displayBox.getY1() - coorY) / factor <= height ? (int) ((displayBox.getY1() - coorY) / factor) : height;
        int originX2 = (displayBox.getX2() - coorX) / factor <= width ? (int) ((displayBox.getX2() - coorX) / factor) : width;
        int originY2 = (displayBox.getY2() - coorY) / factor <= height ? (int) ((displayBox.getY2() - coorY) / factor) : height;
        editableImage.getActiveBox().setX1(originX1);
        editableImage.getActiveBox().setY1(originY1);
        editableImage.getActiveBox().setX2(originX2);
        editableImage.getActiveBox().setY2(originY2);
    }
}
