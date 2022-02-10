package com.daqin.medicinegod.slice;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 * Changes by daqin,at 2019-2022
 */

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.ChatListItemProvider;
import com.daqin.medicinegod.provider.HomePageListItemProvider;
import com.daqin.medicinegod.utils.imageControler.ImageSaver;
import com.daqin.medicinegod.provider.ScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.*;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
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
import java.util.*;

//修复post的bug
public class MainAbilitySlice extends AbilitySlice {
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;   //自定义的一个权限请求识别码，用于处理权限回调
    private static DataAbilityHelper databaseHelper;
    private static Context cont;

    private BubbleNavigationLinearView mBubbleNavigationLinearView;
    //    private List<String> labelList = new ArrayList<>();
    private static final int RESULTCODE_IMAGE_CHOOSE = 100;
    private static final int RESULTCODE_IMAGE_CROP = 101;
    private static int newUsage_utils_1 = 0, newUsage_utils_3 = 0, elabelCount = 0;
    private int dataHas = 0,dataFav=0,dataShare=0;

    List<String> eLABEL = new ArrayList<>();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "MainAbilitySlice");
    private static final String BASE_URI = "dataability:///com.daqin.medicinegod.MedicineDataAbility";
    private static final String DATA_PATH = "/medicine";
    private static final String DB_COLUMN_KEYID = "KEYID";
    private static final String DB_COLUMN_NAME = "NAME";
    private static final String DB_COLUMN_IMAGE = "IMAGE";
    private static final String DB_COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String DB_COLUMN_OUTDATE = "OUTDATE";
    private static final String DB_COLUMN_OTC = "OTC";
    private static final String DB_COLUMN_BARCODE = "BARCODE";
    private static final String DB_COLUMN_USAGE = "USAGE";
    private static final String DB_COLUMN_COMPANY = "COMPANY";
    private static final String DB_COLUMN_YU = "YU";
    private static final String DB_COLUMN_ELABEL = "ELABEL";
    /**
     * @param img 图片最终数据
     * @param imgbytes 图片待裁剪数据
     */
    byte[] img = null;
    private static byte[] imgbytes = null;

    Image img_thing_head;
    Image btn_add_img;
    Button btn_add_clear_context;
    Text btn_add_otc_question;
    Text btn_add_newUsage_utils_1;
    Text btn_add_newUsage_utils_2;
    Text btn_add_newUsage_utils_3;
    Text btn_add_yu_title;
    Text btn_things_seach;

    ScrollView view_add;
    TextField tf_add_name;
    TextField tf_add_desp;
    Picker tf_add_outdate_year;
    Picker tf_add_outdate_month;
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
    ImageSaver imageSaver;


    Text[] add_elabelview;
    TextField[] add_tf_list;


    public void iniView() {
        img_thing_head = (Image) findComponentById(ResourceTable.Id_things_image_head);
        img_thing_head.setCornerRadius(100);
        btn_add_clear_context = (Button) findComponentById(ResourceTable.Id_add_clear);

        btn_add_otc_question = (Text) findComponentById(ResourceTable.Id_add_newOtc_question);
        btn_add_img = (Image) findComponentById(ResourceTable.Id_add_newImg);
        btn_add_newUsage_utils_1 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_1);
        btn_add_newUsage_utils_2 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_2);
        btn_add_newUsage_utils_3 = (Text) findComponentById(ResourceTable.Id_add_newUsage_utils_3);
        btn_add_yu_title = (Text) findComponentById(ResourceTable.Id_add_newYu_title);
        btn_things_seach = (Text) findComponentById(ResourceTable.Id_things_textField_search);
        view_add = (ScrollView) findComponentById(ResourceTable.Id_add_scrollview);
        tf_add_name = (TextField) findComponentById(ResourceTable.Id_add_newName);
        tf_add_desp = (TextField) findComponentById(ResourceTable.Id_add_newDescription);
        tf_add_outdate_year = (Picker) findComponentById(ResourceTable.Id_add_newOutdate_year);
        tf_add_outdate_month = (Picker) findComponentById(ResourceTable.Id_add_newOutdate_month);
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


        add_elabelview = new Text[]{t_add_elabel1, t_add_elabel2, t_add_elabel3, t_add_elabel4, t_add_elabel5};
        add_tf_list = new TextField[]{tf_add_name, tf_add_desp, tf_add_barcode, tf_add_usage_total, tf_add_usage_time, tf_add_usage_day, tf_add_company, tf_add_yu};
    }

    public void iniClicklistener() {
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
        t_add_imgclaer.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                imgbytes = null;
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
                btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
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

                        Intent intent = new Intent();
                        Operation opt = new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                        intent.setOperation(opt);
                        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                        intent.setType("image/*");
                        intent.setBundle("com.huawei.photos");
                        startAbilityForResult(intent, RESULTCODE_IMAGE_CHOOSE);
                    } else {
                        // 显示应用需要权限的理由，提示用户进入设置授权
                        ToastUtil.showToast(getContext(), "请进入系统设置进行授权  ");
                    }
                } else {
                    // 权限已被授予
                    //加载显示系统相册中的照片
                    Intent intent = new Intent();
                    Operation opt = new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                    intent.setOperation(opt);
                    intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                    intent.setType("image/*");
                    intent.setBundle("com.huawei.photos");
                    startAbilityForResult(intent, RESULTCODE_IMAGE_CHOOSE);
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
                                    eLABEL.remove(t_add_elabel1.getText());
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
                                    eLABEL.remove(t_add_elabel2.getText());
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
                                    eLABEL.remove(t_add_elabel3.getText());
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
                                    eLABEL.remove(t_add_elabel4.getText());
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
                                    eLABEL.remove(t_add_elabel5.getText());
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
            if (elabelCount > 5) {
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
                        || tf_add_elabelBox.length() >= 5
                        || tf_add_elabelBox.getText().equals(" ")
                        || tf_add_elabelBox.getText().equals("  ")) {
                    //输入框内条件标签不满足则提示
                    new XPopup.Builder(getContext())
                            //.setPopupCallback(new XPopupListener())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .asConfirm("格式受限", "您在一个标签内只能添加1到4个中文字符",
                                    " ", "好", null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                            .show(); // 最后一个参数绑定已有布局
                } else if (tf_add_elabelBox.length() > 0 && tf_add_elabelBox.length() < 5) {
                    for (Text text : add_elabelview) {
                        if (text.getText() == null || text.getText().equals("测试标签")) {
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
            String key = util.getRandomKeyId();
            System.out.println("生成key" + key);
            //检测已经选择图片
            if (imgbytes == null) {
                view_add.fluentScrollTo(0, btn_add_img.getTop() - 100);
                ToastUtil.showToast(this, "图片不能为空");
            } else if (eLABEL == null || elabelCount == 0) {
                view_add.fluentScrollTo(0, btn_add_ok.getTop());
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
                    String usa1, usa2, usa3, usageall;
                    usa1 = tf_add_usage_total.getText().trim();
                    usa2 = tf_add_usage_time.getText().trim();
                    usa3 = tf_add_usage_day.getText().trim();
                    usageall = usa1 + "-" + btn_add_newUsage_utils_1.getText() + "-" + usa2 + "-" + btn_add_newUsage_utils_2.getText() + "-" + usa3 + "-" + btn_add_newUsage_utils_3.getText();
                    insert(key,
                            tf_add_name.getText(),
                            img == null ? imgbytes : img,
                            tf_add_desp.getText(),
                            (tf_add_outdate_year.getDisplayedData())[tf_add_outdate_year.getValue()].replace("年", "")
                                    + "-"
                                    + (tf_add_outdate_month.getDisplayedData())[tf_add_outdate_month.getValue()].replace("月", "")
                                    + "-1",
                            tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("OTC(非处方药)-绿") ? "OTC-G" : tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("OTC(非处方药)-红") ? "OTC-R" : tf_add_otc.getDisplayedData()[tf_add_otc.getValue()].equals("RX(处方药)") ? "Rx" : "none",
                            tf_add_barcode.getText(),
                            usageall,
                            tf_add_company.getText(),
                            tf_add_yu.getText().trim(),
                            eLABEL.toString());

                }

            }
        });


    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
//        mflowLayout = (FlowLayout) findComponentById(ResourceTable.Id_flow_layout);
//        labelList.clear();
        databaseHelper = DataAbilityHelper.creator(this);
        cont = getContext();
        imageSaver = new ImageSaver();
        imageSaver.setInstance();
        query();
        util.PreferenceUtils.putString(getContext(), "editok", "none");
        intPageStart();
        initHomepageListContainer();
        if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
            // 应用未被授予权限
            if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                requestPermissionsFromUser(
                        new String[]{"ohos.permission.READ_MEDIA"}, MY_PERMISSIONS_REQUEST_READ_MEDIA);
            }
        }
//        Image img_homehead = (Image) findComponentById(ResourceTable.Id_home_image_head);
//        img_homehead.setCornerRadius(150);
//        initCommunityListContainer();
//        initChatListContainer();
        iniView();
        iniCalendarPicker();
        iniClicklistener();

    }

    //对话框的监听事件
    class dialogListener extends SimpleCallback {
        @Override
        public void onCreated(BasePopupView pv) {

        }

        @Override
        public void onShow(BasePopupView popupView) {


        }

        @Override
        public void onDismiss(BasePopupView popupView) {


        }

        @Override
        public void beforeDismiss(BasePopupView popupView) {


        }

        // 如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
        @Override
        public boolean onBackPressed(BasePopupView popupView) {
//            ToastUtil.showToast(getContext(), "onBackPressed返回true，拦截了返回按键，按返回键XPopup不会关闭了");
            return true;
        }

        @Override
        public void onKeyBoardStateChanged(BasePopupView popupView, int height) {
            super.onKeyBoardStateChanged(popupView, height);
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
        ScreenSlidePagerProvider pagerAdapter = new ScreenSlidePagerProvider(this, fragList);

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
                    Image img_homehead = (Image) findComponentById(ResourceTable.Id_home_image_head);
                    img_homehead.setCornerRadius(200);
                    Text me_has = (Text) findComponentById(ResourceTable.Id_me_has);
                    me_has.setText(String.valueOf(dataHas));
                    Text me_fav = (Text) findComponentById(ResourceTable.Id_me_fav);
                    me_fav.setText(String.valueOf(dataFav));
                    Text me_share = (Text) findComponentById(ResourceTable.Id_me_share);
                    me_share.setText(String.valueOf(dataShare));
                    break;
                default:
                    break;
            }
        });
    }
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
        List<String> yearList = new ArrayList<>();
        int year_now = cal.get(Calendar.YEAR);
        for (int i = year_now; i <= year_now + 9; i++) {
            yearList.add(i + "年");
        }

        tf_add_outdate_year.setDisplayedData(yearList.toArray(new String[]{}));
        tf_add_outdate_year.setValue(0);

        tf_add_outdate_month.setDisplayedData(new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"});
        tf_add_outdate_month.setHeight(util.getWindowWidthPx(MainAbilitySlice.this) / 4);
        tf_add_outdate_month.setValue(0);

        tf_add_otc.setDisplayedData(new String[]{"OTC(非处方药)-红", "OTC(非处方药)-绿", "(留空)", "RX(处方药)"});
        tf_add_otc.setValue(2);


    }


    // 初始化药品主页的ListContainer
    private void initHomepageListContainer() {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_things_list);

        // 2.实例化数据源
        List<Map<String, Object>> list = queryData();
        // 3.初始化Provider对象
        HomePageListItemProvider listItemProvider = new HomePageListItemProvider(list, this);
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

    //药品数据源
    private List<Map<String, Object>> queryData() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_KEYID,"KEY");
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return new ArrayList<>();
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
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
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
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
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
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
                map.put("image", ResourceTable.Media_test);
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
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.contains(field, value);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
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
                map.put("image", ResourceTable.Media_test);
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
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_KEYID,"KEY");
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
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
                map.put("image", ResourceTable.Media_test);
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
            default:
                btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
                break;
        }
        super.onAbilityResult(requestCode, resultCode, data);
    }

    //TODO：刷新个人界面数据
    public int queryMeData() {
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.beginsWith(DB_COLUMN_OUTDATE,"KEY");
        int count = 0;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return 0;
            }
            resultSet.goToFirstRow();
            do {
                count++;
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            dataHas = count;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
        return 0;
    }
    //刷新数据
    public void query() {
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
//        predicates.between(DB_COLUMN_KEYID, lowKey, highKey);
        predicates.beginsWith(DB_COLUMN_KEYID,"KEY");
        int count = 0;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return;
            }
            resultSet.goToFirstRow();
            do {
                count++;
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                HiLog.info(LABEL_LOG, "query: Id :" + " keyid:" + keyid + " name:" + name + " imagepath:" + image + " description:" + description + " outdate:" + outdate
                        + " otc:" + otc + " barcode:" + barcode + " :" + usage + " company:" + company + " yu:" + yu + " elabel:" + elabel);
            } while (resultSet.goToNextRow());
            dataHas = count;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
    }

    public static Map<String, Object> querySingleData(String idkey) {
        Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_KEYID, idkey);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return null;
            }
            resultSet.goToFirstRow();
            do {
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                byte[] image = resultSet.getBlob(resultSet.getColumnIndexForName(DB_COLUMN_IMAGE));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
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
                map.put("image", ResourceTable.Media_test);
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
        String[] columns = new String[]{
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGE,
                DB_COLUMN_DESCRIPTION,
                DB_COLUMN_OUTDATE,
                DB_COLUMN_OTC,
                DB_COLUMN_BARCODE,
                DB_COLUMN_USAGE,
                DB_COLUMN_COMPANY,
                DB_COLUMN_YU,
                DB_COLUMN_ELABEL
        };
        // 构造查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_KEYID, keyid);
        boolean isPresent = true;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
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
        valuesBucket.putString(DB_COLUMN_KEYID, keyid);
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putByteArray(DB_COLUMN_IMAGE, image);
        valuesBucket.putString(DB_COLUMN_DESCRIPTION, description);
        valuesBucket.putLong(DB_COLUMN_OUTDATE, util.getDateFromString(outdate));
        valuesBucket.putString(DB_COLUMN_OTC, otc);
        valuesBucket.putString(DB_COLUMN_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_YU, yu);
        valuesBucket.putString(DB_COLUMN_ELABEL, elabel);

        try {
            if (databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket) != -1) {
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
        predicates.equalTo(DB_COLUMN_KEYID, keyid);

        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putByteArray(DB_COLUMN_IMAGE, image);
        valuesBucket.putString(DB_COLUMN_DESCRIPTION, description);
        valuesBucket.putLong(DB_COLUMN_OUTDATE, util.getDateFromString(outdate));
        valuesBucket.putString(DB_COLUMN_OTC, otc);
        valuesBucket.putString(DB_COLUMN_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_YU, yu);
        valuesBucket.putString(DB_COLUMN_ELABEL, elabel);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates) != -1) {
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
                .equalTo(DB_COLUMN_KEYID, keyid);
        try {
            if (databaseHelper.delete(Uri.parse(BASE_URI + DATA_PATH), predicates) != -1) {
                HiLog.info(LABEL_LOG, "delete successful");
                ToastUtil.showToast(cont, "删除成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "delete: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont, "删除失败，请重试  ");
        }
    }

    public static class XPopupListener extends SimpleCallback {
        @Override
        public void onCreated(BasePopupView pv) {
            HiLog.info(LABEL_LOG, "onCreater");
        }

        @Override
        public void onShow(BasePopupView popupView) {
            HiLog.info(LABEL_LOG, "onShow");
        }

        @Override
        public void onDismiss(BasePopupView popupView) {
            HiLog.info(LABEL_LOG, "onDismiss");
        }

        @Override
        public void beforeDismiss(BasePopupView popupView) {
            HiLog.info(LABEL_LOG, "beforeDismiss");
        }

        // 如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
        @Override
        public boolean onBackPressed(BasePopupView popupView) {
//            ToastUtil.showToast(getContext(), "onBackPressed返回true，拦截了返回按键，按返回键XPopup不会关闭了");
            return true;
        }

        @Override
        public void onKeyBoardStateChanged(BasePopupView popupView, int height) {
            super.onKeyBoardStateChanged(popupView, height);
//            loge("tag", "onKeyBoardStateChanged height: " + height);
        }
    }

}


