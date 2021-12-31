package com.daqin.medicinegod.utils;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;

import java.util.Set;

class PreferenceUtils {

    private static String PREFERENCE_FILE_NAME = "prefrence_file";
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
     * @return 获取int的默认值为：-1
     */
    public static int getInt(Context context, String key) {
        initPreference(context);
        return preferences.getInt(key, -1);
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
