package com.daqin.medicinegod.slice;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 * Changes by daqin,at 2019-2022
 */

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.ChatListItemProvider;
import com.daqin.medicinegod.provider.HomePageListItemProvider;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


//TODO：图片处理、otc为空的判断、时间处理的判断
public class MainAbilitySlice extends AbilitySlice {
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;   //自定义的一个权限请求识别码，用于处理权限回调
    private static DataAbilityHelper databaseHelper;
    private static Context cont;

    private BubbleNavigationLinearView mBubbleNavigationLinearView;
//    private List<String> labelList = new ArrayList<>();
    private static final int RESULTCODE_IMAGE_STARTCROP = 100;
    private static final int RESULTCODE_IMAGE_CROPED = 101;
    private static int newUsage_utils_1 = 0,newUsage_utils_3 = 0,elabelCount = 0;
    /**
     * @param  lowKey 第一条key，查询所用
     * @param  highKey 最后一条key，查询所用
     */
    private static String lowKey ;
    private static String highKey ;
    private String imgpath = null;
    private String eLABEL = "";
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "MainAbilitySlice");
    private static final String BASE_URI = "dataability:///com.daqin.medicinegod.PersonDataAbility";
    private static final String DATA_PATH = "/mg";
    private static final String DB_COLUMN_KEYID = "KEYID";
    private static final String DB_COLUMN_NAME = "NAME";
    private static final String DB_COLUMN_IMAGEPATH = "IMAGEPATH";
    private static final String DB_COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String DB_COLUMN_OUTDATE = "OUTDATE";
    private static final String DB_COLUMN_OTC = "OTC";
    private static final String DB_COLUMN_BARCODE = "BARCODE";
    private static final String DB_COLUMN_USAGE = "USAGE";
    private static final String DB_COLUMN_COMPANY = "COMPANY";
    private static final String DB_COLUMN_YU = "YU";
    private static final String DB_COLUMN_ELABEL = "ELABEL";


    Image img_thing_head;
    Image btn_add_img;
    Button btn_add_clear_context;
    Text btn_add_otc_question;
    Text btn_add_newUsage_utils_1;
    Text btn_add_newUsage_utils_2;
    Text btn_add_newUsage_utils_3;
    Text btn_add_yu_title;
    Text btn_things_seach;

    ScrollView view_add ;
    TextField tf_add_name ;
    TextField tf_add_desp ;
    Picker tf_add_outdate_year ;
    Picker tf_add_outdate_month ;
    Picker tf_add_otc ;
    TextField tf_add_barcode ;
    TextField tf_add_usage_total ;
    TextField tf_add_usage_time ;
    TextField tf_add_usage_day ;
    TextField tf_add_company ;
    TextField tf_add_yu ;
    Button btn_add_ok ;
    Button btn_addNewLabel ;
    TextField tf_add_elabelBox ;
    Text t_add_elabel_title ;
    Text t_add_elabel1 ;
    Text t_add_elabel2 ;
    Text t_add_elabel3 ;
    Text t_add_elabel4 ;
    Text t_add_elabel5 ;



    Text[] add_elabelview;
    TextField[] add_tf_list ;


    public void iniView(){
        img_thing_head = (Image) findComponentById(ResourceTable.Id_things_image_head);
        img_thing_head.setCornerRadius(100);
        btn_add_clear_context = (Button)findComponentById(ResourceTable.Id_add_clear);
        btn_add_otc_question = (Text)findComponentById(ResourceTable.Id_add_newOtc_question);
        btn_add_img = (Image)findComponentById(ResourceTable.Id_add_newImg);
        btn_add_newUsage_utils_1 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_1);
        btn_add_newUsage_utils_2 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_2);
        btn_add_newUsage_utils_3 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_3);
        btn_add_yu_title = (Text)findComponentById(ResourceTable.Id_add_newYu_title);
        btn_things_seach = (Text)findComponentById(ResourceTable.Id_things_textField_search);
        view_add = (ScrollView)findComponentById(ResourceTable.Id_add_scrollview);
        tf_add_name = (TextField)findComponentById(ResourceTable.Id_add_newName);
        tf_add_desp = (TextField)findComponentById(ResourceTable.Id_add_newDescription);
        tf_add_outdate_year = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_year);
        tf_add_outdate_month = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_month);
        tf_add_otc = (Picker)findComponentById(ResourceTable.Id_add_newOtc);
        tf_add_barcode = (TextField)findComponentById(ResourceTable.Id_add_newbarCode);
        tf_add_usage_total = (TextField)findComponentById(ResourceTable.Id_add_newUsage_1);
        tf_add_usage_time = (TextField)findComponentById(ResourceTable.Id_add_newUsage_2);
        tf_add_usage_day = (TextField)findComponentById(ResourceTable.Id_add_newUsage_3);
        tf_add_company = (TextField)findComponentById(ResourceTable.Id_add_newCompany);
        tf_add_yu = (TextField)findComponentById(ResourceTable.Id_add_newYu);
        btn_add_ok = (Button)findComponentById(ResourceTable.Id_add_addOk);
        btn_addNewLabel = (Button)findComponentById(ResourceTable.Id_add_newLabel_addButton);
        tf_add_elabelBox = (TextField)findComponentById(ResourceTable.Id_add_newLabel_addTextFiled);
        t_add_elabel_title = (Text)findComponentById(ResourceTable.Id_add_newLabel_title);
        t_add_elabel1 = (Text)findComponentById(ResourceTable.Id_add_addNewlabel_label1);
        t_add_elabel2 = (Text)findComponentById(ResourceTable.Id_add_addNewlabel_label2);
        t_add_elabel3 = (Text)findComponentById(ResourceTable.Id_add_addNewlabel_label3);
        t_add_elabel4 = (Text)findComponentById(ResourceTable.Id_add_addNewlabel_label4);
        t_add_elabel5 = (Text)findComponentById(ResourceTable.Id_add_addNewlabel_label5);

        add_elabelview = new Text[]{t_add_elabel1,t_add_elabel2,t_add_elabel3,t_add_elabel4,t_add_elabel5};
        add_tf_list = new TextField[]{tf_add_name,tf_add_desp,tf_add_barcode,tf_add_usage_total,tf_add_usage_time,tf_add_usage_day,tf_add_company,tf_add_yu};
    }
    public void iniClicklistener(){
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
        //添加图片的图片按钮
        btn_add_img.setClickedListener(new Component.ClickedListener(){
            @Override
            public void onClick(Component component) {
                if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
                    // 应用未被授予权限
                    if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                        // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                        requestPermissionsFromUser(
                                new String[] { "ohos.permission.READ_MEDIA" } , MY_PERMISSIONS_REQUEST_READ_MEDIA);

                        Intent intent = new Intent();
                        Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                        intent.setOperation(opt);
                        intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                        intent.setType("image/*");
//                        intent.setBundle("com.huawei.photos");
                        startAbilityForResult(intent,RESULTCODE_IMAGE_STARTCROP);
                    } else {
                        // 显示应用需要权限的理由，提示用户进入设置授权
                        ToastUtil.showToast(getContext(),"请进入系统设置进行授权");
                    }
                } else {
                    // 权限已被授予
                    //加载显示系统相册中的照片
                    Intent intent = new Intent();
                    Operation opt=new Intent.OperationBuilder().withAction("android.intent.action.GET_CONTENT").build();
                    intent.setOperation(opt);
                    intent.addFlags(Intent.FLAG_NOT_OHOS_COMPONENT);
                    intent.setType("image/*");
                    intent.setBundle("com.huawei.photos");
                    startAbilityForResult(intent,RESULTCODE_IMAGE_STARTCROP);
                }
            }
        });
        //用法用量单位变换
        btn_add_newUsage_utils_1.setClickedListener(component->{
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1){
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
        btn_add_newUsage_utils_3.setClickedListener(component->{
            newUsage_utils_3 += 1;
            switch (newUsage_utils_3){
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
                                    t_add_elabel1.setText("测试标签");
                                    t_add_elabel1.setVisibility(Component.HIDE);
                                    elabelCount -= 1;
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
                                    t_add_elabel2.setText("测试标签");
                                    t_add_elabel2.setVisibility(Component.HIDE);
                                    elabelCount -= 1;
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
                                    t_add_elabel3.setText("测试标签");
                                    t_add_elabel3.setVisibility(Component.HIDE);
                                    elabelCount -= 1;
                                    t_add_elabel_title.setText("添加药效标签("+elabelCount+"/5)");

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
                                    t_add_elabel4.setText("测试标签");
                                    t_add_elabel4.setVisibility(Component.HIDE);
                                    elabelCount += 1;
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
                                    t_add_elabel5.setText("测试标签");
                                    t_add_elabel5.setVisibility(Component.HIDE);
                                    elabelCount += 1;
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
                                " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
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
                                    " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                            .show(); // 最后一个参数绑定已有布局
                } else if (tf_add_elabelBox.length() > 0 && tf_add_elabelBox.length() < 5) {
                    for (Text text : add_elabelview) {
                        if (text.getText() == null || text.getText().equals("测试标签")) {
                            elabelCount += 1;
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
        final int[] a = {0};
        btn_add_ok.setClickedListener(component -> {
            //检测已经选择图片
            a[0] +=1;
            insert("QWEY"+ a[0],
                    "NameNameNameName",
                    "imgpath",
                    "描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述描述",
                    "2022-3-1",
                    "OTC-R",
                    "6666666666",
                    //XX-包/克/片-XX-次-XX-时/天
                    "3-片-3-次-1-天",
                    "大马猴大马猴大马猴大马猴",
                    "2459",
                    "超级无敌@@嘟嘟嘟@@大大大@@DADAD");
            /*if (imgpath == null) {
                view.fluentScrollTo(0, addimg.getTop() - 100);
                ToastUtil.showToast(this, "图片不能为空");
            }else if(ELABEL == null){
                view.fluentScrollTo(0,btn_ok.getTop());
                ToastUtil.showToast(this, "图片不能为空");
            }else if((add_otc.getDisplayedData())[add_otc.getValue()].equals("(空)")){
                view.fluentScrollTo(0,add_otc.getTop());
                ToastUtil.showToast(this, "标识不能为空");
            }else {
                //计次，计算是否都填好了
                int count = 0;
                //检测里面是否为空
                for (TextField box : texttList) {
                    if (box.getText().equals("") || box.getText().equals(" ") || box.getText().equals(null)) {
                        //为空则跳转
                        box.setFocusable(Component.FOCUS_ADAPTABLE);
                        box.setTouchFocusable(true);
                        box.requestFocus();
                        //使view滑动到指定位置
                        ToastUtil.showToast(this, "有未填写空白，请填写后提交");
                        view.fluentScrollTo(0, box.getTop() - 100);
                        break;
                    } else {
                        box.clearFocus();
                        count += 1;
                        if (count >= 8) {
                            //添加数据
                            String otctext;
                            switch ((add_otc.getDisplayedData())[add_otc.getValue()]){
                                case "OTC(非处方药)-红":
                                    otctext = "OTC-R";
                                    break;
                                case "OTC(非处方药)-绿":
                                    otctext = "OTC-G";
                                    break;
                                case "RX(处方药)":
                                    otctext = "Rx";
                                    break;
                                default:
                                    otctext = "none";
                                    break;
                            }
                            *//*
             * insert(id
             *       name
             *       imgpath
             *       desp
             *       XXXX年-XX月
             *       OTC /  / Rx
             *       barcode
             *       XX-包/克/片-XX-次-XX-时/天
             *       company
             *       剩余：XX  包/克/片
             *       Elabel
             *       )
             * *//*
                            insert(idInsert,
                                    add_name.getText(),
                                    imgpath,
                                    add_desp.getText(),
                                    (add_outdate_year.getDisplayedData())[add_outdate_year.getValue()]
                                            + "-"
                                            + (add_outdate_month.getDisplayedData())[add_outdate_month.getValue()],
                                    otctext,
                                    add_barcode.getText(),
                                    add_usage_total.getText() + "-" + method1.getText() + "-" + add_usage_time.getText() + "-次-" + add_usage_day.getText() + method3.getText(),
                                    add_company.getText(),
                                    add_yu.getText(),
                                    ELABEL);
//                            query();//刷新
//TODO：2.解决img上屏的问题。
                        }
                    }
                }
            }*/
            /*if (DIALOG==null) {
                DIALOG = new XPopup.Builder(getContext())
                        .dismissOnBackPressed(false) // 点击返回键是否消失
                        .dismissOnTouchOutside(true) // 点击外部是否消失
                        .setPopupCallback(new dialogListener())
                        .asConfirm("提示", "未填写药品名", "取消", "确定",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        view.fluentScrollTo(0, 0);
                                    }
                                }, null, false);
            }
            DIALOG.show();
*/
        });


    }
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
//        mflowLayout = (FlowLayout) findComponentById(ResourceTable.Id_flow_layout);
//        labelList.clear();
        databaseHelper = DataAbilityHelper.creator(this);
        lowKey = util.PreferenceUtils.getString(this,"lowKey");
        highKey = util.PreferenceUtils.getString(this,"highKey");
        if (lowKey==null){
            lowKey = "";
        }if (highKey==null){
            highKey = "";
        }
        cont = getContext();
        util.PreferenceUtils.putString(getContext(),"editok","none");
        query();
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
        //TODO：elabel列表清理
        imgpath = "";
        eLABEL = "";
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
        view_add.fluentScrollTo(0,0);
        elabelCount = 0;
        t_add_elabel1.setText("测试标签") ;
        t_add_elabel1.setVisibility(Component.HIDE);
        t_add_elabel2.setText("测试标签") ;
        t_add_elabel2.setVisibility(Component.HIDE);
        t_add_elabel3.setText("测试标签") ;
        t_add_elabel3.setVisibility(Component.HIDE);
        t_add_elabel4.setText("测试标签") ;
        t_add_elabel4.setVisibility(Component.HIDE);
        t_add_elabel5.setText("测试标签") ;
        t_add_elabel5.setVisibility(Component.HIDE);
        t_add_elabel_title.setText("添加药效标签(0/5)");
        iniCalendarPicker();

        ToastUtil.showToast(getContext(),"已清空内容  ");
    }

    //导航栏和窗口初始化
    private void intPageStart(){
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
            switch (position){
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
    private void initChatListContainer(){
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_chat_chatlist);

        // 2.实例化数据源
        List<Map<String,Object>> list = getChatListData();
        // 3.初始化Provider对象
        ChatListItemProvider listItemProvider = new ChatListItemProvider(list,this);
        // 4.适配要展示的内容数据
        listContainer.setItemProvider(listItemProvider);
        // 5.设置每个Item的点击事件
        listContainer.setItemClickedListener((container, component, position, id) -> {
            Map<String,Object> item = (Map<String,Object>) listContainer.getItemProvider().getItem(position);
            new ToastDialog(this)
                    .setDuration(2000)
                    .setText("你点击了:" + item.get("name")+"，"+item.get("lastmsg"))
                    .setAlignment(LayoutAlignment.BOTTOM)
                    .show();
        });
//        setListContainerHeight(listContainer);

    }
    // 初始化聊天界面的数据源
    private List<Map<String,Object>> getChatListData(){
        List<Map<String,Object>> list;
        // icon图标
        int[] images = {ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head,
                ResourceTable.Media_head,ResourceTable.Media_head};

        String[] names={"曹操","刘备","关羽","诸葛亮","小乔","貂蝉","吕布","赵云","黄盖","周瑜"};
        String[] lastmsg={"一代枭雄","卖草鞋","财神","卧龙先生","周瑜媳妇","四大镁铝","天下无双","常胜将军","愿意挨打","愿意打人"};
        String[] lasttime={"20:30","2021-10-02","2021-10-02","2021-10-02","2021-10-02","2021-10-02","2021-10-02","2021-10-02","2021-10-02","2021-10-02"};

        list= new ArrayList<>();
        for(int i=0;i<images.length;i++){
            Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
            map.put("head", images[i]);
            map.put("name", names[i]);
            map.put("lastmsg",lastmsg[i]);
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
        for (int i = year_now; i <= year_now+9 ; i++) {
            yearList.add(i +"年");
        }

        tf_add_outdate_year.setDisplayedData(yearList.toArray(new String[]{}));
        tf_add_outdate_year.setValue(0);

        tf_add_outdate_month.setDisplayedData(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"});
        tf_add_outdate_month.setHeight(util.getWindowWidthPx(MainAbilitySlice.this)/4);
        tf_add_outdate_month.setValue(0);

        tf_add_otc.setDisplayedData(new String[]{"OTC(非处方药)-红","OTC(非处方药)-绿","(留空)","RX(处方药)"});
        tf_add_otc.setValue(2);


    }



    // 初始化药品主页的ListContainer
    private void initHomepageListContainer(){
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_things_list);

        // 2.实例化数据源
        List<Map<String,Object>> list = queryData();
        // 3.初始化Provider对象
        HomePageListItemProvider listItemProvider = new HomePageListItemProvider(list,this);
        // 4.适配要展示的内容数据
        listContainer.setItemProvider(listItemProvider);
        // 5.设置每个Item的点击事件
        listContainer.setItemClickedListener((container, component, position, id) -> {

            Map<String,Object> item = (Map<String,Object>) listContainer.getItemProvider().getItem(position);
            Map<String,Object> res = list.get(position);

            //单击打开详情弹窗
            util.PreferenceUtils.putString(getContext(), "editok", "none");
            util.PreferenceUtils.putString(this,"mglocalkey", res.getOrDefault("keyid",null).toString());
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
    private List<Map<String,Object>> queryData() {
        List<Map<String,Object>> list = new ArrayList<>();
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        System.out.println("开始"+lowKey+"-"+highKey);
        predicates.between(DB_COLUMN_KEYID, lowKey, highKey);
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
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                map.put("keyid",keyid);
                map.put("imgpath", imagepath);
                map.put("name", name);
                map.put("description",description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company",company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("image", ResourceTable.Media_test);
                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :"  +" keyid:"+keyid+ " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }
    //药品搜索返回数据【筛选搜索】
    public static List<Map<String,Object>> queryScreenData(String field,long value) {
        List<Map<String,Object>> list = new ArrayList<>();
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        predicates.lessThan(DB_COLUMN_OUTDATE,value);
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
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                map.put("keyid",keyid);
                map.put("imgpath", imagepath);
                map.put("name", name);
                map.put("description",description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company",company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("image", ResourceTable.Media_test);
                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :"  +" keyid:"+keyid+ " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }
    //药品搜索返回数据【指定搜索】
    public static List<Map<String,Object>> queryAssignData(String field,String value) {
        List<Map<String,Object>> list = new ArrayList<>();
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        predicates.contains(field,value);
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
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                String outdate = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                map.put("keyid",keyid);
                map.put("imgpath", imagepath);
                map.put("name", name);
                map.put("description",description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company",company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("image", ResourceTable.Media_test);
                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :"  +" keyid:"+keyid+ " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
            } while (resultSet.goToNextRow());
            return list;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return new ArrayList<>();
        }
    }
    //药品搜索界面总数据源
    public static List<Map<String,Object>> querySearchData(String lowKey,String highKey) {
        List<Map<String,Object>> list = new ArrayList<>();
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        predicates.between(DB_COLUMN_KEYID, lowKey, highKey);
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
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                String outdate = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                map.put("keyid",keyid);
                map.put("imgpath", imagepath);
                map.put("name", name);
                map.put("description",description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company",company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("image", ResourceTable.Media_test);
                list.add(map);
                HiLog.info(LABEL_LOG, "query: Id :"  +" keyid:"+keyid+ " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
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
        String editdone = util.PreferenceUtils.getString(getContext(),"editok");
        if (editdone.equals("ok")){
            initHomepageListContainer();
            util.PreferenceUtils.putString(getContext(),"editok","none");
        }
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onBackground() {
        super.onBackground();
        if (mBubbleNavigationLinearView.mEventHandler != null){
            mBubbleNavigationLinearView.mEventHandler.removeAllEvent();
            mBubbleNavigationLinearView.mEventHandler = null;
        }
    }

    @Override
    public void onAbilityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("返回"+resultCode+":"+requestCode+":"+data);
        switch (requestCode) {
            case RESULTCODE_IMAGE_STARTCROP:
                if (data != null) {
                    //取得图片路径
                    String paths = data.getUriString();
                    imgpath = data.getUriString();

                    System.out.println("gggggggg"+imgpath);
                    //定义数据能力帮助对象
                    DataAbilityHelper helper = DataAbilityHelper.creator(getContext());

                    //原组件是居中，这里给他选择填充
                    btn_add_img.setScaleMode(Image.ScaleMode.STRETCH);

                    //定义组件资源
                    ImageSource imageSource = null;
                    FileInputStream inputStream = null;

                    try {
                        inputStream = new FileInputStream(helper.openFile(Uri.parse(imgpath), "r"));
                    } catch (DataAbilityRemoteException|FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //定义文件
                    FileDescriptor file = null;
                    try {
                        file = helper.openFile(Uri.parse(paths), "r");
                    } catch (DataAbilityRemoteException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //创建文件对象
                    imageSource = ImageSource.create(file, null);
                    //创建位图
                    PixelMap pixelMap = imageSource.createPixelmap(null);
//                    addimg.setPixelMap(pixelMap);





                    //readInputStream将inputStream转换成byte[]
                    byte[] bytes = readInputStream(inputStream);
//                    System.out.println("ggggg"+ Arrays.toString(bytes));
//                    System.out.println("ggggg"+ util.pixelMap2BASE64(util.byte2PixelmapImage(bytes)));
                    //BASE64编码后上传并返回图片链接，后续使用在线图片链接

                    //TODO:修复跳转去裁剪图片
                    Intent newIntent = new Intent();
                    newIntent.setParam("startcropimage",bytes);
                    presentForResult(new ImageCropAbilitySlice(), newIntent,RESULTCODE_IMAGE_CROPED);

                }
                break;
            case RESULTCODE_IMAGE_CROPED:
                btn_add_img.setPixelMap(data.getSequenceableParam("cropedimage"));
                break;
            default:
                btn_add_img.setScaleMode(Image.ScaleMode.CENTER);
                btn_add_img.setPixelMap(ResourceTable.Media_add_imgadd);
                break;
        }
        super.onAbilityResult(requestCode, resultCode, data);
    }

    private byte[] readInputStream(InputStream inputStream) {

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



    //刷新数据
    public void query() {
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        predicates.between(DB_COLUMN_KEYID, lowKey, highKey);
        int count = 0;
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return;
            }
            resultSet.goToFirstRow();
            lowKey = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
            util.PreferenceUtils.putString(this,"lowKey",lowKey);
            do {
                count++;
                String keyid = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_KEYID));
                String name = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_NAME));
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                HiLog.info(LABEL_LOG, "query: Id :"  + " keyid:"+keyid+" name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
                if (count==resultSet.getRowCount()){
                    highKey = keyid;
                    util.PreferenceUtils.putString(this,"highKey",highKey);
                }
            } while (resultSet.goToNextRow());

        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
    }

    public static Map<String,Object> querySingleData(String idkey) {
        Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
        String[] columns = new String[] {
                DB_COLUMN_KEYID,
                DB_COLUMN_NAME,
                DB_COLUMN_IMAGEPATH,
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
        predicates.equalTo(DB_COLUMN_KEYID,idkey);
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
                String imagepath = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_IMAGEPATH));
                String description = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_DESCRIPTION));
                long outdate = resultSet.getLong(resultSet.getColumnIndexForName(DB_COLUMN_OUTDATE));
                String otc = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_OTC));
                String barcode = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_BARCODE));
                String usage = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_USAGE));
                String company = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_COMPANY));
                String yu = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_YU));
                String elabel = resultSet.getString(resultSet.getColumnIndexForName(DB_COLUMN_ELABEL));
                map.put("keyid",keyid);
                map.put("imgpath", imagepath);
                map.put("name", name);
                map.put("description",description);
                map.put("outdate", outdate);
                map.put("otc", otc);
                map.put("barcode", barcode);
                map.put("usage", usage);
                map.put("company",company);
                map.put("yu", yu);
                map.put("elabel", elabel);
                map.put("image", ResourceTable.Media_test);
                HiLog.info(LABEL_LOG, "query: Id :" +" keyid:"+keyid+ " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
            } while (resultSet.goToNextRow());
            return map;
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
            return null;
        }
    }


    //插入数据
    public void insert( String keyid,String name, String imagepath,
                        String description, String outdate, String otc,
                        String barcode, String usage, String company,
                        String yu ,String elabel) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_KEYID, keyid);
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putString(DB_COLUMN_IMAGEPATH, imagepath);
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
                if (lowKey.equals("")||lowKey.equals(null)){
                    lowKey = keyid;
                    util.PreferenceUtils.putString(this,"lowKey",lowKey);
                }
                highKey = keyid;
                util.PreferenceUtils.putString(this,"highKey",highKey);
                System.out.println("keykeykey"+lowKey+"ley"+highKey+"ffff");
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
            ToastUtil.showToast(this,"添加失败，请重试  ");
        }
    }
    //更新
    public static void update( String keyid, String name, String imagepath,
                        String description, String outdate, String otc,
                        String barcode, String usage, String company,
                        String yu ,String elabel) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_KEYID, keyid);

        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putString(DB_COLUMN_IMAGEPATH, imagepath);
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
                ToastUtil.showToast(cont,"修改成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont,"修改失败，请重试  ");
        }
    }
    //删除
    public static void delete(String keyid) {
        DataAbilityPredicates predicates = new DataAbilityPredicates()
                .equalTo(DB_COLUMN_KEYID, keyid);
        try {
            if (databaseHelper.delete(Uri.parse(BASE_URI + DATA_PATH), predicates) != -1) {
                HiLog.info(LABEL_LOG, "delete successful");
                ToastUtil.showToast(cont,"删除成功  ");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "delete: dataRemote exception | illegalStateException");
            ToastUtil.showToast(cont,"删除失败，请重试  ");
        }
    }
    public static class XPopupListener extends SimpleCallback {
        @Override
        public void onCreated(BasePopupView pv) {
            HiLog.info(LABEL_LOG, "onCreater");
        }

        @Override
        public void onShow(BasePopupView popupView) {
            HiLog.info(LABEL_LOG,"onShow");
        }

        @Override
        public void onDismiss(BasePopupView popupView) {
            HiLog.info(LABEL_LOG,"onDismiss");
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


