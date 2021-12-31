package com.daqin.medicinegod.utils;

import com.daqin.medicinegod.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Image;
import ohos.agp.render.*;
import ohos.global.resource.NotExistException;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

import java.io.IOException;
import java.io.InputStream;



public class ImageCorner extends AbilitySlice {
    /**
     *
     * @param resourceId 图片资源ID
     * @param componentId 组件ID
     * @param method 1：圆角 2：圆形
     */
    public int HeadView(int resourceId,int componentId,int method) {
        //从资源文件加载PixelMap
        PixelMap originMap = getPixelMapFromResource(resourceId);

        Image imgOrigin = (Image) findComponentById(componentId);
        imgOrigin.setPixelMap(originMap);

        //获取原图片的大小
        assert originMap != null;
        Size originSize = originMap.getImageInfo().size;
        PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
        options.size = new Size(originSize.width, originSize.height);
        options.pixelFormat = PixelFormat.ARGB_8888;
        options.editable = true;
        //创建结果PixelMap
        PixelMap circlePixelMap = PixelMap.create(options);
        Canvas canvas = new Canvas();
        //将结果PixelMap作为画布背景
        Texture texture = new Texture(circlePixelMap);
        canvas.setTexture(texture);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        PixelMapHolder pixelMapHolder = new PixelMapHolder(PixelMap.create(originMap, options));
        PixelMapShader shader = new PixelMapShader(pixelMapHolder, Shader.TileMode.CLAMP_TILEMODE, Shader.TileMode.CLAMP_TILEMODE);
        paint.setShader(shader, Paint.ShaderType.PIXELMAP_SHADER);
        //圆角矩形图
//        RectFloat rect = new RectFloat(50, 50, originSize.width - 20, originSize.height -20);
//        canvas.drawRoundRect(rect, 50,50, paint);
        //圆形图
        canvas.drawCircle(originSize.width * 1.0f / 2, originSize.height * 1.0f / 2, originSize.width * 1.0f / 2, paint);
        Image imgCircle = (Image) findComponentById(componentId);
        imgCircle.setPixelMap(circlePixelMap);
        return 0;


    }
    public PixelMap getPixelMapFromResource(int resourceId) {
        try (InputStream inputStream = getContext().getResourceManager().getResource(resourceId)) {
            // 创建图像数据源ImageSource对象
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/jpg";
            ImageSource imageSource = ImageSource.create(inputStream, srcOpts);
            // 设置图片参数
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            return imageSource.createPixelmap(decodingOptions);
        } catch (IOException | NotExistException ignored) {
        }
        return null;
    }
}
