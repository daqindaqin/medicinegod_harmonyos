package com.daqin.medicinegod.data;


import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import ohos.aafwk.ability.AbilitySlice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;


public class WebDataAbility extends AbilitySlice {
    /*private static final String CONNECT_HOST = "你的地址+端口";//如：172.0.0.1:3306
    private static final String CONNECT_DB = "库名";//数据库库名，如：datebase
    //配置，需将数据库字段都更为utf-8才能正确插入数据，否则抛异常
    private static final String CONNECT_CONFIG = "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
    private static final String CONNECT_DB_USERNAME = "root";//用户名
    private static final String CONNECT_DB_USERPWD = "123456";//密码
    private static final String FromMail = "你的邮箱";
    private static final String FromSecret = "邮箱授权码";*/

    private static final String CONNECT_HOST = "139.224.48.87:3306";
    private static final String CONNECT_DB = "mg";
    private static final String CONNECT_CONFIG = "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
    private static final String CONNECT_DB_USERNAME = "mg";
    private static final String CONNECT_DB_USERPWD = "mg@Qhx010394";

    private static final String CONNECT_DB_ID = "ID";
    private static final String CONNECT_DB_LNAME = "LNAME";
    private static final String CONNECT_DB_SNAME = "SNAME";
    private static final String CONNECT_DB_PWD = "PWD";
    private static final String CONNECT_DB_HEAD = "HEAD";
    private static final String CONNECT_DB_FRIEND = "FRIEND";
    private static final String CONNECT_DB_PHONE = "PHONE";
    private static final String CONNECT_DB_MAIL = "MAIL";
    private static final String CONNECT_DB_RGTIME = "RGTIME";
    private static final String CONNECT_DB_ONLINE = "ONLINE";
    private static final String CONNECT_DB_HAS = "HAS";
    private static final String CONNECT_DB_VIP = "VIP";
    private static final String CONNECT_DB_VIPYU = "VIPYU";
    static Connection connection = null;

//    static{
//
//    }
    //TODO：修改方法
    public static boolean editLname(String sql) {
        final boolean[] ok = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = "jdbc:mysql://" + CONNECT_HOST + "/" + CONNECT_DB + "?" + CONNECT_CONFIG;
                try {
                    //1、加载驱动
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    //3.连接成功，返回数据库对象
                    connection = DriverManager.getConnection(url, CONNECT_DB_USERNAME, CONNECT_DB_USERPWD);
                    //4.执行SQL的对象
                    Statement statement = connection.createStatement();
                    //5.执行SQL的对象去执行SQL,可能存在结果，查看返回结果
                    boolean resultSet_isHasLname = statement.execute(sql);//返回的结果集,结果集中封装了我们全部的查询出来的结果
                    if (!resultSet_isHasLname) {
                        ok[0] = true;
                    }
                    //6.释放连接
                    //resultSet_getid.close();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return ok[0];
    }

    public static Map<String, Object> getData(String sql,String lname) {
        Map<String, Object> map = new HashMap<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = "jdbc:mysql://" + CONNECT_HOST + "/" + CONNECT_DB + "?" + CONNECT_CONFIG;
                try {
                    //1、加载驱动
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    //3.连接成功，返回数据库对象
                    Connection connection = DriverManager.getConnection(url, CONNECT_DB_USERNAME, CONNECT_DB_USERPWD);
                    //4.执行SQL的对象
                    Statement statement = connection.createStatement();
                    //5.执行SQL的对象去执行SQL,可能存在结果，查看返回结果
                    ResultSet resultSet_isHas = statement.executeQuery(sql);//返回的结果集,结果集中封装了我们全部的查询出来的结果
                    while (resultSet_isHas.next()) {
                        String s_lname = resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_LNAME));
                        if (s_lname.equals(lname)){
                            map.put(CONNECT_DB_ID, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_ID)));
                            map.put(CONNECT_DB_LNAME, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_LNAME)));
                            map.put(CONNECT_DB_SNAME, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_SNAME)));
                            map.put(CONNECT_DB_PWD, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_PWD)));
                            map.put(CONNECT_DB_HEAD, resultSet_isHas.getBlob(resultSet_isHas.findColumn(CONNECT_DB_HEAD)));
                            map.put(CONNECT_DB_FRIEND, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_FRIEND)));
                            map.put(CONNECT_DB_PHONE, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_PHONE)));
                            map.put(CONNECT_DB_MAIL, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_MAIL)));
                            map.put(CONNECT_DB_RGTIME, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_RGTIME)));
                            map.put(CONNECT_DB_ONLINE, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_ONLINE)));
                            map.put(CONNECT_DB_HAS, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_HAS)));
                            map.put(CONNECT_DB_VIP, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_VIP)));
                            map.put(CONNECT_DB_VIPYU, resultSet_isHas.getString(resultSet_isHas.findColumn(CONNECT_DB_VIPYU)));
                            break;
                        }

                    }
                    resultSet_isHas.close();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return map;
    }
}
