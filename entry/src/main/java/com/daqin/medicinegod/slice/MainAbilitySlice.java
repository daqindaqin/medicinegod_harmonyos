package com.daqin.medicinegod.slice;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 * Changes by daqin,at 2019-2022
 */

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.data.WebDataAbility;
import com.daqin.medicinegod.provider.ChatListItemProvider;
import com.daqin.medicinegod.provider.HomePageListItemProvider;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.provider.MainScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.*;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.lxj.xpopup.util.ToastUtil;
import com.sxu.shadowdrawable.ShadowDrawable;
import com.ycuwq.datepicker.date.DatePicker;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.agp.window.service.WindowManager;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

//TODO:修改删除功能（去掉），适配
public class MainAbilitySlice extends AbilitySlice implements PickiTCallbacks {
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;   //自定义的一个权限请求识别码，用于处理权限回调
    private static DataAbilityHelper databaseHelper;
    private static Context cont;

    private BubbleNavigationLinearView mBubbleNavigationLinearView;
    //    private List<String> labelList = new ArrayList<>();
    private static final int RESULTCODE_IMAGE_CHOOSE = 100;
    private static final int RESULTCODE_IMAGE_CROP = 101;
    private static int newUsage_utils_1 = 0, newUsage_utils_3 = 0, elabelCount = 0;
    private static int dataHas = 0, dataFav = 0, dataShare = 0, style = 0;
    List<String> eLABEL = new ArrayList<>();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "MainAbilitySlice");
    private static final String BASE_URI_MEDICINE = "dataability:///com.daqin.medicinegod.data.MedicineDataAbility";
    private static final String DATA_PATH_MEDICINE = "/medicine";
    private static final String DB_COLUMN_MEDICINE_KEYID = "KEYID";
    private static final String DB_COLUMN_MEDICINE_NAME = "NAME";
    private static final String DB_COLUMN_MEDICINE_IMAGE = "IMAGE";
    private static final String DB_COLUMN_MEDICINE_DESCRIPTION = "DESCRIPTION";
    private static final String DB_COLUMN_MEDICINE_OUTDATE = "OUTDATE";
    private static final String DB_COLUMN_MEDICINE_OTC = "OTC";
    private static final String DB_COLUMN_MEDICINE_BARCODE = "BARCODE";
    private static final String DB_COLUMN_MEDICINE_USAGE = "USAGE";
    private static final String DB_COLUMN_MEDICINE_COMPANY = "COMPANY";
    private static final String DB_COLUMN_MEDICINE_YU = "YU";
    private static final String DB_COLUMN_MEDICINE_ELABEL = "ELABEL";
    private static final String DB_COLUMN_MEDICINE_LOVE = "LOVE";
    private static final String DB_COLUMN_MEDICINE_SHARE = "SHARE";
    private static final String DB_COLUMN_MEDICINE_DELECT = "DELECT";

    private static String[] columns_medicine = new String[]{};

    private static final String BASE_URI_PERSON = "dataability:///com.daqin.medicinegod.data.PersonDataAbility";
    private static final String DATA_PATH_PERSON = "/person";
    private static final String DB_COLUMN_PERSON_ID = "ID";
    private static final String DB_COLUMN_PERSON_LNAME = "LNAME";
    private static final String DB_COLUMN_PERSON_SNAME = "SNAME";
    private static final String DB_COLUMN_PERSON_PWD = "PWD";
    private static final String DB_COLUMN_PERSON_HEAD = "HEAD";
    private static final String DB_COLUMN_PERSON_FRIEND = "FRIEND";
    private static final String DB_COLUMN_PERSON_PHONE = "PHONE";
    private static final String DB_COLUMN_PERSON_MAIL = "MAIL";
    private static final String DB_COLUMN_PERSON_RGTIME = "RGTIME";
    private static final String DB_COLUMN_PERSON_ONLINE = "ONLINE";
    private static final String DB_COLUMN_PERSON_HAS = "HAS";
    private static final String DB_COLUMN_PERSON_VIP = "VIP";
    private static final String DB_COLUMN_PERSON_VIPYU = "VIPYU";
    private static String[] columns_person = new String[]{};

    private static Map<String, Object> personData = new HashMap<>();
    /**
     * @param img 图片最终数据
     * @param imgbytes 图片待裁剪数据
     * @param imgdefault 默认图片
     */
    private byte[] img = null;
    private static byte[] imgbytes = null;
    private static byte[] imgdefault;
    private static String localPerson = "";
    Image img_hp_head;
    Image btn_add_img;
    Button btn_add_clear_context;
    Button btn_add_import;
    Text btn_add_otc_question;
    Text btn_add_newUsage_utils_1;
    Text btn_add_newUsage_utils_2;
    Text btn_add_newUsage_utils_3;
    Text btn_add_yu_title;
    Text btn_things_seach;
    Text btn_changestyle;
    ScrollView view_add;
    TextField tf_add_name;
    TextField tf_add_desp;

    Text btn_add_date;
    String outdate;


    Picker tf_add_otc;
    TextField tf_add_barcode;
    TextField tf_add_usage_total;
    TextField tf_add_usage_time;
    TextField tf_add_usage_day;
    TextField tf_add_company;
    TextField tf_add_yu;
    Button btn_add_ok;
    Button btn_addNewLabel;
    TextField tf_add_elabelBox;
    Text t_add_elabel_title;
    Text t_add_elabel1;
    Text t_add_elabel2;
    Text t_add_elabel3;
    Text t_add_elabel4;
    Text t_add_elabel5;
    Text t_add_imgcrop;
    Text t_add_imgclaer;
    Text t_add_imgdefault;


    Image img_homehead;
    Text t_me_sname;
    Text t_me_lname;
    Text t_me_vip;
    Text t_me_date_out;
    Text t_me_date_near;
    Text t_me_date_ok;
    Text t_me_has;
    Text t_me_fav;
    Text t_me_share;
    Text t_me_tuijian;
    Text t_me_qa;
    Text t_me_faceback;
    Text t_me_about;
    Text t_me_setting;
    //主页顶部栏
    Text t_hp_title;
    Text t_hp_title_usersname;

    ImageSaver imageSaver;

    DirectionalLayout view_hp_head_of_top;
    Text[] add_elabelview;
    TextField[] add_tf_list;

    private PickiT pickiT;

    public void iniView() {
        img_hp_head = (Image) findComponentById(ResourceTable.Id_hp_image_head);
        img_hp_head.setCornerRadius(100);
        btn_add_clear_context = (Button) findComponentById(ResourceTable.Id_add_clear);
        btn_add_import = (Button) findComponentById(ResourceTable.Id_add_import);
        btn_add_otc_question = (Text) findComponentById(ResourceTable.Id_add_newOtc_question);
        btn_add_img = (Image) findComponentById(ResourceTable.Id_add_newImg);
        btn_add_newUsage_utils_1 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_1);
        btn_add_newUsage_utils_2 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_2);
        btn_add_newUsage_utils_3 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_3);
        btn_add_yu_title = (Text) findComponentById(ResourceTable.Id_add_newYu_title);
        btn_things_seach = (Text) findComponentById(ResourceTable.Id_things_textField_search);
        view_add = (ScrollView) findComponentById(ResourceTable.Id_add_scrollview);
        view_hp_head_of_top = (DirectionalLayout) findComponentById(ResourceTable.Id_hp_head_of_top);
        ShadowDrawable.setShadowDrawable(view_hp_head_of_top, Color.getIntColor("#FFFFFF"), 50, Color.getIntColor("#25000000"), 5, 0, 10);
        tf_add_name = (TextField) findComponentById(ResourceTable.Id_add_newName);
        tf_add_desp = (TextField) findComponentById(ResourceTable.Id_add_newDescription);
        btn_add_date = (Text) findComponentById(ResourceTable.Id_add_newOutDate);

        tf_add_otc = (Picker) findComponentById(ResourceTable.Id_add_newOtc);
        tf_add_barcode = (TextField) findComponentById(ResourceTable.Id_add_newbarCode);
        tf_add_usage_total = (TextField) findComponentById(ResourceTable.Id_add_newUsage_1);
        tf_add_usage_time = (TextField) findComponentById(ResourceTable.Id_add_newUsage_2);
        tf_add_usage_day = (TextField) findComponentById(ResourceTable.Id_add_newUsage_3);
        tf_add_company = (TextField) findComponentById(ResourceTable.Id_add_newCompany);
        tf_add_yu = (TextField) findComponentById(ResourceTable.Id_add_newYu);
        btn_add_ok = (Button) findComponentById(ResourceTable.Id_add_addOk);
        btn_addNewLabel = (Button) findComponentById(ResourceTable.Id_add_newLabel_addButton);
        tf_add_elabelBox = (TextField) findComponentById(ResourceTable.Id_add_newLabel_addTextFiled);
        t_add_elabel_title = (Text) findComponentById(ResourceTable.Id_add_newLabel_title);
        t_add_elabel1 = (Text) findComponentById(ResourceTable.Id_add_addNewlabel_label1);
        t_add_elabel2 = (Text) findComponentById(ResourceTable.Id_add_addNewlabel_label2);
        t_add_elabel3 = (Text) findComponentById(ResourceTable.Id_add_addNewlabel_label3);
        t_add_elabel4 = (Text) findComponentById(ResourceTable.Id_add_addNewlabel_label4);
        t_add_elabel5 = (Text) findComponentById(ResourceTable.Id_add_addNewlabel_label5);
        t_add_imgcrop = (Text) findComponentById(ResourceTable.Id_add_imgcrop);
        t_add_imgclaer = (Text) findComponentById(ResourceTable.Id_add_imgclear);
        t_add_imgdefault = (Text) findComponentById(ResourceTable.Id_add_imgdefault);
        btn_changestyle = (Text) findComponentById(ResourceTable.Id_changestyle);
        t_hp_title = (Text) findComponentById(ResourceTable.Id_hp_showtitle);
        t_hp_title_usersname = (Text) findComponentById(ResourceTable.Id_hp_showtitle_username);




        add_elabelview = new Text[]{t_add_elabel1, t_add_elabel2, t_add_elabel3, t_add_elabel4, t_add_elabel5};
        add_tf_list = new TextField[]{tf_add_name, tf_add_desp, tf_add_barcode, tf_add_usage_total, tf_add_usage_time, tf_add_usage_day, tf_add_company, tf_add_yu};
    }

    public void iniClicklistener() {
        btn_add_date.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment(getContext());
                datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                    @Override
                    public void onDateChoose(int year, int month, int day) {
                        outdate = year + "-" + month + "-1";
                        btn_add_date.setText("已选:" + outdate);
                    }
                });
                datePickerDialogFragment.show();
            }
        });

        btn_add_import.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                //要连接的数据库url,注意：此处连接的应该是服务器上的MySQl的地址
                String url = "jdbc:mysql://139.224.48.87:3306/mg?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT";
                //连接数据库使用的用户名
                String userName = "mg";
                //连接的数据库时使用的密码
                String password = "mg@Qhx010394";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //1、加载驱动
                            Class.forName("com.mysql.jdbc.Driver").newInstance();
                            //3.连接成功，返回数据库对象
                            Connection connection = DriverManager.getConnection(url, userName, password);
                            //4.执行SQL的对象
                            Statement statement = connection.createStatement();
                            //5.执行SQL的对象去执行SQL,可能存在结果，查看返回结果
                            String sql = "SELECT * FROM USERINFO";
                            java.sql.ResultSet resultSet = statement.executeQuery(sql);//返回的结果集,结果集中封装了我们全部的查询出来的结果
                            resultSet.toString();
                            while (resultSet.next()) {

                                System.out.println("type_id=" + resultSet.getObject("username"));
                                System.out.println("type_name=" + resultSet.getObject("userpwd"));
                                System.out.println("delete_time=" + resultSet.getObject("userid"));
                            }
                            //6.释放连接
                            resultSet.close();
                            statement.close();
                            connection.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

        btn_changestyle.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Intent intentSearch = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                        .withBundleName(getBundleName())    // 包名
                        .withAbilityName("com.daqin.medicinegod.ChangeStyleAbility")
                        // Ability页面的名称，在本地可以缺省前面的路径
                        .build();    // 构建代码
                intentSearch.setOperation(operation);    // 将operation存入到intent中
                startAbilityForResult(intentSearch, 300);    // 实现Ability跳转
            }
        });
        //清空添加药品的列表
        btn_add_clear_context.setClickedListener(component -> clearAddTextfield());
        //otc疑问按钮
        btn_add_otc_question.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .moveUpToKeyboard(false) // 如果不加这个，评论弹窗会移动到软键盘上面
                    .enableDrag(true)
                    .asCustom(new OTCQuestionAbilitySlice(getContext()))
                    .show();
        });
        //搜索按钮
        btn_things_seach.setClickedListener(component -> {
            Intent intentSearch = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                    .withBundleName(getBundleName())    // 包名
                    .withAbilityName("com.daqin.medicinegod.SearchAbility")
                    // Ability页面的名称，在本地可以缺省前面的路径
                    .build();    // 构建代码
            intentSearch.setOperation(operation);    // 将operation存入到intent中
            startAbility(intentSearch);    // 实现Ability跳转
        });


        t_add_imgcrop.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (imgbytes == null) {
                    ToastUtil.showToast(getContext(), "请先选择图片才能裁剪  ");
                } else if (imgbytes == imgdefault) {
                    ToastUtil.showToast(getContext(), "默认图片无法裁剪  ");
                } else {
                    Intent intent = new Intent();
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")
                            .withBundleName(getBundleName())
                            .withAbilityName("com.daqin.medicinegod.ImageControlAbility")
                            .build();
                    intent.setOperation(operation);
                    ImageSaver.getInstance().setByte(imgbytes);
//                    imageSaver.setByte(imgbytes);
                    System.out.println("开始传输");
//                    intent.setParam("startcropimage", imgbytes);
                    startAbilityForResult(intent, RESULTCODE_IMAGE_CROP);
                }

            }
        });
        //清除已有的图片
        t_add_imgclaer.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                imgbytes = null;
                imgdefault = null;
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
                btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
            }
        });
        //设置默认图片
        t_add_imgdefault.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgdefault);
                btn_add_img.setScaleMode(Image.ScaleMode.STRETCH);
                imgdefault = util.pixelMap2byte(btn_add_img.getPixelMap());
                imgbytes = imgdefault;
            }
        });
        //添加图片的图片按钮
        btn_add_img.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
                    // 应用未被授予权限
                    if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                        // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                        requestPermissionsFromUser(
                                new String[]{"ohos.permission.READ_MEDIA"}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                        openGallery();
                        /*Intent intent = new Intent();
                        Operation opt = new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                        intent.setOperation(opt);
                        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                        intent.setType("image/*");
                        intent.setBundle("com.huawei.photos");
                        startAbilityForResult(intent, RESULTCODE_IMAGE_CHOOSE);*/
                    } else {
                        // 显示应用需要权限的理由，提示用户进入设置授权
                        ToastUtil.showToast(getContext(), "请进入系统设置进行授权  ");
                    }
                } else {
                    // 权限已被授予
                    //加载显示系统相册中的照片
                    openGallery();
                    /*Intent intent = new Intent();

                    Operation opt = new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                    intent.setOperation(opt);
                    intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                    intent.setType("image/*");
                    intent.setBundle("com.huawei.photos");
                    startAbilityForResult(intent, RESULTCODE_IMAGE_CHOOSE);*/
                }
            }
        });
        //用法用量单位变换
        btn_add_newUsage_utils_1.setClickedListener(component -> {
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1) {
                case 1:
                    btn_add_newUsage_utils_1.setText("克");
                    btn_add_yu_title.setText("剩余余量(单位:克)");
                    break;
                case 2:
                    btn_add_newUsage_utils_1.setText("包");
                    btn_add_yu_title.setText("剩余余量(单位:包)");
                    break;
                case 3:
                    btn_add_newUsage_utils_1.setText("片");
                    btn_add_yu_title.setText("剩余余量(单位:片)");
                    newUsage_utils_1 = 0;
                    break;
            }
        });
        btn_add_newUsage_utils_3.setClickedListener(component -> {
            newUsage_utils_3 += 1;
            switch (newUsage_utils_3) {
                case 1:
                    btn_add_newUsage_utils_3.setText("时");
                    break;
                case 2:
                    btn_add_newUsage_utils_3.setText("天");
                    newUsage_utils_3 = 0;
                    break;
            }
        });
        //标签栏目删除事件
        t_add_elabel1.setClickedListener(component -> {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("确认删除", "要删除" + t_add_elabel1.getText() + "这个标签吗?",
                            "返回", "删除", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    eLABEL.remove(t_add_elabel1.getText().trim());
                                    eLABEL.add("测试标签");
                                    t_add_elabel1.setText("测试标签");
                                    t_add_elabel1.setVisibility(Component.HIDE);
                                    elabelCount--;
                                    t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");

                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        t_add_elabel2.setClickedListener(component -> {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("确认删除", "要删除" + t_add_elabel2.getText() + "这个标签吗?",
                            "返回", "删除", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    eLABEL.remove(t_add_elabel2.getText().trim());
                                    eLABEL.add("测试标签");
                                    t_add_elabel2.setText("测试标签");
                                    t_add_elabel2.setVisibility(Component.HIDE);
                                    elabelCount--;
                                    t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");

                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        t_add_elabel3.setClickedListener(component -> {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("确认删除", "要删除" + t_add_elabel3.getText() + "这个标签吗?",
                            "返回", "删除", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    eLABEL.remove(t_add_elabel3.getText().trim());
                                    eLABEL.add("测试标签");
                                    t_add_elabel3.setText("测试标签");
                                    t_add_elabel3.setVisibility(Component.HIDE);
                                    elabelCount--;
                                    t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");

                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        t_add_elabel4.setClickedListener(component -> {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("确认删除", "要删除" + t_add_elabel4.getText() + "这个标签吗?",
                            "返回", "删除", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    eLABEL.remove(t_add_elabel4.getText().trim());
                                    eLABEL.add("测试标签");
                                    t_add_elabel4.setText("测试标签");
                                    t_add_elabel4.setVisibility(Component.HIDE);
                                    elabelCount--;
                                    t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");

                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        t_add_elabel5.setClickedListener(component -> {
            new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("确认删除", "要删除" + t_add_elabel5.getText() + "这个标签吗?",
                            "返回", "删除", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    eLABEL.remove(t_add_elabel5.getText().trim());
                                    eLABEL.add("测试标签");
                                    t_add_elabel5.setText("测试标签");
                                    t_add_elabel5.setVisibility(Component.HIDE);
                                    elabelCount--;
                                    t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");

                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        //添加标签事件
        btn_addNewLabel.setClickedListener(component -> {
            //是否已经达到5个标签
            if (elabelCount >= 5) {
                new XPopup.Builder(getContext())
                        //.setPopupCallback(new XPopupListener())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .asConfirm("数量受限", "已达到标签最大数量(5)",
                                " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                        .show(); // 最后一个参数绑定已有布局
                //未达到5个标签则判断
            } else {
                if (tf_add_elabelBox.length() == 0
                        || tf_add_elabelBox.length() >= 5) {
                    //输入框内条件标签不满足则提示
                    new XPopup.Builder(getContext())
                            //.setPopupCallback(new XPopupListener())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .asConfirm("格式受限", "您在一个标签内只能添加1到4个中文字符",
                                    " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                            .show(); // 最后一个参数绑定已有布局
                } else if (eLABEL.contains(tf_add_elabelBox.getText().trim())) {
                    //不允许存在相同标签
                    new XPopup.Builder(getContext())
                            //.setPopupCallback(new XPopupListener())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .asConfirm("数量受限", "您只能添加此标签一次",
                                    " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                            .show(); // 最后一个参数绑定已有布局
                } else if (tf_add_elabelBox.length() > 0 && tf_add_elabelBox.length() < 5) {
                    for (Text text : add_elabelview) {
                        if (text.getText().equals("测试标签")) {
                            elabelCount++;
                            eLABEL.add(tf_add_elabelBox.getText().trim());
                            text.setText(tf_add_elabelBox.getText().trim());
                            text.setVisibility(Component.VISIBLE);
                            tf_add_elabelBox.setText("");
                            t_add_elabel_title.setText("添加药效标签(" + elabelCount + "/5)");
                            break;
                        }
                    }


                }
            }
        });
        //添加药品数据事件
        btn_add_ok.setClickedListener(component -> {

            //检测已经选择图片
            if (imgbytes == null) {
                view_add.fluentScrollTo(0, btn_add_img.getTop() - 100);
                ToastUtil.showToast(this, "图片不能为空");
            } else if (eLABEL == null || elabelCount == 0) {
                view_add.fluentScrollTo(0, btn_add_ok.getTop());
                ToastUtil.showToast(this, "药品标签不能为空");
            } else if (outdate.equals("")) {
                view_add.fluentScrollTo(0, btn_add_date.getTop());
                ToastUtil.showToast(this, "药品标签不能为空");
            } else {
                //计次，计算是否都填好了
                int count = 0;
                //检测里面是否为空
                for (TextField box : add_tf_list) {
                    if (box.getText().length() == 0 || box.getText().equals(" ")) {
                        //为空则跳转
                        box.setFocusable(Component.FOCUS_ADAPTABLE);
                        box.setTouchFocusable(true);
                        box.requestFocus();
                        //使view滑动到指定位置
                        ToastUtil.showToast(this, "有未填写空白，请填写后提交");
                        view_add.fluentScrollTo(0, box.getTop() - 100);
                        break;
                    } else {
                        box.clearFocus();
                        count++;
                    }
                }
                if (count >= 8) {
                    //添加数据
                    String key = util.getRandomKeyId();
                    System.out.println("生成key" + key);
                    String usa1, usa2, usa3, usageall;
                    usa1 = tf_add_usage_total.getText().trim();
                    usa2 = tf_add_usage_time.getText().trim();
                    usa3 = tf_add_usage_day.getText().trim();
                    usageall = usa1 + "-" + btn_add_newUsage_utils_1.getText() + "-" + usa2 + "-" + btn_add_newUsage_utils_2.getText() + "-" + usa3 + "-" + btn_add_newUsage_utils_3.getText();
                    StringBuilder label = new StringBuilder();
                    for (String s : eLABEL) {
                        label.append(s).append("@@");
                    }
                    insert(key,
                            tf_add_name.getText(),
                            img == null ? imgbytes : img,
                            tf_add_desp.getText(),
                            outdate,
                            tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("OTC(非处方药)-绿") ? "OTC-G" : tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("OTC(非处方药)-红") ? "OTC-R" : tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("RX(处方药)") ? "Rx" : "none",
                            tf_add_barcode.getText(),
                            usageall,
                            tf_add_company.getText(),
                            tf_add_yu.getText().trim(),
                            label.toString());

                }

            }
        });


    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        this.getWindow().setInputPanelDisplayType(WindowManager.LayoutConfig.INPUT_ADJUST_RESIZE);
        pickiT = new PickiT(this, this, this);
        int isFirstStart = util.PreferenceUtils.getInt(getContext(), "isFirstStart");
        int isLogin = util.PreferenceUtils.getInt(getContext(), "isLogin");
        if (isLogin == 0 && isFirstStart == 0) {
            Intent intentSearch = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                    .withBundleName(getBundleName())    // 包名
                    .withAbilityName("com.daqin.medicinegod.RgLgAbility")
                    // Ability页面的名称，在本地可以缺省前面的路径
                    .build();    // 构建代码
            intentSearch.setOperation(operation);    // 将operation存入到intent中
            startAbility(intentSearch);    // 实现Ability跳转
        }
        if (isFirstStart == 0) {
            //TODO：权限提示的页面
            System.out.println("首次启动跳转获取权限");
            if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
                // 应用未被授予权限
                if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                    // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                    requestPermissionsFromUser(
                            new String[]{"ohos.permission.READ_MEDIA"}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
                }
            }
        }
        //一定要和库内列一模一样，否则异常
        columns_medicine = new String[]{
                DB_COLUMN_MEDICINE_KEYID,
                DB_COLUMN_MEDICINE_NAME,
                DB_COLUMN_MEDICINE_IMAGE,
                DB_COLUMN_MEDICINE_DESCRIPTION,
                DB_COLUMN_MEDICINE_OUTDATE,
                DB_COLUMN_MEDICINE_OTC,
                DB_COLUMN_MEDICINE_BARCODE,
                DB_COLUMN_MEDICINE_USAGE,
                DB_COLUMN_MEDICINE_COMPANY,
                DB_COLUMN_MEDICINE_YU,
                DB_COLUMN_MEDICINE_ELABEL,
                DB_COLUMN_MEDICINE_LOVE,
                DB_COLUMN_MEDICINE_SHARE,
                DB_COLUMN_MEDICINE_DELECT
        };
        //一定要和库内列一模一样，否则异常
        columns_person = new String[]{
                DB_COLUMN_PERSON_ID,
                DB_COLUMN_PERSON_LNAME,
                DB_COLUMN_PERSON_SNAME,
                DB_COLUMN_PERSON_PWD,
                DB_COLUMN_PERSON_HEAD,
                DB_COLUMN_PERSON_FRIEND,
                DB_COLUMN_PERSON_PHONE,
                DB_COLUMN_PERSON_MAIL,
                DB_COLUMN_PERSON_RGTIME,
                DB_COLUMN_PERSON_ONLINE,
                DB_COLUMN_PERSON_HAS,
                DB_COLUMN_PERSON_VIP,
                DB_COLUMN_PERSON_VIPYU
        };
        databaseHelper = DataAbilityHelper.creator(this);
        style = util.PreferenceUtils.getInt(getContext(), "style");
        util.PreferenceUtils.putInt(getContext(), "isFirstStart", 1);
        cont = getContext();
        imageSaver = new ImageSaver();
        imageSaver.setInstance();
        query();
        intPageStart();
        initHomepageListContainer();
        iniView();
        iniCalendarPicker();
        iniClicklistener();

//        Image img_homehead = (Image) findComponentById(ResourceTable.Id_home_image_head);
//        img_homehead.setCornerRadius(150);
//        initCommunityListContainer();
//        initChatListContainer();

    }

    @Override
    public void pickiTonUriReturned() {

    }

    @Override
    public void pickiTonStartListener() {

    }

    @Override
    public void pickiTonProgressUpdate(int i) {

    }

    @Override
    public void pickiTonCompleteListener(String path, boolean wasDriveFile,
                                         boolean wasUnknownProvider, boolean wasSuccessful, String reason) {
//  Check if it was a Drive/local/unknown provider file and display a Toast
//        if (wasDriveFile) {
//            showLongToast("Drive file was selected");
//        } else if (wasUnknownProvider) {
//            showLongToast("File was selected from unknown provider");
//        } else {
//            showLongToast("Local file was selected");
//        }
        if (wasSuccessful) {

        } else {

        }
    }


    //清空添加药品的列表
    private void clearAddTextfield() {
        eLABEL.clear();
        btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
        btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
        tf_add_name.setText("");
        tf_add_desp.setText("");
        tf_add_barcode.setText("");
        tf_add_usage_total.setText("");
        tf_add_usage_time.setText("");
        tf_add_usage_day.setText("");
        tf_add_company.setText("");
        tf_add_yu.setText("");
        btn_add_newUsage_utils_1.setText("包");
        btn_add_newUsage_utils_2.setText("次");
        btn_add_newUsage_utils_3.setText("天");
        view_add.fluentScrollTo(0, 0);
        elabelCount = 0;
        t_add_elabel1.setText("测试标签");
        t_add_elabel1.setVisibility(Component.HIDE);
        t_add_elabel2.setText("测试标签");
        t_add_elabel2.setVisibility(Component.HIDE);
        t_add_elabel3.setText("测试标签");
        t_add_elabel3.setVisibility(Component.HIDE);
        t_add_elabel4.setText("测试标签");
        t_add_elabel4.setVisibility(Component.HIDE);
        t_add_elabel5.setText("测试标签");
        t_add_elabel5.setVisibility(Component.HIDE);
        t_add_elabel_title.setText("添加药效标签(0/5)");
        imgbytes = null;
        img = null;
        btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
        btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
        iniCalendarPicker();
        ToastUtil.showToast(getContext(), "已清空内容  ");
    }

    //导航栏和窗口初始化
    private void intPageStart() {
        ArrayList<DependentLayout> fragList = new ArrayList<>();

        LayoutScatter layoutScatter = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView = (DependentLayout) layoutScatter.parse(
                ResourceTable.Layout_ability_main_homepage, null, false);
        fragList.add(inflatedView);

        LayoutScatter layoutScatter1 = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView1 = (DependentLayout) layoutScatter1.parse(
                ResourceTable.Layout_ability_main_community, null, false);
        fragList.add(inflatedView1);

        LayoutScatter layoutScatter2 = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView2 = (DependentLayout) layoutScatter2.parse(
                ResourceTable.Layout_ability_main_add, null, false);
        fragList.add(inflatedView2);

        LayoutScatter layoutScatter3 = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView3 = (DependentLayout) layoutScatter3.parse(
                ResourceTable.Layout_ability_main_chat, null, false);
        fragList.add(inflatedView3);

        LayoutScatter layoutScatter4 = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView4 = (DependentLayout) layoutScatter4.parse(
                ResourceTable.Layout_ability_main_me, null, false);
        fragList.add(inflatedView4);
        initPagerSlider(fragList);

    }

    //底部栏相关
    private void initPagerSlider(ArrayList<DependentLayout> fragList) {
        MainScreenSlidePagerProvider pagerAdapter = new MainScreenSlidePagerProvider(this, fragList);

        mBubbleNavigationLinearView =
                (BubbleNavigationLinearView) findComponentById(ResourceTable.Id_bottom_navigation_view_linear);

        mBubbleNavigationLinearView.setBadgeValue(0, "40");
        mBubbleNavigationLinearView.setBadgeValue(1, "99+");
        mBubbleNavigationLinearView.setBadgeValue(2, null);
        mBubbleNavigationLinearView.setBadgeValue(3, "2");
        mBubbleNavigationLinearView.setBadgeValue(4, "1");

        final PageSlider viewPager = (PageSlider) findComponentById(ResourceTable.Id_view_pager);
        viewPager.setProvider(pagerAdapter);
        viewPager.addPageChangedListener(new PageSlider.PageChangedListener() {
            @Override
            public void onPageSliding(int i, float v, int i1) {
            }

            @Override
            public void onPageSlideStateChanged(int i) {
            }

            @Override
            public void onPageChosen(int i) {
                mBubbleNavigationLinearView.setCurrentActiveItem(i);
            }
        });

        mBubbleNavigationLinearView.setNavigationChangeListener((view, position) ->
        {
            //添加药品的页面刷新多选框
            viewPager.setCurrentPage(position, true);
            switch (position) {
                case 0:
                    initHomepageListContainer();
                    break;
                case 1:
                    //TODO:修改社区显示内容，卡顿与繁杂
//                    initCommunityListContainer();
                    break;
                case 2:
                    iniCalendarPicker();
                    break;
                case 3:
                    initChatListContainer();
                    break;
                case 4:
                    initMe();
                    break;
                default:
                    break;
            }
        });
    }

    private void initMe() {
        if (personData != null && !personData.toString().equals("{}")) {
            t_me_sname = (Text) findComponentById(ResourceTable.Id_me_sname);
            t_me_lname = (Text) findComponentById(ResourceTable.Id_me_lname);
            t_me_vip = (Text) findComponentById(ResourceTable.Id_me_vip);
            t_me_date_out = (Text) findComponentById(ResourceTable.Id_me_date_out);
            t_me_date_near = (Text) findComponentById(ResourceTable.Id_me_date_near);
            t_me_date_ok = (Text) findComponentById(ResourceTable.Id_me_date_ok);
            t_me_has = (Text) findComponentById(ResourceTable.Id_me_has);
            t_me_fav = (Text) findComponentById(ResourceTable.Id_me_fav);
            t_me_share = (Text) findComponentById(ResourceTable.Id_me_share);
            t_me_tuijian = (Text) findComponentById(ResourceTable.Id_me_tuijian);
            t_me_qa = (Text) findComponentById(ResourceTable.Id_me_qa);
            t_me_faceback = (Text) findComponentById(ResourceTable.Id_me_faceback);
            t_me_about = (Text) findComponentById(ResourceTable.Id_me_about);
            t_me_setting = (Text) findComponentById(ResourceTable.Id_me_setting);
            img_homehead = (Image) findComponentById(ResourceTable.Id_me_head);
            img_homehead.setCornerRadius(200);
            try {
                img_homehead.setPixelMap(util.byte2PixelMap((byte[]) personData.get(DB_COLUMN_PERSON_HEAD)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            t_me_sname.setText((String) personData.get(DB_COLUMN_PERSON_SNAME));
            t_me_lname.setText("ID:" + (String) personData.get(DB_COLUMN_PERSON_LNAME));
            String vip = (String) personData.get(DB_COLUMN_PERSON_VIP);
            if (vip.equals("0")) {
                t_me_vip.setText("免费会员");
            } else {
                t_me_vip.setText(" VIP ");
            }

        }

        t_me_date_out.setText(util.PreferenceUtils.getInt(getContext(), "date_out") + "");
        t_me_date_near.setText(util.PreferenceUtils.getInt(getContext(), "date_near") + "");
        t_me_date_ok.setText(util.PreferenceUtils.getInt(getContext(), "date_ok") + "");
        t_me_has.setText(String.valueOf(dataHas));
        t_me_fav.setText(String.valueOf(dataFav));
        t_me_share.setText(String.valueOf(dataShare));
        t_me_lname.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (t_me_lname.getText().trim().equals("ID:未登录")) {
                    Intent intentSearch = new Intent();
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                            .withBundleName(getBundleName())    // 包名
                            .withAbilityName("com.daqin.medicinegod.RgLgAbility")
                            // Ability页面的名称，在本地可以缺省前面的路径
                            .build();    // 构建代码
                    intentSearch.setOperation(operation);    // 将operation存入到intent中
                    startAbility(intentSearch);    // 实现Ability跳转
                } else {
                    Intent intentSearch = new Intent();
                    Operation operation = new Intent.OperationBuilder()
                            .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                            .withBundleName(getBundleName())    // 包名
                            .withAbilityName("com.daqin.medicinegod.MeDetailAbility")
                            // Ability页面的名称，在本地可以缺省前面的路径
                            .build();    // 构建代码
                    intentSearch.setOperation(operation);    // 将operation存入到intent中
                    startAbility(intentSearch);    // 实现Ability跳转
                }
            }
        });
        t_me_tuijian.setClickedListener(component -> {
            //TODO:推荐给好友
        });
        t_me_qa.setClickedListener(component -> {
            //TODO:疑难解答
        });
        t_me_faceback.setClickedListener(component -> {
            //TODO:反馈
        });
        t_me_about.setClickedListener(component -> {
            //TODO:关于
        });
        t_me_setting.setClickedListener(component -> {

        });

    }

    //TODO：图片转换出错
    // 初始化社区
    /*
    private void initCommunityListContainer(){
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_community_contextlist);

        // 2.实例化数据源
        List<Map<String,Object>> list = getCommunityData();
        // 3.初始化Provider对象
        CommunityListItemProvider listItemProvider = new CommunityListItemProvider(list,this);
        // 4.适配要展示的内容数据
        listContainer.setItemProvider(listItemProvider);
        // 5.设置每个Item的点击事件
        listContainer.setItemClickedListener((container, component, position, id) -> {
            Map<String,Object> item = (Map<String,Object>) listContainer.getItemProvider().getItem(position);
            new ToastDialog(this)
                    .setDuration(2000)
                    .setText("你点击了:" + item.get("commname")+"，"+item.get("commtime"))
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();

        });

    }
    // 初始化社区主页的数据源
    private List<Map<String,Object>> getCommunityData(){
        List<Map<String,Object>> list;
        // icon图标
        int[] imghead = {ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head};
        int[] imgpho = {ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test};
        String[] names={"曹操","刘备","关羽","诸葛亮","小乔","貂蝉","吕布","赵云","黄盖","周瑜"};
        String[] qianming={"一代枭雄","卖草鞋","财神","卧龙先生","周瑜媳妇","四大镁铝","天下无双","常胜将军","愿意挨打","愿意打人"};
        String[] time={"2002-1-1","2102-1-1","2102-1-1","2242-1-1","2202-4-1","2202-2-1","2202-7-1","2202-9-1","2202-3-1","2203-1-1"};
        String[] context={"木大木大木大木大木大木大木大木大木大木大木大木大木大木大木大木大","木大木大木大木大木大","奥里给奥里给","大大大靠近覅","daaaaaa士大夫方法","发誓生生世世生生世世","xdfhcFAzxfhgzsxv","afgshszdx sdfgsdFszgdhfjdgzgdxhfcjcfxdzsfxd","发生过活动经费算法公式的的说法","反反复复烦烦烦烦烦烦烦烦烦萨顶顶是的"};
        list= new ArrayList<>();
        for(int i=0;i<imghead.length;i++){
            Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
            map.put("commname",names[i]);
            map.put("commqianming", qianming[i]);
            map.put("commtime", time[i]);
            map.put("commtext",context[i]);
            map.put("commhead", imghead[i]);
            map.put("commpho", imgpho[i]);

            list.add(map);
        }


        return list;
    }
    */
    private void openGallery() {
        Intent intent = new Intent();
//        intent.setType("video/*");
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        intent.setAction("android.intent.action.GET_CONTENT");
        intent.setParam("return-data", true);
        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
        intent.addFlags(0x00000001);
        startAbilityForResult(intent, RESULTCODE_IMAGE_CHOOSE);
    }

    // 初始化聊天界面的ListContainer
    private void initChatListContainer() {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_chat_chatlist);

        // 2.实例化数据源
        List<Map<String, Object>> list = getChatListData();
        // 3.初始化Provider对象
        ChatListItemProvider listItemProvider = new ChatListItemProvider(list, this);
        // 4.适配要展示的内容数据
        listContainer.setItemProvider(listItemProvider);
        // 5.设置每个Item的点击事件
        listContainer.setItemClickedListener((container, component, position, id) -> {
            Map<String, Object> item = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
            new ToastDialog(this)
                    .setDuration(2000)
                    .setText("你点击了:" + item.get("name") + "，" + item.get("lastmsg"))
                    .setAlignment(LayoutAlignment.BOTTOM)
                    .show();
        });
//        setListContainerHeight(listContainer);

    }

    // 初始化聊天界面的数据源
    private List<Map<String, Object>> getChatListData() {
        List<Map<String, Object>> list;
        // icon图标
        int[] images = {ResourceTable.Media_head, ResourceTable.Media_head,
                ResourceTable.Media_head, ResourceTable.Media_head,
                ResourceTable.Media_head, ResourceTable.Media_head,
                ResourceTable.Media_head, ResourceTable.Media_head,
                ResourceTable.Media_head, ResourceTable.Media_head};

        String[] names = {"曹操", "刘备", "关羽", "诸葛亮", "小乔", "貂蝉", "吕布", "赵云", "黄盖", "周瑜"};
        String[] lastmsg = {"一代枭雄", "卖草鞋", "财神", "卧龙先生", "周瑜媳妇", "四大镁铝", "天下无双", "常胜将军", "愿意挨打", "愿意打人"};
        String[] lasttime = {"20:30", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02", "2021-10-02"};

        list = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
            map.put("head", images[i]);
            map.put("name", names[i]);
            map.put("lastmsg", lastmsg[i]);
            map.put("lasttime", lasttime[i]);
            list.add(map);
        }


        return list;
    }


    //定义选择器
    private void iniCalendarPicker() {
        Calendar cal = Calendar.getInstance();
        outdate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-1";

        tf_add_otc.setDisplayedData(new String[]{"OTC(非处方药)-红", "OTC(非处方药)-绿", "(留空)", "RX(处方药)"});
        tf_add_otc.setValue(2);


    }


    // 初始化药品主页的ListContainer
    private void initHomepageListContainer() {
        //1.获取xml布局中的ListContainer组件
        Text nonelist = (Text) findComponentById(ResourceTable.Id_nonelist);
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_hp_list_data);
        listContainer.setLongClickable(false);
        // 2.实例化数据源
        List<Map<String, Object>> list = queryData();
        if (list == null) {
            nonelist.setVisibility(Component.VISIBLE);
            listContainer.setVisibility(Component.HIDE);
            dataFav = 0;
            dataHas = 0;
            util.PreferenceUtils.putInt(this, "date_out", 0);
            util.PreferenceUtils.putInt(this, "date_near", 0);
            util.PreferenceUtils.putInt(this, "date_ok", 0);
        } else {
            switch (style) {
                case 1:
                    listContainer.setOrientation(Component.VERTICAL);
                    break;
                case 0:
                default:
                    listContainer.setOrientation(Component.HORIZONTAL);
                    break;
            }
            System.out.println("样式" + style);
            nonelist.setVisibility(Component.HIDE);
            listContainer.setVisibility(Component.VISIBLE);
            // 3.初始化Provider对象
            HomePageListItemProvider listItemProvider = new HomePageListItemProvider(list, this, style);
            // 4.适配要展示的内容数据
            listContainer.setItemProvider(listItemProvider);
            // 5.设置每个Item的点击事件
            listContainer.setItemClickedListener((container, component, position, id) -> {

                Map<String, Object> item = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                Map<String, Object> res = list.get(position);

                //单击打开详情弹窗
                util.PreferenceUtils.putString(getContext(), "editok", "none");
                util.PreferenceUtils.putString(this, "mglocalkey", res.getOrDefault("keyid", null).toString());
                Intent intentDetail = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                        .withBundleName(getBundleName())    // 包名
                        .withAbilityName("com.daqin.medicinegod.DetailAbility")
                        // Ability页面的名称，在本地可以缺省前面的路径
                        .build();    // 构建代码
                intentDetail.setOperation(operation);    // 将operation存入到intent中
                startAbility(intentDetail);    // 实现Ability跳转


            });
        }
    }

    //药品数据源
    private List<Map<String, Object>> queryData() {
        List<Map<String, Object>> list = new ArrayList<>();

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_MEDICINE_KEYID, "M-");
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            dataFav = 0;
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));
                map.put("keyid", keyid);
                map.put("img", image);
                map.put("name", name);
                map.put("description", description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company", company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("love", love);
                map.put("share", share);
                map.put("delect", delect);

                if (love.equals("yes")) {
                    dataFav++;
                }
                list.add(map);
                System.out.println("query: Id :" + " keyid:" + keyid + " name:" + name + " image:" + Arrays.toString(image) + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            dataHas = list.size();
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }

    //药品搜索返回数据【筛选搜索】
    public static List<Map<String, Object>> queryScreenData(int method, String field, String value1, String value2) {
        List<Map<String, Object>> list = new ArrayList<>();

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        switch (method) {
            case 11:
                predicates.lessThan(field, util.getDateFromString(value1));
                break;
            case 12:
                predicates.between(field, util.getDateFromString(value1), util.getDateFromString(value2));
                break;
            case 13:
                predicates.greaterThan(field, util.getDateFromString(value1));
                break;
            case 21:
                predicates.lessThan(field, value1);
                break;
            case 22:
                predicates.between(field, value1, value2);
                break;
            case 23:
                predicates.greaterThan(field, value1);
                break;
            case 31:
                predicates.contains(field, value1);
                break;
            case 32:
                predicates.equalTo(field, value1);
                break;
            case 33:
                predicates.equalTo(field, value1);
                break;
            case 34:
                predicates.equalTo(field, value1);
                break;
            case 35:
                predicates.equalTo(field, value1);
                break;


        }

        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));
                map.put("keyid", keyid);
                map.put("img", image);
                map.put("name", name);
                map.put("description", description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company", company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("love", love);
                map.put("share", share);
                map.put("delect", delect);

                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }

    //药品搜索返回数据【指定搜索】
    public static List<Map<String, Object>> queryAssignData(String field, String value) {
        List<Map<String, Object>> list = new ArrayList<>();

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.contains(field, value);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));

                map.put("keyid", keyid);
                map.put("img", image);
                map.put("name", name);
                map.put("description", description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company", company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("love", love);
                map.put("share", share);
                map.put("delect", delect);

                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }

    //药品搜索界面总数据源
    public static List<Map<String, Object>> querySearchData(String lowKey, String highKey) {
        List<Map<String, Object>> list = new ArrayList<>();

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_MEDICINE_KEYID, "M-");
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));

                map.put("keyid", keyid);
                map.put("img", image);
                map.put("name", name);
                map.put("description", description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company", company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("love", love);
                map.put("share", share);
                map.put("delect", delect);

                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }


    @Override
    public void onActive() {
        super.onActive();
        System.out.println("输出流active");
        String editdone = util.PreferenceUtils.getString(getContext(), "editok");
        if (editdone.equals("ok")) {
            initHomepageListContainer();
            util.PreferenceUtils.putString(getContext(), "editok", "none");
        }
        String rgdone = util.PreferenceUtils.getString(getContext(), "rlok");
        localPerson = util.PreferenceUtils.getString(getContext(), "localperson");
        //登录则获取数据
        int isLogin = util.PreferenceUtils.getInt(getContext(), "isLogin");
        if (isLogin != 0) {
            util.PreferenceUtils.putString(getContext(), "rlok", "none");
            personData = queryPerson(localPerson);
            if (personData != null && !personData.toString().equals("{}")) {
                try {
                    img_hp_head.setPixelMap(util.byte2PixelMap((byte[]) personData.get(DB_COLUMN_PERSON_HEAD)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                t_hp_title_usersname.setText((String) personData.get(DB_COLUMN_PERSON_SNAME));
            }
        }
        System.out.println("结果" + rgdone);
        if (rgdone.equals("ok")) {
            personData = queryPerson(localPerson);
            System.out.println(localPerson + personData);
            if (personData != null && !personData.toString().equals("{}")) {
                util.PreferenceUtils.putInt(getContext(), "isFirstStart", 1);
                util.PreferenceUtils.putInt(getContext(), "isLogin", 1);
                try {
                    img_hp_head.setPixelMap(util.byte2PixelMap((byte[]) personData.get(DB_COLUMN_PERSON_HEAD)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                util.PreferenceUtils.putString(getContext(), "rlok", "none");
                t_hp_title_usersname.setText((String) personData.get(DB_COLUMN_PERSON_SNAME));
            }
        }
        //刷新界面
        if (util.PreferenceUtils.getInt(getContext(), "editPerson") == 1) {
            personData = queryPerson(localPerson);
            PixelMap pixelMap = util.byte2PixelMap((byte[]) personData.get(DB_COLUMN_PERSON_HEAD));
            img_hp_head.setPixelMap(pixelMap);
            t_hp_title_usersname.setText((String) personData.get(DB_COLUMN_PERSON_SNAME));
            img_homehead.setPixelMap(pixelMap);
            t_me_sname.setText((String) personData.get(DB_COLUMN_PERSON_SNAME));
            t_me_lname.setText((String) personData.get(DB_COLUMN_PERSON_LNAME));
            String vip = (String) personData.get(DB_COLUMN_PERSON_VIP);
            if (vip.equals("0")) {
                t_me_vip.setText("免费会员");
            } else {
                t_me_vip.setText(" VIP ");
            }
            util.PreferenceUtils.putInt(getContext(), "editPerson", 0);
        }
        Map<String, Object> map = WebDataAbility.getData("select * from USERINFO where `lname` = '" + localPerson + "';",localPerson);

    }

    //插入数据
    public void updatePerson(String id, String lname, String sname, String pwd, byte[] head,
                             String friend, String phone, String mail,
                             String online, String has, String vip,
                             String vipyu) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, id);

        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_ID, id);
        valuesBucket.putString(DB_COLUMN_PERSON_LNAME, lname);
        valuesBucket.putString(DB_COLUMN_PERSON_SNAME, sname);
        valuesBucket.putString(DB_COLUMN_PERSON_PWD, pwd);
        valuesBucket.putByteArray(DB_COLUMN_PERSON_HEAD, head);
        valuesBucket.putString(DB_COLUMN_PERSON_FRIEND, friend);
        valuesBucket.putString(DB_COLUMN_PERSON_PHONE, phone);
        valuesBucket.putString(DB_COLUMN_PERSON_MAIL, mail);
        valuesBucket.putString(DB_COLUMN_PERSON_ONLINE, online);
        valuesBucket.putString(DB_COLUMN_PERSON_HAS, has);
        valuesBucket.putString(DB_COLUMN_PERSON_VIP, vip);
        valuesBucket.putString(DB_COLUMN_PERSON_VIPYU, vipyu);

        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onBackground() {
        super.onBackground();
        if (mBubbleNavigationLinearView.mEventHandler != null) {
            mBubbleNavigationLinearView.mEventHandler.removeAllEvent();
            mBubbleNavigationLinearView.mEventHandler = null;
        }
    }

    @Override
    public void onAbilityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("输出了: " + resultCode + ":" + requestCode + ":" + data);
        switch (requestCode) {
            case RESULTCODE_IMAGE_CHOOSE:
                if (data != null) {
                    //取得图片路径
                    String imgpath = data.getUriString();
//                    取真实地址
//                    pickiT.getPath(data.getUri());
                    System.out.println("gggggggg" + imgpath);
                    //定义数据能力帮助对象
                    DataAbilityHelper helper = DataAbilityHelper.creator(getContext());

                    //原组件是居中，这里给他选择填充
                    btn_add_img.setScaleMode(Image.ScaleMode.STRETCH);

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
                    btn_add_img.setPixelMap(pixelMap);

                    //readInputStream将inputStream转换成byte[]
                    imgbytes = util.readInputStream(inputStream);
                    System.out.println(Arrays.toString(imgbytes));

                }
                break;
            case RESULTCODE_IMAGE_CROP:
                if (data != null) {
                    if (data.getStringParam("cropedimage").equals("ok")) {
                        img = ImageSaver.getInstance().getByte();
                        btn_add_img.setPixelMap(util.byte2PixelMap(img));

                    }
                }
                break;
            case 300:
                if (data != null) {
                    if (data.getStringParam("changeok").equals("ok")) {
                        style = util.PreferenceUtils.getInt(getContext(), "style");
                        initHomepageListContainer();
                    }
                }
                break;
            default:
                btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
                break;
        }
        super.onAbilityResult(requestCode, resultCode, data);
    }

    //TODO：刷新个人界面数据
    public int queryMeData() {

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_MEDICINE_OUTDATE, "M-");
        int count = 0;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return 0;
            }
            resultSet.goToFirstRow();
            do {
                count++;
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));

                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel + "love" + love + "share:" + share);
            } while (resultSet.goToNextRow());
            dataHas = count;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
        return 0;
    }

    //刷新数据
    public void query() {
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
//        predicates.between(DB_COLUMN_MEDICINE_KEYID, lowKey, highKey);
        predicates.beginsWith(DB_COLUMN_MEDICINE_KEYID, "M-");
        int count = 0;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return;
            }
            resultSet.goToFirstRow();
            do {
                count++;
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel + "love" + love + "share:" + share);
            } while (resultSet.goToNextRow());
            dataHas = count;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
    }

    public static Map<String, Object> querySingleData(String idkey) {
        Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_MEDICINE_KEYID, idkey);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_ELABEL));
                String love = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_LOVE));
                String share = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_SHARE));
                int delect = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_MEDICINE_DELECT));
                map.put("keyid", keyid);
                map.put("img", image);
                map.put("name", name);
                map.put("description", description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company", company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("love", love);
                map.put("share", share);
                map.put("delect", delect);

                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            return map;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return null;
        }
    }

    public static boolean isPresentkeyId(String keyid) {

        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_MEDICINE_KEYID, keyid);
        boolean isPresent = true;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE),
                    columns_medicine, predicates);
            isPresent = resultSet != null && resultSet.getRowCount() != 0;
        } catch (DataAbilityRemoteException | IllegalStateException ignored) {
        }
        return isPresent;
    }

    //插入数据
    public void insert(String keyid, String name, byte[] image,
                       String description, String outdate, String otc,
                       String barcode, String usage, String company,
                       String yu, String elabel) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_MEDICINE_KEYID, keyid);
        valuesBucket.putString(DB_COLUMN_MEDICINE_NAME, name);
        valuesBucket.putByteArray(DB_COLUMN_MEDICINE_IMAGE, image);
        valuesBucket.putString(DB_COLUMN_MEDICINE_DESCRIPTION, description);
        valuesBucket.putLong(DB_COLUMN_MEDICINE_OUTDATE, util.getDateFromString(outdate));
        valuesBucket.putString(DB_COLUMN_MEDICINE_OTC, otc);
        valuesBucket.putString(DB_COLUMN_MEDICINE_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_MEDICINE_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_MEDICINE_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_MEDICINE_YU, yu);
        valuesBucket.putString(DB_COLUMN_MEDICINE_ELABEL, elabel);
        valuesBucket.putString(DB_COLUMN_MEDICINE_LOVE, "no");
        valuesBucket.putString(DB_COLUMN_MEDICINE_SHARE, "none");
        valuesBucket.putInteger(DB_COLUMN_MEDICINE_DELECT, 0);
        try {
            if (databaseHelper.insert(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE), valuesBucket) != -1) {
                HiLog.info(LABEL_LOG, "insert successful");
                //消息弹框
                new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .asConfirm("添加成功", "        您已成功添加了一个药品，单击药品列可查看更多操作。\n" +
                                        "        是否清空内容以便继续填写?",
                                "返回", "清空内容",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        clearAddTextfield();
                                    }
                                }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_blueconfirm)
                        .show(); // 最后一个参数绑定已有布局
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "insert: dataRemote exception|illegalStateException");
            ToastUtil.showToast(this, "添加失败，请重试  ");
        }
    }

    //更新
    public static void update(String keyid, String name, byte[] image,
                              String description, String outdate, String otc,
                              String barcode, String usage, String company,
                              String yu, String elabel) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_MEDICINE_KEYID, keyid);

        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_MEDICINE_NAME, name);
        valuesBucket.putByteArray(DB_COLUMN_MEDICINE_IMAGE, image);
        valuesBucket.putString(DB_COLUMN_MEDICINE_DESCRIPTION, description);
        valuesBucket.putLong(DB_COLUMN_MEDICINE_OUTDATE, util.getDateFromString(outdate));
        valuesBucket.putString(DB_COLUMN_MEDICINE_OTC, otc);
        valuesBucket.putString(DB_COLUMN_MEDICINE_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_MEDICINE_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_MEDICINE_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_MEDICINE_YU, yu);
        valuesBucket.putString(DB_COLUMN_MEDICINE_ELABEL, elabel);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    //删除
    public static void delete(String keyid) {
        DataAbilityPredicates predicates = new DataAbilityPredicates()
                .equalTo(DB_COLUMN_MEDICINE_KEYID, keyid);
        try {
            if (databaseHelper.delete(Uri.parse(BASE_URI_MEDICINE + DATA_PATH_MEDICINE), predicates) != -1) {
                HiLog.info(LABEL_LOG, "delete successful");
                ToastUtil.showToast(cont, "删除成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "delete: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "删除失败，请重试  ");
        }
    }

    public static void updateSname(String lname, String editSname) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_SNAME, editSname);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updatePwd(String lname, String editPwd) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_PWD, editPwd);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updatePhone(String lname, String editPhone) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_PHONE, editPhone);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updateHead(String lname, byte[] head) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putByteArray(DB_COLUMN_PERSON_HEAD, head);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updateLname(String lname, String editLname) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_LNAME, editLname);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updateMail(String lname, String editMail) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_MAIL, editMail);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static void updatePerson(String lname, String editLname) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, lname);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_PERSON_LNAME, editLname);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(cont, "修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "修改失败，请重试  ");
        }
    }

    public static Map<String, Object> queryPerson(String personid) {
        // 构造查询条件
        Map<String, Object> map = new HashMap<>();
        DataAbilityPredicates predicates = new DataAbilityPredicates();
//        predicates.between(DB_COLUMN_MEDICINE_KEYID, lowKey, highKey);
        predicates.equalTo(DB_COLUMN_PERSON_LNAME, personid);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI_PERSON + DATA_PATH_PERSON),
                    columns_person, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                System.out.println("query:resultSet is null or no result found");
                return null;
            } else {
                resultSet.goToFirstRow();
                map.put(DB_COLUMN_PERSON_ID, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_ID)));
                map.put(DB_COLUMN_PERSON_LNAME, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_LNAME)));
                map.put(DB_COLUMN_PERSON_HEAD, resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_HEAD)));
                map.put(DB_COLUMN_PERSON_SNAME, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_SNAME)));
                map.put(DB_COLUMN_PERSON_PWD, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_PWD)));
                map.put(DB_COLUMN_PERSON_PHONE, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_PHONE)));
                map.put(DB_COLUMN_PERSON_MAIL, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_MAIL)));
                map.put(DB_COLUMN_PERSON_FRIEND, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_FRIEND)));
                map.put(DB_COLUMN_PERSON_RGTIME, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_RGTIME)));
                map.put(DB_COLUMN_PERSON_ONLINE, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_ONLINE)));
                map.put(DB_COLUMN_PERSON_HAS, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_HAS)));
                map.put(DB_COLUMN_PERSON_VIP, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_VIP)));
                map.put(DB_COLUMN_PERSON_VIPYU, resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_PERSON_VIPYU)));
                System.out.println(map);
                return map;

            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            System.out.println("query: dataRemote exception | illegalStateException");
        }
        return map;
    }

    @Override
    protected void onStop() {
        if (!isUpdatingConfigurations()) {
            pickiT.deleteTemporaryFile(this);
        }
        super.onStop();
    }

    @Override
    protected void onBackPressed() {
        pickiT.deleteTemporaryFile(this);
        super.onBackPressed();
    }
}


