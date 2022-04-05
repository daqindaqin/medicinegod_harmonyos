package com.daqin.medicinegod.slice;


import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.data.MedicineDataAbility;
import com.daqin.medicinegod.data.WebDataAbility;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeDetailAbilitySlice extends AbilitySlice {

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

    private static final Pattern userlname = Pattern.compile("^[A-Za-z0-9]{6,12}+$");

    private static final Pattern userpwd = Pattern.compile("^[A-Za-z0-9._@]{6,16}+$");
    private static final Pattern usermail = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    private static final Pattern userphone = Pattern.compile("^[0-9]{11}+$");

    private static String personid;
    private static Map<String, Object> personData = new HashMap<>();
    Image me_dtl_head;
    Text me_dtl_lname;
    Text me_dtl_sname;
    Text me_dtl_pwd;
    Text me_dtl_phone;
    Text me_dtl_mail;
    Text me_dtl_rgtime;
    Text me_dtl_vipdate;
    String lname = "";
    String sname = "";
    String pwd = "";
    String phone = "";
    String mail = "";


    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_me_detial);
        initView();

        personid = util.PreferenceUtils.getString(getContext(), "localperson");
        //获取数据,无数据退出
        int isLogin = util.PreferenceUtils.getInt(getContext(), "isLogin");
        if (isLogin != 0) {
            personData = MainAbilitySlice.queryPerson(personid);
            if (personData != null && !personData.toString().equals("{}")) {
                me_dtl_head.setCornerRadius(150);
                me_dtl_head.setPixelMap(util.byte2PixelMap((byte[]) personData.get(CONNECT_DB_HEAD)));
                me_dtl_head.setClickedListener(component -> editPersonData(0));
                lname = (String) personData.get(CONNECT_DB_LNAME);
                sname = (String) personData.get(CONNECT_DB_SNAME);
                pwd = (String) personData.get(CONNECT_DB_PWD);
                phone = (String) personData.get(CONNECT_DB_PHONE);
                mail = (String) personData.get(CONNECT_DB_MAIL);

                me_dtl_lname.setText(lname);
                me_dtl_lname.setClickedListener(component -> editPersonData(1));
                me_dtl_sname.setText(sname);
                me_dtl_sname.setClickedListener(component -> editPersonData(2));

                me_dtl_pwd.setText("*** *** ***");
                me_dtl_pwd.setClickedListener(component -> editPersonData(3));

                me_dtl_phone.setText(phone.substring(0, 2) + "****" + phone.substring(7));
                me_dtl_phone.setClickedListener(component -> editPersonData(4));

                me_dtl_mail.setText(mail.substring(0, 3) + "****@***.com");
                me_dtl_mail.setClickedListener(component -> editPersonData(5));

                me_dtl_rgtime.setText(util.getStringFromDate(Long.valueOf(personData.get(CONNECT_DB_RGTIME).toString())));
                if (Integer.parseInt(personData.get(CONNECT_DB_VIP).toString()) != 0) {
                    System.out.println(util.getStringFromDate(Long.valueOf(personData.get(CONNECT_DB_VIPYU).toString())));
                    me_dtl_vipdate.setText(util.getStringFromDate(Long.valueOf(personData.get(CONNECT_DB_VIPYU).toString())));
                }
            } else {
                new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .asConfirm("出现错误", "无数据，请检查",
                                " ", "返回", new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        terminate();
                                    }
                                }, new OnCancelListener() {
                                    @Override
                                    public void onCancel() {
                                        terminate();
                                    }
                                }, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                        .show(); // 最后一个参数绑定已有布局
            }
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
                        byte[] img = ImageSaver.getInstance().getByte();

                        boolean ok = WebDataAbility.editLname("update USERINFO set HEAD = '" + Arrays.toString(img)+ "' where LNAME = '" + lname + "';");
                        if (!ok) {
                            me_dtl_head.setPixelMap(util.byte2PixelMap(img));
                            MainAbilitySlice.updateHead(lname, img);
                        } else {
                            ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                        }


                    }
                }

                break;
        }
    }



    private void editPersonData(int method) {
        util.PreferenceUtils.putInt(getContext(), "editPerson",1);

        /**
         * @param method 修改方式
         *               0：head
         *               1：lname
         *               2：sname
         *               3：pwd
         *               4: phone
         *               5: mail
         * */
        switch (method) {
            case 0:
                openGallery();
                break;
            case 1:
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true) // 暂无实现
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true)
                        .isDestroyOnDismiss(true)
                        .setComponent(me_dtl_lname) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("修改登录名", "", null, "", new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String s) {
                                Matcher matcher;
                                matcher = userlname.matcher(s.trim());
                                if (matcher.matches()) {
                                    boolean ok = WebDataAbility.editLname("update USERINFO set LNAME = '" + s.trim() + "' where LNAME = '" + lname + "';");
                                    if (!ok) {
                                        me_dtl_lname.setText(s.trim());
                                        MainAbilitySlice.updateLname(lname, s.trim());
                                        lname = s.trim();
                                    } else {
                                        ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                                    }

                                } else {
                                    ToastUtil.showToast(getContext(), "输入内容不合规范  ");
                                }

                            }
                        }, null, ResourceTable.Layout_popup_comfirm_with_input_number)
                        .show();
                break;
            case 2:
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true) // 暂无实现
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true)
                        .isDestroyOnDismiss(true)
                        .setComponent(me_dtl_sname) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("修改外显名", "", null, "", new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String s) {
                                if (!s.trim().equals("")) {
                                    boolean ok = WebDataAbility.editLname("update USERINFO set SNAME = '" + s.trim() + "' where SNAME = '" + sname + "';");
                                    if (!ok) {
                                        me_dtl_sname.setText(s.trim());
                                        MainAbilitySlice.updateSname(lname, s.trim());
                                        sname = s.trim();
                                    } else {
                                        ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                                    }

                                } else {
                                    ToastUtil.showToast(getContext(), "输入内容不合规范  ");
                                }
                            }
                        }, null, ResourceTable.Layout_popup_comfirm_with_input_number)
                        .show();
                break;
            case 3:
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true) // 暂无实现
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true)
                        .isDestroyOnDismiss(true)
                        .setComponent(me_dtl_pwd) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("修改密码", "", null, "", new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String s) {
                                Matcher matcher;
                                matcher = userpwd.matcher(s.trim());
                                String passw = null;
                                try {
                                    passw = util.getSerect(lname, s.trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (matcher.matches()) {
                                    boolean ok = WebDataAbility.editLname("update USERINFO set PWD = '" + passw + "' where PWD = '" + pwd + "';");
                                    if (!ok) {
                                        MainAbilitySlice.updatePwd(lname, passw);
                                        me_dtl_pwd.setText("*********");
                                    } else {
                                        ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                                    }

                                } else {
                                    ToastUtil.showToast(getContext(), "输入内容不合规范  ");
                                }
                            }
                        }, null, ResourceTable.Layout_popup_comfirm_with_input_number)
                        .show();
                break;
            case 4:
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true) // 暂无实现
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true)
                        .isDestroyOnDismiss(true)
                        .setComponent(me_dtl_phone) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("修改手机", "", null, "", new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String s) {
                                Matcher matcher;
                                matcher = userphone.matcher(s.trim());
                                if (matcher.matches()) {
                                    boolean ok = WebDataAbility.editLname("update USERINFO set PHONE = '" + s.trim() + "' where PHONE = '" + phone + "';");
                                    if (!ok) {
                                        phone = s.trim();
                                        me_dtl_phone.setText(phone.substring(0, 2) + "****" + phone.substring(7));
                                        MainAbilitySlice.updatePhone(lname, phone);
                                    } else {
                                        ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                                    }

                                } else {
                                    ToastUtil.showToast(getContext(), "输入内容不合规范  ");
                                }

                            }
                        }, null, ResourceTable.Layout_popup_comfirm_with_input_number)
                        .show();
                break;
            case 5:
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true) // 暂无实现
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(true)
                        .isDestroyOnDismiss(true)
                        .setComponent(me_dtl_mail) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("修改邮箱", "", null, "", new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String s) {
                                Matcher matcher;
                                matcher = usermail.matcher(s.trim());
                                if (matcher.matches()) {
                                    boolean ok = WebDataAbility.editLname("update USERINFO set MAIL = '" + s.trim() + "' where MAIL = '" + mail + "';");
                                    if (!ok) {
                                        me_dtl_mail.setText(s.trim().substring(0, 3) + "****@***.com");
                                        mail = s.trim();
                                        MainAbilitySlice.updateMail(lname, mail);
                                    } else {
                                        ToastUtil.showToast(getContext(), "修改失败，请重试  ");
                                    }
                                } else {
                                    ToastUtil.showToast(getContext(), "输入内容不合规范  ");
                                }
                            }
                        }, null, ResourceTable.Layout_popup_comfirm_with_input_number)
                        .show();
                break;
        }
    }

    private void initView() {
        me_dtl_head = (Image) findComponentById(ResourceTable.Id_me_dtl_head);
        me_dtl_lname = (Text) findComponentById(ResourceTable.Id_me_dtl_lname);
        me_dtl_sname = (Text) findComponentById(ResourceTable.Id_me_dtl_sname);
        me_dtl_phone = (Text) findComponentById(ResourceTable.Id_me_dtl_phone);
        me_dtl_mail = (Text) findComponentById(ResourceTable.Id_me_dtl_mail);
        me_dtl_pwd = (Text) findComponentById(ResourceTable.Id_me_dtl_pwd);
        me_dtl_rgtime = (Text) findComponentById(ResourceTable.Id_me_dtl_rgtime);
        me_dtl_vipdate = (Text) findComponentById(ResourceTable.Id_me_dtl_vipdate);
    }
}
