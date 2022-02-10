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
import ohos.media.image.common.Size;
import ohos.utils.net.Uri;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static PixelMap byte2PixelMap(byte[] bytes){
        ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
        ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();

        srcOpts.formatHint = "image/jpg";
        decodingOptions.rotateDegrees = 0.0f;
        decodingOptions.desiredPixelFormat = PixelFormat.ARGB_8888;

        return ImageSource.create(bytes, srcOpts).createPixelmap(decodingOptions);

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
        sb.append("KEY");
        for (int i = 0; i < 13; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        if (MainAbilitySlice.isPresentkeyId(sb.toString())){
            getRandomKeyId();
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
}
