package com.daqin.medicinegod.utils;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.slice.MainAbilitySlice;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Image;
import ohos.agp.render.*;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Base64;
import java.util.Set;


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
            return preferences.getString(key, null);
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

    public static int getWindowHeightPx(Context context) {
        return context.getResourceManager().getDeviceCapability().height * context.getResourceManager().getDeviceCapability().screenDensity / 160;
    }
    public static int getWindowWidthPx(Context context) {
        return context.getResourceManager().getDeviceCapability().width * context.getResourceManager().getDeviceCapability().screenDensity / 160;
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
                // TODO Auto-generated catch block
                e.printStackTrace();
                }
            }
        return encode;
    }



}
