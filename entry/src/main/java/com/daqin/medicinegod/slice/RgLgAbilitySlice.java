package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.RgLgScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.window.service.WindowManager;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ohos.agp.components.InputAttribute.*;


public class RgLgAbilitySlice extends AbilitySlice {
    private static final String CONNECT_HOST = "你的地址+端口";//如：172.0.0.1:3306
    private static final String CONNECT_DB = "库名";//数据库库名，如：datebase
    //配置，需将数据库字段都更为utf-8才能正确插入数据，否则抛异常
    private static final String CONNECT_CONFIG = "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
    private static final String CONNECT_DB_USERNAME = "root";//用户名
    private static final String CONNECT_DB_USERPWD = "123456";//密码

    private static final String BASE_URI = "dataability:///com.daqin.medicinegod.data.PersonDataAbility";
    private static final String DATA_PATH = "/person";
    private static DataAbilityHelper databaseHelper;
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


    private static final Pattern userlname = Pattern.compile("^[A-Za-z0-9]{6,12}+$");
    private static final Pattern userpwd = Pattern.compile("^[A-Za-z0-9._@]{6,16}+$");
    private static final Pattern usermail = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    private static final Pattern userphone = Pattern.compile("^[0-9]{11}+$");

    private static String[] columns = new String[]{};


    private List<Component> mPageViewList = new ArrayList<>();
    PageSlider mPageSlider;

    ScrollView v_r_scrollView;
    TextField tf_l_userlname;
    Text t_l_userlname_status;
    TextField tf_l_userpwd;
    Text t_l_userpwd_status;
    Text t_l_ok;
    Text t_l_toRegister;
    TextField tf_r_userlname;
    Text t_r_userlname_status;
    TextField tf_r_userpwd;
    Text t_r_userpwd_status;
    TextField tf_r_usermail;
    Text t_r_usermail_status;
    TextField tf_r_usermailCode;
    Text t_r_usermailCode_status;
    TextField tf_r_userphone;
    Text t_r_userphone_status;
    Text t_r_mail_sendCode;
    Text t_r_ok;
    Text t_r_toLogin;
    Text t_back;
    Image t_r_showhidepwd;
    Image t_r_head;
    TextField[] tf_ = new TextField[]{};


    boolean showhidepwd = false;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_rglg);
        this.getWindow().setInputPanelDisplayType(WindowManager.LayoutConfig.INPUT_ADJUST_PAN);

        columns = new String[]{
                CONNECT_DB_LNAME,
                CONNECT_DB_SNAME,
                CONNECT_DB_PWD,
                CONNECT_DB_HEAD,
                CONNECT_DB_FRIEND,
                CONNECT_DB_PHONE,
                CONNECT_DB_MAIL,
                CONNECT_DB_RGTIME,
                CONNECT_DB_ONLINE,
                CONNECT_DB_HAS,
                CONNECT_DB_VIP,
                CONNECT_DB_VIPYU
        };
        databaseHelper = DataAbilityHelper.creator(this);
        mPageSlider = (PageSlider) findComponentById(ResourceTable.Id_pager_slider);
        mPageViewList.clear();
        mPageViewList.add(LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_ability_main_register, null, false));
        mPageViewList.add(LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_ability_main_login, null, false));
        RgLgScreenSlidePagerProvider adapter = new RgLgScreenSlidePagerProvider(mPageViewList);
        mPageSlider.setProvider(adapter);
        mPageSlider.setCurrentPage(0);
        initView();
        initClickListener();
    }


    private void initView() {
        tf_r_userlname = (TextField) findComponentById(ResourceTable.Id_rg_userlname);
        t_r_userlname_status = (Text) findComponentById(ResourceTable.Id_rg_userlname_status);
        tf_r_userpwd = (TextField) findComponentById(ResourceTable.Id_rg_userpwd);
        t_r_userpwd_status = (Text) findComponentById(ResourceTable.Id_rg_userpwd_status);
        tf_r_usermail = (TextField) findComponentById(ResourceTable.Id_rg_usermail);
        t_r_usermail_status = (Text) findComponentById(ResourceTable.Id_rg_usermail_status);
        tf_r_usermailCode = (TextField) findComponentById(ResourceTable.Id_rg_usermail_code);
        t_r_usermailCode_status = (Text) findComponentById(ResourceTable.Id_rg_usermailcode_status);
        tf_r_userphone = (TextField) findComponentById(ResourceTable.Id_rg_userphone);
        t_r_userphone_status = (Text) findComponentById(ResourceTable.Id_rg_userphone_status);
        t_r_mail_sendCode = (Text) findComponentById(ResourceTable.Id_rg_usermail_sendcode);
        t_r_head = (Image) findComponentById(ResourceTable.Id_rg_head);
        t_r_ok = (Text) findComponentById(ResourceTable.Id_rg_ok);
        t_r_toLogin = (Text) findComponentById(ResourceTable.Id_rg_goto);
        tf_l_userlname = (TextField) findComponentById(ResourceTable.Id_lg_userlname);
        v_r_scrollView = (ScrollView) findComponentById(ResourceTable.Id_rg_srcview);
        t_r_showhidepwd = (Image) findComponentById(ResourceTable.Id_rg_showhidepwd);
        t_l_userlname_status = (Text) findComponentById(ResourceTable.Id_lg_userlname_status);
        tf_l_userpwd = (TextField) findComponentById(ResourceTable.Id_lg_userpwd);
        t_l_userpwd_status = (Text) findComponentById(ResourceTable.Id_lg_userpwd_status);
        t_l_toRegister = (Text) findComponentById(ResourceTable.Id_lg_goto);
        t_l_ok = (Text) findComponentById(ResourceTable.Id_lg_ok);
        t_back = (Text) findComponentById(ResourceTable.Id_rg_back);

        tf_ = new TextField[]{tf_r_userlname, tf_r_userpwd, tf_r_usermail, tf_r_userphone, tf_l_userlname, tf_l_userpwd};
        for (TextField textField : tf_) {
            textField.setFocusChangedListener(new Component.FocusChangedListener() {
                @Override
                public void onFocusChange(Component component, boolean b) {
                    if (b) {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_foucs));
                        v_r_scrollView.fluentScrollTo(0, textField.getTop() - 150);
                    } else {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
                    }
                }
            });
        }


    }

    private void initClickListener() {
        t_l_toRegister.setClickedListener(component -> mPageSlider.setCurrentPage(0));
        t_r_toLogin.setClickedListener(component -> mPageSlider.setCurrentPage(1));
        t_back.setClickedListener(component -> terminate());
        t_r_ok.setClickedListener(component -> {

            try {
                startRegister();
            } catch (Exception e) {
                System.out.println("错误");
                e.printStackTrace();
            }

        });
        t_l_ok.setClickedListener(component -> startLogin());
        t_r_showhidepwd.setClickedListener(component -> {
            if (showhidepwd) {
                tf_r_userpwd.setTextInputType(PATTERN_PASSWORD);
                t_r_showhidepwd.setPixelMap(ResourceTable.Media_rg_hidepwd);
                showhidepwd = false;
            } else {
                tf_r_userpwd.setTextInputType(PATTERN_TEXT_VISIBLE_PASSWORD_TYPE);
                t_r_showhidepwd.setPixelMap(ResourceTable.Media_rg_showpwd);
                showhidepwd = true;
            }
        });
    }

    private void startRegister() throws Exception {
        Matcher matcher;
        boolean isRight = true;
//        Pattern[] patterns = new Pattern[]{userlname, userpwd, usermail, userphone};
//        TextField[] textFields = new TextField[]{tf_r_userlname, tf_r_userpwd, tf_r_usermail, tf_r_userphone};
//        Text[] texts = new Text[]{t_r_userlname_status, t_r_userpwd_status, t_r_usermail_status, t_r_userphone_status};
//        String[] strings = new String[]{
//                "用户名应由6-12个字母、数字或其组合而成\n正确示例:abc123 (√)\n错误示例:abc/*+ (×)",
//                "密码应由6-16个字母、数字 . @ _ 或其组合而成\n正确示例:abc123.@_ (√)\n错误示例:abc/*+ (×)",
//                "您填写的邮箱格式不正确\n正确示例:23333333@qq.com (√)\n错误示例:abc/*@1*.com (×)",
//                "您填写的手机号不正确\n正确示例:12345678901 (11位)\n错误示例:23333 (×)"};
//        for (int i = 0; i < patterns.length; i++) {
//            matcher = patterns[i].matcher(textFields[i].getText().trim());
//            if (!matcher.matches()) {
//                isRight = false;
//                texts[i].setVisibility(Component.VISIBLE);
//                texts[i].setText(strings[i]);
//                textFields[i].setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
//                v_r_scrollView.fluentScrollTo(0, textFields[i].getTop());
//            } else {
//                texts[i].setVisibility(Component.HIDE);
//                textFields[i].setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
//            }
//        }

        if (isRight) {
            //运行注册
            System.out.println("可以注册");
            //要连接的数据库url,注意：此处连接的应该是服务器上的MySQl的地址
            Calendar cl = new GregorianCalendar();
            String sname = "用户" + util.getRandomSNAME();
            String secret = util.getSerect(tf_r_userpwd.getText().trim());
            String rgtime = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + (cl.get(Calendar.DAY_OF_MONTH));
            final String url = "jdbc:mysql://" + CONNECT_HOST + "/" + CONNECT_DB + "?" + CONNECT_CONFIG;
            final String SQL_user = "INSERT INTO USERINFO ("
                    + CONNECT_DB_LNAME + ","
                    + CONNECT_DB_SNAME + ","
                    + CONNECT_DB_PWD + ","
                    + CONNECT_DB_HEAD + ","
                    + CONNECT_DB_FRIEND + ","
                    + CONNECT_DB_PHONE + ","
                    + CONNECT_DB_MAIL + ","
                    + CONNECT_DB_RGTIME + ","
                    + CONNECT_DB_ONLINE + ","
                    + CONNECT_DB_HAS + ","
                    + CONNECT_DB_VIP + ","
                    + CONNECT_DB_VIPYU
                    + ") VALUES ("
                    + "'" + tf_r_userlname.getText().trim() + "',"
                    + "'" + sname + "',"
                    + "'" + secret + "',"
                    + "'" + Arrays.toString(util.pixelMap2byte(t_r_head.getPixelMap())) + "',"
                    + "'" + "{\"count\":\"0\",\"friendlist\":{}}',"
                    + "'" + tf_r_userphone.getText().trim() + "',"
                    + "'" + tf_r_usermail.getText().trim() + "',"
                    + "'" + util.getDateFromString(rgtime) + "',"
                    + "'" + "0',"
                    + "'" + "{\"count\":\"0\",\"medicinelist\":{}}',"
                    + "'0',"
                    + "'0'"
                    + ");";
            final String SQL_db = "create table " + tf_r_userlname.getText().trim() + " (KEYID text not null, NAME text not null, IMAGE longblob not null, DESCRIPTION longtext not null, OUTDATE text not null, OTC text not null, BARCODE text not null, YU text not null, ELABEL text not null, LOVE int not null ,MUSE text not null, COMPANY text not null)ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            ;
            final String SQL_isHasLname = "SELECT LNAME FROM USERINFO WHERE LNAME = '" + tf_r_userlname.getText().trim() + "'";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean successname = true, success = true;
                    try {
                        //1、加载驱动
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        //3.连接成功，返回数据库对象
                        Connection connection = DriverManager.getConnection(url, CONNECT_DB_USERNAME, CONNECT_DB_USERPWD);
                        //4.执行SQL的对象
                        Statement statement = connection.createStatement();
                        //5.执行SQL的对象去执行SQL,可能存在结果，查看返回结果
                        java.sql.ResultSet resultSet_isHasLname = statement.executeQuery(SQL_isHasLname);//返回的结果集,结果集中封装了我们全部的查询出来的结果
                        if (resultSet_isHasLname.next()) {
                            successname = false;
                            success = false;
                        } else if (successname) {
                            //插入用户信息和药名表
                            boolean resultSet_user = statement.execute(SQL_user);//返回的结果集,结果集中封装了我们全部的查询出来的结果
                            boolean resultSet_db = statement.execute(SQL_db);
                            System.out.println("输出了" + !resultSet_user + !resultSet_db);
                            //取ID
//                            java.sql.ResultSet resultSet_getid = statement.executeQuery("SELECT ID,LNAME FROM USERINFO WHERE LNAME = '"+tf_r_userlname.getText().trim()+"'");
//                            while (resultSet_getid.next()){
//                                id = resultSet_getid.getString(CONNECT_DB_ID);
//                                System.out.println("id"+resultSet_getid.getString(CONNECT_DB_ID));
//                                System.out.println("name"+resultSet_getid.getString(CONNECT_DB_LNAME));
//                            }

                        }
                        //6.释放连接
                        //resultSet_getid.close();
                        statement.close();
                        connection.close();
                    } catch (Exception e) {
                        success = false;
                        e.printStackTrace();
                    }
                    if (!successname) {
                        new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .isDestroyOnDismiss(true)
                                .asConfirm("注册失败", "用户名已存在",
                                        " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                .show(); // 最后一个参数绑定已有布局
                        System.out.println("发生错误");
                    } else if (!success) {
                        new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .isDestroyOnDismiss(true)
                                .asConfirm("注册失败", "注册失败，可能由于网络原因或服务器不稳，\n" +
                                                "请稍后重试或联系管理员处理。",
                                        " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                .show(); // 最后一个参数绑定已有布局
                        System.out.println("发生错误");
                    } else if (success) {
                        System.out.println("运行到这");
                        insert("P-" + tf_r_userlname.getText().trim(),
                                tf_r_userlname.getText().trim(),
                                sname,
                                secret,
                                util.pixelMap2byte(t_r_head.getPixelMap()),
                                "{\"count\":\"0\",\"friendlist\":{}}",
                                tf_r_userphone.getText().trim(),
                                tf_r_usermail.getText().trim(),
                                String.valueOf(util.getDateFromString(rgtime)),
                                "0",
                                "{\"count\":\"0\",\"medicinelist\":{}}",
                                "0",
                                "0");
                        Thread.interrupted();
                        query();
                    }
                }
            }).start();


        }


    }

    private void startLogin() {

    }


    public void insert(String id, String lname, String sname,
                       String pwd, byte[] head, String friend,
                       String phone, String mail, String rgtime,
                       String online, String has, String vip, String vipyu) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(CONNECT_DB_ID, id);
        valuesBucket.putString(CONNECT_DB_LNAME, lname);
        valuesBucket.putByteArray(CONNECT_DB_HEAD, head);
        valuesBucket.putString(CONNECT_DB_SNAME, sname);
        valuesBucket.putString(CONNECT_DB_PWD, pwd);
        valuesBucket.putString(CONNECT_DB_FRIEND, friend);
        valuesBucket.putString(CONNECT_DB_PHONE, phone);
        valuesBucket.putString(CONNECT_DB_MAIL, mail);
        valuesBucket.putString(CONNECT_DB_RGTIME, rgtime);
        valuesBucket.putString(CONNECT_DB_ONLINE, online);
        valuesBucket.putString(CONNECT_DB_HAS, has);
        valuesBucket.putString(CONNECT_DB_VIP, vip);
        valuesBucket.putString(CONNECT_DB_VIPYU, vipyu);
        try {
            if (databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket) != -1) {
                util.PreferenceUtils.putString(getContext(), "rgok", "ok");
                System.out.println("person insert successful");
//                query();
                System.out.println("注册成功");
                terminate();
//                new XPopup.Builder(getContext())
////                        .setPopupCallback(new XPopupListener())
//                        .dismissOnTouchOutside(false)
//                        .dismissOnBackPressed(false)
//                        .isDestroyOnDismiss(true)
//                        .asConfirm("注册成功", "恭喜你",
//                                " ", "现在进入", new OnConfirmListener() {
//                                    @Override
//                                    public void onConfirm() {
//
//                                        terminate();
//                                    }
//                                }, new OnCancelListener() {
//                                    @Override
//                                    public void onCancel() {
//                                        terminate();
//                                    }
//                                }, false, ResourceTable.Layout_popup_comfirm_without_cancel)
//                        .show(); // 最后一个参数绑定已有布局

            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
//            ToastUtil.showToast(this, "注册成功，但登录失败，请重试  ");
            exception.printStackTrace();
            query();
            System.out.println("登录出错");
        }
    }

    public void query() {
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(CONNECT_DB_ID, "P-");
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                System.out.println("query:resultSet is null or no result found");
            } else {
                System.out.println("query:" + resultSet.getRowCount());
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            System.out.println("query: dataRemote exception | illegalStateException");
        }
    }

}
