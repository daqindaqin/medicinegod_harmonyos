package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.RgLgScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;

import com.sun.mail.util.MailSSLSocketFactory;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;
import ohos.agp.window.service.WindowManager;


import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.daqin.medicinegod.slice.MainAbilitySlice.insertPerson;
import static ohos.agp.components.InputAttribute.*;


public class RgLgAbilitySlice extends AbilitySlice {
    private static final String CONNECT_HOST = "你的地址+端口";//如：172.0.0.1:3306
    private static final String CONNECT_DB = "库名";//数据库库名，如：datebase
    //配置，需将数据库字段都更为utf-8才能正确插入数据，否则抛异常
    private static final String CONNECT_CONFIG = "characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
    private static final String CONNECT_DB_USERNAME = "root";//用户名
    private static final String CONNECT_DB_USERPWD = "123456";//密码
    private static final String FromMail = "你的邮箱";
    private static final String FromSecret = "邮箱授权码";

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
    private static int mailCode = 0 ;


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
    TextField[] tf_r = new TextField[]{};
    static String l_lname, l_lpwd;


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

        tf_r = new TextField[]{tf_r_userlname, tf_r_userpwd, tf_r_usermail, tf_r_userphone, tf_l_userlname, tf_l_userpwd};
        for (TextField textField : tf_r) {
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
        Random random = new Random();
        t_r_mail_sendCode.setClickedListener(component -> {
            t_r_mail_sendCode.setText("请稍后...");
            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(68,94,238)));
            t_r_mail_sendCode.setClickable(false);
            //生成4位随机数
            mailCode = random.nextInt(9999 - 1000 + 1) + 1000;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Properties props = new Properties();
                        props.setProperty("mail.smtp.auth", "true");
                        props.setProperty("mail.transport.protocol", "smtp");
                        props.put("mail.smtp.ssl.enable", "true");
                        props.put("mail.host", "smtp.163.com");
                        Session session = Session.getInstance(props, new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(FromMail,FromSecret);
                            }
                        });
                        MimeMessage msg = new MimeMessage(session);
                        msg.setFrom(new InternetAddress(FromMail));
                        msg.setSubject("【药神】绑定邮箱验证码");
                        msg.setSentDate(new Date());
                        System.out.println("mailcode"+mailCode);
                        msg.setText("您的验证码是["+mailCode+"]\n您正在进行邮箱绑定的操作，如非您的操作，请忽略此邮件。\n官方人员不会向您索要任何信息，请勿上当！");
                        Transport transport=session.getTransport();
                        transport.connect();
                        transport.sendMessage(msg, new Address[]{new InternetAddress(tf_r_usermail.getText().trim())});
                        transport.close();

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            int countdown = 60;
                            @Override
                            public void run() {
                                //跳转主线程异步方法来更新UI
                                getMainTaskDispatcher().asyncDispatch(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (countdown<1){
                                            t_r_mail_sendCode.setText("获取验证码");
                                            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(68,94,238)));
                                            t_r_mail_sendCode.setClickable(true);
                                            timer.cancel();
                                        }else {
                                            countdown--;
                                            t_r_mail_sendCode.setText("请等待" + countdown + "秒");
                                            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(100,100,100)));
                                        }
                                    }
                                });
                            }
                        }, 0, 1000);
                    } catch (MessagingException mex) {
                        System.out.println("send failed, exception: " + mex);
                        //跳转主线程异步方法来更新UI
                        getMainTaskDispatcher().asyncDispatch(new Runnable() {
                            @Override
                            public void run() {
                                t_r_mail_sendCode.setClickable(true);
                                t_r_mail_sendCode.setText("发送失败");
                                t_r_mail_sendCode.setTextColor(new Color(Color.rgb(255,0,0)));
                            }
                        });




                    }

                }
            }).start();


        });
    }

    private void startRegister() throws Exception {
        Matcher matcher;
        boolean isRight = true;
        Pattern[] patterns = new Pattern[]{userlname, userpwd, usermail, userphone};
        TextField[] textFields = new TextField[]{tf_r_userlname, tf_r_userpwd, tf_r_usermail, tf_r_userphone};
        Text[] texts = new Text[]{t_r_userlname_status, t_r_userpwd_status, t_r_usermail_status, t_r_userphone_status};
        String[] strings = new String[]{
                "用户名应由6-12个字母、数字或其组合而成\n正确示例:abc123 (√)\n错误示例:abc/*+ (×)",
                "密码应由6-16个字母、数字 . @ _ 或其组合而成\n正确示例:abc123.@_ (√)\n错误示例:abc/*+ (×)",
                "您填写的邮箱格式不正确\n正确示例:23333333@qq.com (√)\n错误示例:abc/*@1*.com (×)",
                "您填写的手机号不正确\n正确示例:12345678901 (11位)\n错误示例:23333 (×)"};
        for (int i = 0; i < patterns.length; i++) {
            matcher = patterns[i].matcher(textFields[i].getText().trim());
            if (!matcher.matches()) {
                isRight = false;
                texts[i].setVisibility(Component.VISIBLE);
                texts[i].setText(strings[i]);
                textFields[i].setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
                v_r_scrollView.fluentScrollTo(0, textFields[i].getTop());
            } else {
                texts[i].setVisibility(Component.HIDE);
                textFields[i].setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
            }
        }
        if (Integer.parseInt(tf_r_usermailCode.getText().trim())!=mailCode || tf_r_usermailCode.getText().length()==0){
            isRight = false;
            t_r_usermailCode_status.setText("验证码不正确，请检查！");
            t_r_usermailCode_status.setVisibility(Component.VISIBLE);
            tf_r_usermailCode.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
        }else if(Integer.parseInt(tf_r_usermailCode.getText().trim())==mailCode ){
            tf_r_usermailCode.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
            t_r_usermailCode_status.setVisibility(Component.HIDE);
        }

        if (isRight) {
            //运行注册
            System.out.println("可以注册");
            String lname = tf_r_userpwd.getText().trim(),mail=tf_r_usermail.getText().trim(),phone=tf_r_userphone.getText().trim();
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
                    + "'" + lname + "',"
                    + "'" + sname + "',"
                    + "'" + secret + "',"
                    + "'" + Arrays.toString(util.pixelMap2byte(t_r_head.getPixelMap())) + "',"
                    + "'" + "{\"count\":\"0\",\"friendlist\":{}}',"
                    + "'" + phone + "',"
                    + "'" + mail + "',"
                    + "'" + util.getDateFromString(rgtime) + "',"
                    + "'" + "0',"
                    + "'" + "{\"count\":\"0\",\"medicinelist\":{}}',"
                    + "'0',"
                    + "'0'"
                    + ");";
            final String SQL_db = "create table " + lname + " (KEYID text not null, NAME text not null, IMAGE longblob not null, DESCRIPTION longtext not null, OUTDATE text not null, OTC text not null, BARCODE text not null, YU text not null, ELABEL text not null, LOVE int not null ,MUSE text not null, COMPANY text not null)ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            ;
            final String SQL_isHasLname = "SELECT LNAME FROM USERINFO WHERE LNAME = '" + lname + "'";
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
                        resultSet_isHasLname.close();
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
                        insertPerson("P-" + lname,
                                lname,
                                sname,
                                secret,
                                util.pixelMap2byte(t_r_head.getPixelMap()),
                                "{\"count\":\"0\",\"friendlist\":{}}",
                                phone,
                                mail,
                                String.valueOf(util.getDateFromString(rgtime)),
                                "0",
                                "{\"count\":\"0\",\"medicinelist\":{}}",
                                "0",
                                "0");
                        terminate();
                    }
                }
            }).start();


        }


    }

    private void startLogin() {

    }


}
