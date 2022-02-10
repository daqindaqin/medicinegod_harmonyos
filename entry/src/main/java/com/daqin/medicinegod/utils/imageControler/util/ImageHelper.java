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
package com.daqin.medicinegod.utils.imageControler.util;


import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Rect;
import ohos.media.image.common.ScaleMode;
import ohos.media.image.common.Size;

public class ImageHelper {
    public static PixelMap rotateImage(byte[] img, int i) {
            // 创建图像数据源ImageSource对象
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/jpg";
            ImageSource imageSource = ImageSource.create(img,srcOpts);
            // 设置图片参数
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            // 旋转
            decodingOptions.rotateDegrees = 90 * i;
            decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;
            return imageSource.createPixelmap(decodingOptions);
    }



    public static PixelMap cropImage(PixelMap pixelMap, int x1, int y1, int x2, int y2) {
        if (y2 - y1 > pixelMap.getImageInfo().size.height) {
            y2 = pixelMap.getImageInfo().size.height + y1;
        }
        if (x2 - x1 > pixelMap.getImageInfo().size.width) {
            x2 = pixelMap.getImageInfo().size.width + x1;
        }

        PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
        options.size = new Size(x2 - x1, y2 - y1);
        options.scaleMode = ScaleMode.FIT_TARGET_SIZE;
        return PixelMap.create(pixelMap, new Rect(x1, y1, x2 - x1, y2 - y1), options);
    }
}
