package com.daqin.medicinegod.utils;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.slice.MainAbilitySlice;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.agp.components.Image;
import ohos.agp.render.*;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Rect;
import ohos.media.image.common.Size;
import ohos.utils.net.Uri;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class util extends AbilitySlice {
    //本地键值对数据库相关
    public static class PreferenceUtils {

        private static String PREFERENCE_FILE_NAME = "mgconfig";
        private static Preferences preferences;
        private static DatabaseHelper databaseHelper;
        private static Preferences.PreferencesObserver mPreferencesObserver;

        private static void initPreference(Context context){
            if(databaseHelper==null){
                databaseHelper = new DatabaseHelper(context);
            }
            if(preferences==null){
                preferences = databaseHelper.getPreferences(PREFERENCE_FILE_NAME);
            }

        }

        //存放、获取时传入的context必须是同一个context,否则存入的数据无法获取
        public static void putString(Context context, String key, String value) {
            initPreference(context);
            preferences.putString(key, value);
            preferences.flush();
        }

        /**
         * @param context 上下文
         * @param key  键
         * @return 获取的String 默认值为:null
         */
        public static String getString(Context context, String key) {
            initPreference(context);
            return preferences.getString(key, "none");
        }


        public static void putInt(Context context, String key, int value) {
            initPreference(context);
            preferences.putInt(key, value);
            preferences.flush();
        }

        /**
         * @param context 上下文
         * @param key 键
         * @return 获取int的默认值为：0
         */
        public static int getInt(Context context, String key) {
            initPreference(context);
            return preferences.getInt(key, 0);
        }


        public static void putLong(Context context, String key, long value) {
            initPreference(context);
            preferences.putLong(key, value);
            preferences.flush();
        }

        /**
         * @param context 上下文
         * @param key  键
         * @return 获取long的默认值为：-1
         */
        public static long getLong(Context context, String key) {
            initPreference(context);
            return preferences.getLong(key, -1L);
        }


        public static void putBoolean(Context context, String key, boolean value) {
            initPreference(context);
            preferences.putBoolean(key, value);
            preferences.flush();
        }

        /**
         * @param context  上下文
         * @param key  键
         * @return 获取boolean的默认值为：false
         */
        public static boolean getBoolean(Context context, String key) {
            initPreference(context);
            return preferences.getBoolean(key, false);
        }


        public static void putFloat(Context context, String key, float value) {
            initPreference(context);
            preferences.putFloat(key, value);
            preferences.flush();
        }

        /**
         * @param context 上下文
         * @param key   键
         * @return 获取float的默认值为：0.0
         */
        public static float getFloat(Context context, String key) {
            initPreference(context);
            return preferences.getFloat(key, 0.0F);
        }


        public static void putStringSet(Context context, String key, Set<String> set) {
            initPreference(context);
            preferences.putStringSet(key, set);
            preferences.flush();
        }

        /**
         * @param context  上下文
         * @param key 键
         * @return 获取set集合的默认值为：null
         */
        public static Set<String> getStringSet(Context context, String key) {
            initPreference(context);
            return preferences.getStringSet(key, null);
        }


        public static boolean deletePreferences(Context context) {
            initPreference(context);
            boolean isDelete= databaseHelper.deletePreferences(PREFERENCE_FILE_NAME);
            return isDelete;
        }


        public static void registerObserver(Context context, Preferences.PreferencesObserver preferencesObserver){
            initPreference(context);
            mPreferencesObserver=preferencesObserver;
            preferences.registerObserver(mPreferencesObserver);
        }

        public static void unregisterObserver(){
            if(mPreferencesObserver!=null){
                // 向preferences实例注销观察者
                preferences.unregisterObserver(mPreferencesObserver);
            }
        }

    }
    /**
     *
     * @Author: kiki
     * @Date: 2018/12/26
     */
    public static String bytes2HexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        String sTmp;

        for (byte b : bArr) {
            sTmp = Integer.toHexString(0xFF & b);
            if (sTmp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hex2ByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = (byte) Integer.parseInt(inHex.substring(i, i + 2), 16);
            j++;
        }
        return result;
    }



    public static int getWindowHeightPx(Context context) {
        return context.getResourceManager().getDeviceCapability().height * context.getResourceManager().getDeviceCapability().screenDensity / 160;
    }
    public static int getWindowWidthPx(Context context) {
        return context.getResourceManager().getDeviceCapability().width * context.getResourceManager().getDeviceCapability().screenDensity / 160;
    }

    public static long getDateFromString(String Value){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            //将日期转成Date对象作比较
            Date fomatDate = dateFormat.parse(Value);
            time =  fomatDate.getTime();

            System.out.println("输出了"+time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    public static String getStringFromDate(long Value){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Value);
        return dateFormat.format(date);
    }


    public static int isTimeOut(String timeA,String timeB){
        /**
         * @param timeA 第一个时间
         * @param timeB 第二个时间
         * @param return 0为临期；-1过期；1未过期;
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int res = 0;
        try {
            //将日期转成Date对象作比较
            Date fomatDate1 = dateFormat.parse(timeA);
            Date fomatDate2 = dateFormat.parse(timeB);

            Long time1 =  fomatDate1.getTime();
            Long time2 =  fomatDate2.getTime();

            int day = (int) ((time1 - time2) / (24*3600*1000));

            if (day <= 0){
                res = -1;
            }else if(day <= 60){
                res = 0;
            }else {
                res = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int[] getRemainTime(String timeA,String timeB){
        /**
         * @param timeA 第一个时间
         * @param timeB 第二个时间
         * @param return 返回时间数组
         */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int[] outdate = new int[]{0,0,0,0,0,0};
        //0年 1月 2天 3时 4分 5秒
        try {
            //将日期转成Date对象作比较
            Date fomatDate1 = dateFormat.parse(timeA);
            Date fomatDate2 = dateFormat.parse(timeB);

            Long time1 =  fomatDate1.getTime();
            Long time2 =  fomatDate2.getTime();
            int second = (int) ((time1 - time2) / 1000);
            //如有更好的方案则优化此处
            if (second > 0) {
                outdate[5] = second;
                if (outdate[5] >= 60) {
                    outdate[4] = outdate[5] / 60;
                    outdate[5] = outdate[5] % 60;
                    if (outdate[4] >= 60) {
                        outdate[3] = outdate[4] / 60;
                        outdate[4] = outdate[4] % 60;
                        if (outdate[3] > 24) {
                            outdate[2] = outdate[3] / 24;
                            outdate[3] = outdate[3] % 24;
                            if(outdate[2] > 30){
                                outdate[1] = outdate[2] / 30;
                                outdate[2] = outdate[2] % 30;
                                if(outdate[1] > 12){
                                    outdate[0] = outdate[1] / 12;
                                    outdate[1] = outdate[1] % 12;
                                }
                            }
                        }
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outdate;
    }


    public static String getImageBase64(String imgPath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        // String imgFile = imgPath;// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        String encode = null; // 返回Base64编码过的字节数组字符串
        // 对字节数组Base64编码
        try {
            // 读取图片字节数组
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            encode = Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                }
            }
        return encode;
    }



    public static PixelMap byte2PixelMap(byte[] bytes){
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();

        srcOpts.formatHint = "image/jpeg";
        decodingOptions.rotateDegrees = 0.0f;
        decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;

        return ImageSource.create(bytes, srcOpts).createPixelmap(decodingOptions);


//
//        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
//        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
//
//        srcOpts.formatHint = "image/png";
//        decodingOptions.rotateDegrees = 0.0f;
//        decodingOptions.desiredPixelFormat = PixelFormat.UNKNOWN;

//        return ImageSource.create(bytes, srcOpts).createPixelmap(decodingOptions);

    }
    //TODO：70%的缩略图分别存
    public static PixelMap getThumPixelMap(PixelMap orgPixelMap,int bgScale){
        PixelMap result;
        PixelMap.InitializationOptions initializationOptions = new PixelMap.InitializationOptions();
        new Size(orgPixelMap.getImageInfo().size.width / bgScale,
                orgPixelMap.getImageInfo().size.height / bgScale);
        initializationOptions.pixelFormat = PixelFormat.ARGB_8888;
        result =
                PixelMap.create(
                        orgPixelMap,
                        new Rect(0,0,
                                orgPixelMap.getImageInfo().size.width,
                                orgPixelMap.getImageInfo().size.height),
                        initializationOptions);
        return result;
    }


    public static byte[] pixelMap2byte(PixelMap pixelMap){
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        imagePacker.initializePacking(byteArrayOutputStream, packingOptions);
        imagePacker.addImage(pixelMap);
        imagePacker.finalizePacking();
        return byteArrayOutputStream.toByteArray();
    }

    public static String pixelMap2Base64(PixelMap pixelMap){
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        imagePacker.initializePacking(byteArrayOutputStream, packingOptions);
        imagePacker.addImage(pixelMap);
        imagePacker.finalizePacking();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getRandomKeyId() {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("M-");
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        if (MainAbilitySlice.isPresentkeyId(sb.toString())){
            getRandomKeyId();
        }
        return sb.toString();
    }
    public static String getRandomSNAME() {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public static byte[] readInputStream(InputStream inputStream) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int length = -1;

        try {
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = baos.toByteArray();

        try {
            inputStream.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
    public static String getSerect(String lname,String sourceStr) throws Exception {
        sourceStr = "daqin" + sourceStr + "qin.@" + lname;
        sourceStr = getSHA256(sourceStr, "daqinMG.@");
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte value : b) {
                i = value;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        result = result.substring(24, 32)+result.substring(8, 16);
        return result.toUpperCase();
    }

    public static String getMD5(String sourceStr,int digit) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("MD5(" + sourceStr + ",32) = " + result);
            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        if (digit == 16){
            return result.substring(8, 24);
        }else {
            return result;
        }

    }
    public static String getSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }

        return sb.toString().toUpperCase();
    }
}
