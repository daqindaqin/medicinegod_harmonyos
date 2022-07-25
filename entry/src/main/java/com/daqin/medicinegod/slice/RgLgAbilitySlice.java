package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.RgLgScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.JdbcUtils;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;

import com.lxj.xpopup.util.ToastUtil;
import com.sun.mail.imap.protocol.ID;
import com.sun.mail.util.MailSSLSocketFactory;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;
import ohos.agp.window.service.WindowManager;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;


import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ohos.agp.components.InputAttribute.*;


public class RgLgAbilitySlice extends AbilitySlice {

//    private static final String FromMail = "你的邮箱";
//    private static final String FromSecret = "邮箱授权码";

    private static final String FromMail = "wfgmqhx@163.com";
    private static final String FromSecret = "PTYVSZLVHAUJDPYG";

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
    private static int mailCode = 0;


    private List<Component> mPageViewList = new ArrayList<>();
    PageSlider mPageSlider;


    int head_status = 0;//0注册 1登录
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
    Image t_l_head;
    TextField[] tf_r = new TextField[]{};


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
        t_r_head.setCornerRadius(150);
        t_r_ok = (Text) findComponentById(ResourceTable.Id_rg_ok);
        t_r_toLogin = (Text) findComponentById(ResourceTable.Id_rg_goto);
        t_l_head = (Image) findComponentById(ResourceTable.Id_lg_head);
        t_l_head.setCornerRadius(150);
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
                        if (textField == tf_l_userlname) {
                            if (tf_l_userlname.getText().length() != 0) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Connection connection = null;
                                        PreparedStatement pps = null;
                                        java.sql.ResultSet resultSet_head = null;
                                        try {
                                            connection = JdbcUtils.getConnection();
                                            pps = connection.prepareStatement("select * from USERINFO where LNAME = ?");
                                            pps.setString(1, tf_l_userlname.getText().trim());
                                            resultSet_head = pps.executeQuery();
                                            if (resultSet_head.next()) {
                                                t_l_head.setPixelMap(util.byte2PixelMap((byte[]) resultSet_head.getBytes(CONNECT_DB_HEAD)));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            JdbcUtils.closeConnection(connection, pps, resultSet_head);
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                }
            });
        }


    }

    private void openGallery() {
        Intent intent = new Intent();
//        intent.setType("video/*");
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        intent.setAction("android.intent.action.GET_CONTENT");
        intent.setParam("return-data", true);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.addFlags(0x00000001);
        startAbilityForResult(intent, 100);
    }

    @Override
    public void onAbilityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (data != null) {
                    //取得图片路径
                    String imgpath = data.getUriString();
//                    取真实地址
//                    pickiT.getPath(data.getUri());
                    System.out.println("gggggggg" + imgpath);
                    //定义数据能力帮助对象
                    DataAbilityHelper helper = DataAbilityHelper.creator(getContext());

                    //定义组件资源
                    ImageSource imageSource = null;
                    FileInputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(helper.openFile(Uri.parse(imgpath), "r"));
                    } catch (DataAbilityRemoteException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //定义文件
                    FileDescriptor file = null;
                    try {
                        file = helper.openFile(Uri.parse(imgpath), "r");
                    } catch (DataAbilityRemoteException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //创建文件对象
                    imageSource = ImageSource.create(file, null);
                    //创建位图
                    PixelMap pixelMap = imageSource.createPixelmap(null);


                    Intent intent = new Intent();
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")
                            .withBundleName(getBundleName())
                            .withAbilityName("com.daqin.medicinegod.ImageControlAbility")
                            .build();
                    intent.setOperation(operation);
                    ImageSaver.getInstance().setByte(util.pixelMap2byte(pixelMap));
//                    imageSaver.setByte(imgbytes);
                    System.out.println("开始传输");
//                    intent.setParam("startcropimage", imgbytes);
                    startAbilityForResult(intent, 101);

                }
                break;
            case 101:
                if (data != null) {
                    if (data.getStringParam("cropedimage").equals("ok")) {
                        if (head_status == 0) {
                            t_r_head.setPixelMap(util.byte2PixelMap(ImageSaver.getInstance().getByte()));
                        } else if (head_status == 1) {
                            t_l_head.setPixelMap(util.byte2PixelMap(ImageSaver.getInstance().getByte()));
                        }
                    }
                }

                break;
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
        t_l_ok.setClickedListener(component -> {
            try {
                startLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t_r_showhidepwd.setClickedListener(component -> {
            if (showhidepwd) {
                tf_r_userpwd.setTextInputType(PATTERN_PASSWORD);
                t_r_showhidepwd.setPixelMap(ResourceTable.Media_rg_hidepwd);
                showhidepwd = false;
            } else {
                tf_r_userpwd.setTextInputType(PATTERN_TEXT);
                t_r_showhidepwd.setPixelMap(ResourceTable.Media_rg_showpwd);
                showhidepwd = true;
            }
        });
        t_r_head.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                head_status = 0;
                openGallery();
            }
        });
        t_l_head.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                head_status = 1;
                openGallery();
            }
        });
        Random random = new Random();
        t_r_mail_sendCode.setClickedListener(component -> {
            t_r_mail_sendCode.setText("请稍后...");
            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(68, 94, 238)));
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
                                return new PasswordAuthentication(FromMail, FromSecret);
                            }
                        });

                        MimeMessage msg = new MimeMessage(session);
                        msg.setFrom(new InternetAddress(FromMail));
                        msg.setSubject("【药神】绑定邮箱验证码");
                        msg.setSentDate(new Date());
                        System.out.println("mailcode" + mailCode);
                        msg.setText("您的验证码是[" + mailCode + "]\n您正在进行邮箱绑定的操作，如非您的操作，请忽略此邮件。\n官方人员不会向您索要任何信息，请勿上当！");
                        Transport transport = session.getTransport();
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
                                        if (countdown < 1) {
                                            t_r_mail_sendCode.setText("获取验证码");
                                            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(68, 94, 238)));
                                            t_r_mail_sendCode.setClickable(true);
                                            timer.cancel();
                                        } else {
                                            countdown--;
                                            t_r_mail_sendCode.setText("请等待" + countdown + "秒");
                                            t_r_mail_sendCode.setTextColor(new Color(Color.rgb(100, 100, 100)));
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
                                t_r_mail_sendCode.setTextColor(new Color(Color.rgb(255, 0, 0)));
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
        if (Integer.parseInt(tf_r_usermailCode.getText().trim()) != mailCode || tf_r_usermailCode.getText().length() == 0) {
            isRight = false;
            t_r_usermailCode_status.setText("验证码不正确，请检查！");
            t_r_usermailCode_status.setVisibility(Component.VISIBLE);
            tf_r_usermailCode.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
        } else if (Integer.parseInt(tf_r_usermailCode.getText().trim()) == mailCode) {
            tf_r_usermailCode.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
            t_r_usermailCode_status.setVisibility(Component.HIDE);
        }

        if (isRight) {
            //运行注册
            System.out.println("可以注册");
            String lname = tf_r_userlname.getText().trim(), mail = tf_r_usermail.getText().trim(), phone = tf_r_userphone.getText().trim();
            //要连接的数据库url,注意：此处连接的应该是服务器上的MySQl的地址
            Calendar cl = new GregorianCalendar();
            String sname = "用户" + util.getRandomSNAME();
            String secret = util.getSerect(lname, tf_r_userpwd.getText().trim());
            String rgtime = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-" + (cl.get(Calendar.DAY_OF_MONTH));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean successName = true, successLogin = true;
                    Connection conn = null;
                    PreparedStatement pps = null;
                    java.sql.ResultSet resultSet = null;
                    try {
                        conn = JdbcUtils.getConnection();
                        //先找有没有账号
                        pps = conn.prepareStatement("SELECT LNAME FROM USERINFO WHERE LNAME = ?");
                        pps.setString(1, lname);
                        resultSet = pps.executeQuery();
                        if (resultSet.next()) {
                            successName = false;
                        } else {
                            //插入用户数据
                            final String SQL_user = "INSERT INTO USERINFO "
                                    +"VALUES ("
                                    +null+","
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
                            pps = conn.prepareStatement(SQL_user);
                            int status1 = pps.executeUpdate();
                            System.out.println("状态1"+status1);

                            if (status1 > 0) {

                                final String SQL_medicine = "CREATE TABLE `"+lname+"`  (" +
                                        "  `KEYID` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `NAME` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `IMAGE` longblob NOT NULL," +
                                        "  `DESCRIPTION` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `OUTDATE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `OTC` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `BARCODE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `YU` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `ELABEL` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `LOVE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `SHARE` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `MUSE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  `COMPANY` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                                        "  PRIMARY KEY (`KEYID`) USING BTREE" +
                                        ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;";

                                pps = conn.prepareStatement(SQL_medicine);
                                int status2 = pps.executeUpdate();
                                System.out.println("状态2"+status2);
                                if (status2 >= 0) {
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

                                }else {
                                    new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                            .dismissOnTouchOutside(false)
                                            .dismissOnBackPressed(false)
                                            .isDestroyOnDismiss(true)
                                            .asConfirm("未知错误", "请尝试登录\n",
                                                    " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                            .show();
                                }

                            } else {
                                successLogin = false;
                            }
                            System.out.println("注册事件完成");
                        }
                        if (!successName) {
                            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                    .dismissOnTouchOutside(false)
                                    .dismissOnBackPressed(false)
                                    .isDestroyOnDismiss(true)
                                    .asConfirm("注册失败", "用户名已存在\n",
                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                    .show();
                        }
                        if (!successLogin) {
                            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                    .dismissOnTouchOutside(false)
                                    .dismissOnBackPressed(false)
                                    .isDestroyOnDismiss(true)
                                    .asConfirm("注册失败", "网络错误，请重试\n",
                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                    .show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .isDestroyOnDismiss(true)
                                .asConfirm("注册失败", "注册失败\n" + e.toString(),
                                        " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                .show(); // 最后一个参数绑定已有布局
                        System.out.println("发生错误");
                    } finally {
                        JdbcUtils.closeConnection(conn, pps, resultSet);
                    }

                }
            }).start();


        }


    }

    private void startLogin() throws Exception {

        if (tf_l_userlname.getText().trim().length() == 0) {
            t_l_userlname_status.setText("密码错误，请检查！");
            t_l_userlname_status.setVisibility(Component.VISIBLE);
            tf_l_userlname.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
        } else if (tf_l_userpwd.getText().trim().length() == 0) {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("登陆失败", "密码为空",
                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else {
            t_l_userlname_status.setVisibility(Component.HIDE);
            t_l_userpwd_status.setVisibility(Component.HIDE);
            System.out.println("可以登录");
            String lname = tf_l_userlname.getText().trim();
            String pwd = util.getSerect(lname, tf_l_userpwd.getText().trim());


            //先检测是否用户存在
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection conn = null;
                    PreparedStatement pps = null;
                    java.sql.ResultSet resultSet = null;
                    try {
                        conn = JdbcUtils.getConnection();
                        pps = conn.prepareStatement("SELECT * FROM USERINFO WHERE LNAME = ?");
                        pps.setString(1, lname);
                        resultSet = pps.executeQuery();
                        if (resultSet.next()) {
                            resultSet.first();
                            do {
                                //异步处理UI
//                                try {
//                                    getMainTaskDispatcher().asyncDispatch(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                t_l_head.setPixelMap(util.byte2PixelMap(resultSet_Has.getBytes(CONNECT_DB_HEAD)));
//                                            } catch (SQLException throwables) {
//                                                throwables.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }

                                if (pwd.equals(resultSet.getString(CONNECT_DB_PWD))) {
                                    String id, lname, sname, pwd, friend, phone, mail, rgtime, online, has, vip, vipyu;
                                    byte[] head;
                                    id = "P-" + resultSet.getString(CONNECT_DB_LNAME);
                                    lname = resultSet.getString(CONNECT_DB_LNAME);
                                    sname = resultSet.getString(CONNECT_DB_SNAME);
                                    pwd = resultSet.getString(CONNECT_DB_PWD);
                                    head = resultSet.getBytes(CONNECT_DB_HEAD);
                                    friend = resultSet.getString(CONNECT_DB_FRIEND);
                                    phone = resultSet.getString(CONNECT_DB_PHONE);
                                    mail = resultSet.getString(CONNECT_DB_MAIL);
                                    rgtime = resultSet.getString(CONNECT_DB_RGTIME);
                                    online = resultSet.getString(CONNECT_DB_ONLINE);
                                    has = resultSet.getString(CONNECT_DB_HAS);
                                    vip = resultSet.getString(CONNECT_DB_VIP);
                                    vipyu = resultSet.getString(CONNECT_DB_VIPYU);
                                    System.out.println("登录的" + Arrays.toString(head));
                                    insertPerson(id,
                                            lname,
                                            sname,
                                            pwd,
                                            head,
                                            friend,
                                            phone,
                                            mail,
                                            rgtime,
                                            online,
                                            has,
                                            vip,
                                            vipyu
                                    );
                                } else {
                                    //异步处理UI
                                    getMainTaskDispatcher().asyncDispatch(new Runnable() {
                                        @Override
                                        public void run() {
                                            t_l_userpwd_status.setText("密码错误，请检查！");
                                            t_l_userpwd_status.setVisibility(Component.VISIBLE);
                                            tf_l_userpwd.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_err));
                                        }
                                    });


                                }
                            } while (resultSet.next());
                        } else {
                            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                    .dismissOnTouchOutside(false)
                                    .dismissOnBackPressed(false)
                                    .isDestroyOnDismiss(true)
                                    .asConfirm("登陆失败", "用户不存在",
                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                    .show(); // 最后一个参数绑定已有布局
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .isDestroyOnDismiss(true)
                                .asConfirm("登陆失败", "发生错误\n" + e.toString(),
                                        " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                                .show(); // 最后一个参数绑定已有布局
                    } finally {
                        JdbcUtils.closeConnection(conn, pps, resultSet);
                    }
                }
            }).start();
        }
    }


    public void insertPerson(String id, String lname, String sname,
                             String pwd, byte[] head, String friend,
                             String phone, String mail, String rgtime,
                             String online, String has, String vip, String vipyu) {
        String[] columns = new String[]{
                CONNECT_DB_ID,
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

        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(CONNECT_DB_LNAME, lname);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
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
                        util.PreferenceUtils.putString(this, "rlok", "ok");
                        util.PreferenceUtils.putString(this, "localperson", lname);
                        System.out.println("person insert successful");
                        System.out.println("注册/登录成功");
                        terminate();
                    }
                } catch (DataAbilityRemoteException | IllegalStateException exception) {
//            ToastUtil.showToast(this, "注册成功，但登录失败，请重试  ");
                    exception.printStackTrace();
                    System.out.println("登录出错");
                }
            } else {
                ValuesBucket valuesBucket = new ValuesBucket();
                valuesBucket.putString(CONNECT_DB_ID, id);
                valuesBucket.putString(CONNECT_DB_LNAME, lname);
                valuesBucket.putString(CONNECT_DB_SNAME, sname);
                valuesBucket.putString(CONNECT_DB_PWD, pwd);
                valuesBucket.putByteArray(CONNECT_DB_HEAD, head);
                valuesBucket.putString(CONNECT_DB_FRIEND, friend);
                valuesBucket.putString(CONNECT_DB_PHONE, phone);
                valuesBucket.putString(CONNECT_DB_MAIL, mail);
                valuesBucket.putString(CONNECT_DB_RGTIME, rgtime);
                valuesBucket.putString(CONNECT_DB_ONLINE, online);
                valuesBucket.putString(CONNECT_DB_HAS, has);
                valuesBucket.putString(CONNECT_DB_VIP, vip);
                valuesBucket.putString(CONNECT_DB_VIPYU, vipyu);
                try {
                    if (databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates) != -1) {
                        terminate();
                        ToastUtil.showToast(getContext(), "登录成功");

                    }
                } catch (DataAbilityRemoteException | IllegalStateException exception) {
                    exception.printStackTrace();
                    ToastUtil.showToast(getContext(), "登录失误");
                }
            }


        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            exception.printStackTrace();

        }

    }
}
