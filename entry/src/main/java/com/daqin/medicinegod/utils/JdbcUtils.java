package com.daqin.medicinegod.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtils {
	 /*private static final String CONNECT_HOST = "你的地址+端口";//如：172.0.0.1:3306
    private static final String CONNECT_DB = "库名";//数据库库名，如：datebase
    //配置，需将数据库字段都更为utf-8才能正确插入数据，否则抛异常
    private static final String CONNECT_CONFIG = "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
    private static final String CONNECT_DB_USERNAME = "root";//用户名
    private static final String CONNECT_DB_USERPWD = "123456";//密码
    private static final String FromMail = "你的邮箱";
    private static final String FromSecret = "邮箱授权码";*/


    private static final String uRL = "jdbc:mysql://139.224.48.87:3306/mg?characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
    private static final String uNname = "mg";
    private static final String uPwd = "Qhx010394Mg";

    // 静态代码块，类加载的时候只执行一次，以后再也不执行了
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * 获得连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(uRL,uNname,uPwd);
    }

    /*
     * 释放资源 ResultSet Connection Statement 或 PrepareStatement 释放循序 ResultSet ->
     * Statement -> PreparedStatement -> Connection
     */
    public static void closeConnection(Connection c, PreparedStatement pps, ResultSet r) {

        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (r != null) {
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pps != null) {
            try {
                pps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void closeConnection(Connection c, PreparedStatement pps) {

        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        if (pps != null) {
            try {
                pps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void closeConnection(Connection c, ResultSet r) {

        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (r != null) {
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public static void closeConnection(PreparedStatement pps, ResultSet r) {


        if (r != null) {
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pps != null) {
            try {
                pps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void closeConnection(Connection c) {

        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    public static void closeConnection(PreparedStatement pps) {


        if (pps != null) {
            try {
                pps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void closeConnection(ResultSet r) {


        if (r != null) {
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
}
