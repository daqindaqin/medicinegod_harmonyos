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
package com.daqin.medicinegod.imagecrop.util;

import com.daqin.medicinegod.ResourceTable;
import ohos.app.Context;
import ohos.global.resource.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Rect;
import ohos.media.image.common.ScaleMode;
import ohos.media.image.common.Size;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageHelper {
    private static final HiLogLabel IMAGE_HELPER = new HiLogLabel(HiLog.LOG_APP, 0xD001100, "image helper");

    /**
     * rotate image with degrees
     *
     * @param resourceId pixel map id
     * @param context context
     * @param degree rotate degree
     * @return rotated pixel map
     */
    static int rotateCount = 0;
    public static PixelMap rotateImage(int resourceId, Context context, int degree) {
        try {
            InputStream inputStream = context.getResourceManager().getResource(resourceId);
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/jpg";
            ImageSource imageSource = ImageSource.create(inputStream, srcOpts);

            ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
            options.rotateDegrees = degree * ++rotateCount;

            return imageSource.createPixelmap(options);
        } catch (NotExistException | IOException e) {
            return rotateImage(ResourceTable.Media_no_resource, context, 0);
        }
    }

    public static PixelMap rotateImage(String path, Context context, int degree) {
        try {
            RawFileEntry assetManager = context.getResourceManager().getRawFileEntry(path);
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/jpg";

            Resource asset = assetManager.openRawFile();
            ImageSource imageSource = ImageSource.create(asset, srcOpts);
            ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
            options.rotateDegrees = degree * ++rotateCount;

            return imageSource.createPixelmap(options);
        } catch (IOException e) {
            return rotateImage(ResourceTable.Media_no_resource, context, 0);
        }
    }

    /**
     * load pixel map from path
     *
     * @param filePath path to the image
     * @return pixel map
     */
    public static PixelMap getPixelMapFromPath(String filePath) {

        ImageSource.SourceOptions pixelMapOptions = new ImageSource.SourceOptions();
        ImageSource imageSource = ImageSource.create(filePath, null);
        return imageSource.createPixelmap(new ImageSource.DecodingOptions());
    }

    public static PixelMap getPixelMapFromResource(Context context, int id) {
        try {
            ResourceManager manager = context.getResourceManager();
            String path = manager.getMediaPath(id);
            RawFileEntry assetManager = context.getResourceManager().getRawFileEntry(path);
            ImageSource.SourceOptions options = new ImageSource.SourceOptions();
            options.formatHint = "image/png";
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            Resource asset = assetManager.openRawFile();
            ImageSource source = ImageSource.create(asset, options);
            return Optional.ofNullable(source.createPixelmap(decodingOptions)).get();
        } catch (NotExistException | WrongTypeException | IOException e) {
            e.printStackTrace();
        }
        return getPixelMapFromResource(context, ResourceTable.Media_no_resource);
    }

    /**
     * save the pixel map to the local path
     *
     * @param pixelMap    pixel map
     * @param imagePath path to save the image
     */
    public static void saveImageToPath(PixelMap pixelMap, String imagePath) {
        //TODO: save image to path
    }

    /**
     * crop image and save as thumbnail
     *
     * @param pixelMap original pixel map
     * @param x1     x1
     * @param y1     y1
     * @param x2     x2
     * @param y2     y2
     * @return local file path
     */
    public static String saveImageCropToPath(PixelMap pixelMap, int x1, int y1, int x2, int y2,
                                             String directoryPath, String imageName) {
        PixelMap croppedImage = cropImage(pixelMap, x1, y1, x2, y2);
        //TODO: save cropped image to path
        return "";
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
