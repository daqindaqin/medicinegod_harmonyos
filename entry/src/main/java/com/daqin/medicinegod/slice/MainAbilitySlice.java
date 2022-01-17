package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.CustomFullScreenPopup;
import com.daqin.medicinegod.OTCQuestionPopup;
import com.daqin.medicinegod.FlowLayout;
import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.*;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.lxj.xpopup.util.ToastUtil;
import com.zzti.fengyongge.imagepicker.ImagePickerInstance;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.render.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.IBundleManager;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.preferences.Preferences;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.global.resource.NotExistException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


//TODO：图片圆角、流式布局
public class MainAbilitySlice extends AbilitySlice {
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;   //自定义的一个权限请求识别码，用于处理权限回调
    private BubbleNavigationLinearView mBubbleNavigationLinearView;
    private FlowLayout mflowLayout;
//    private List<String> labelList = new ArrayList<>();

    static int newUsage_utils_1 = 0,newUsage_utils_3 = 0;
    BasePopupView DIALOG;

    private String imgpath = null;
    private int idInsert = 0;
    private String ELABEL = "headache,aligei,nb,666";


    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "MainAbilitySlice");
    private static final String BASE_URI = "dataability:///com.daqin.medicinegod.utils.PersonDataAbility";
    private static final String DATA_PATH = "/mg";
    private static final String DB_COLUMN_ID = "ID";
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
    private DataAbilityHelper databaseHelper;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
//        mflowLayout = (FlowLayout) findComponentById(ResourceTable.Id_flow_layout);
//        labelList.clear();
        databaseHelper = DataAbilityHelper.creator(this);
        idInsert = PreferenceUtils.getInt(this,"idInset");
        intPageStart();
        intHeadView();
        initHomepageListContainer();



//        initCommunityListContainer();
//        initChatListContainer();



        //清空添加药品的列表
        Button btn_clear= (Button)findComponentById(ResourceTable.Id_add_clear);
        btn_clear.setClickedListener(l->clearAddTextfield());
        //otc疑问按钮
        Text btn_otc_question = (Text)findComponentById(ResourceTable.Id_add_newOtc_question);
        btn_otc_question.setClickedListener(l->{
            new XPopup.Builder(getContext())
                    .moveUpToKeyboard(false) // 如果不加这个，评论弹窗会移动到软键盘上面
                    .enableDrag(true)
                    .asCustom(new OTCQuestionPopup(getContext()))
                    .show();

        });
        //添加图片的图片按钮
        Image addimg=(Image)findComponentById(ResourceTable.Id_add_newImg);
        addimg.setClickedListener(new Component.ClickedListener(){
            @Override
            public void onClick(Component component) {
                if (verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
                    // 应用未被授予权限
                    if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                        // 是否可以申请弹框授权(首次申请或者用户未选择禁止且不再提示)
                        requestPermissionsFromUser(
                                new String[] { "ohos.permission.READ_MEDIA" } , MY_PERMISSIONS_REQUEST_READ_MEDIA);
                        ImagePickerInstance.getInstance().photoSelect(
                                MainAbilitySlice.this, 1,true, 0);
                    } else {
                        // 显示应用需要权限的理由，提示用户进入设置授权
                        ToastUtil.showToast(getContext(),"请进入系统设置进行授权");
                    }
                } else {
                    // 权限已被授予
                    //加载显示系统相册中的照片
                    ImagePickerInstance.getInstance().photoSelect(
                            MainAbilitySlice.this, 1,true, 0);
                }
            }
        });

        //用法用量单位变换
        Text btn_newUsage_utils_1 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_1);
        Text settitle = (Text)findComponentById(ResourceTable.Id_add_newYu_title) ;
        btn_newUsage_utils_1.setClickedListener(l->{
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1){
                case 1:
                    btn_newUsage_utils_1.setText("克");
                    settitle.setText("剩余余量(单位:克)");
                    break;
                case 2:
                    btn_newUsage_utils_1.setText("包");
                    settitle.setText("剩余余量(单位:包)");
                    break;
                case 3:
                    btn_newUsage_utils_1.setText("片");
                    settitle.setText("剩余余量(单位:片)");
                    newUsage_utils_1 = 0;
                    break;
            }
        });
        Text btn_newUsage_utils_3 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_3);
        btn_newUsage_utils_3.setClickedListener(l->{
            newUsage_utils_3 += 1;
            switch (newUsage_utils_3){
                case 1:
                    btn_newUsage_utils_3.setText("时");
                    break;
                case 2:
                    btn_newUsage_utils_3.setText("天");
                    newUsage_utils_3 = 0;
                    break;
            }
        });

        //添加药效标签
//        Button btn_addNewLabel = (Button)findComponentById(ResourceTable.Id_add_newLabel_addButton);
//        btn_addNewLabel.setClickedListener(l->addNewLabel());
        ScrollView view = (ScrollView)findComponentById(ResourceTable.Id_add_scrollview);
        TextField add_name = (TextField)findComponentById(ResourceTable.Id_add_newName);
        TextField add_desp = (TextField)findComponentById(ResourceTable.Id_add_newDescription);
        Picker add_outdate_year = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_year);
        Picker add_outdate_month = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_month);
        Picker add_otc = (Picker)findComponentById(ResourceTable.Id_add_newOtc);
        TextField add_barcode = (TextField)findComponentById(ResourceTable.Id_add_newbarCode);
        TextField add_usage_total = (TextField)findComponentById(ResourceTable.Id_add_newUsage_1);
        TextField add_usage_time = (TextField)findComponentById(ResourceTable.Id_add_newUsage_2);
        TextField add_usage_day = (TextField)findComponentById(ResourceTable.Id_add_newUsage_3);
        TextField add_company = (TextField)findComponentById(ResourceTable.Id_add_newCompany);
        TextField add_yu = (TextField)findComponentById(ResourceTable.Id_add_newYu);
        Button btn_ok = (Button)findComponentById(ResourceTable.Id_add_addOk);

        Text method1 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_1);

        Text method3 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_3);



        TextField[] texttList = {add_name,add_desp,add_barcode,add_usage_total,add_usage_time,add_usage_day,add_company,add_yu};

        btn_ok.setClickedListener(l->{
            //检测已经选择图片
            insert(idInsert,
                    "Name"+idInsert,
                    "imgpath",
                    "描述描述描述描述描述描述",
                    "2022年-2月",
                    "OTC-G",
                    "6666666666",
                    //XX-包/克/片-XX-次-XX-时/天
                    "1-包-3-次-1-天",
                    "company",
                    "2453",
                    ELABEL);
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
    //
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

    //轻量数据库相关
    static class PreferenceUtils {

        private static String PREFERENCE_FILE_NAME = "mgconfig";
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
         * @return 获取int的默认值为：0
         */
        public static int getInt(Context context, String key) {
            initPreference(context);
            return preferences.getInt(key, 0);
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

    //设置scrollview与list一起滚动，暂废弃
    /*
    private void setListContainerHeight(ListContainer listContainer) {
        //获取当前listContainer的适配器
        BaseItemProvider BaseItemProvider = listContainer.getItemProvider();
        if (BaseItemProvider == null){
            return;
        }
        int itemHeight = 0;
        for (int i = 0; i < BaseItemProvider.getCount(); i++) {
            //循环将listContainer适配器的Item数据进行累加
            Component listItem = BaseItemProvider.getComponent(i, null, listContainer);
            itemHeight += listItem.getHeight();
        }
        //对当前listContainer进行高度赋值
        ComponentContainer.LayoutConfig config = listContainer.getLayoutConfig();
        //这边加上(listContainer.getBoundaryThickness() * (BaseItemProvider.getCount()+1))
        //listContainer.getBoundaryThickness() 就是分界线的高度
        //(BaseItemProvider.getCount()+1) 是Item的数量  加1  是因为顶部还有一条分界线
        config.height = itemHeight
                + (listContainer.getBoundaryThickness() * (BaseItemProvider.getCount()+1));
        //赋值
        listContainer.setLayoutConfig(config);
    }
    */

    //清空添加药品的列表
    private void clearAddTextfield() {

        TextField textfield = (TextField)findComponentById(ResourceTable.Id_add_newName);
        textfield.setText("");
        TextField textfield1 = (TextField)findComponentById(ResourceTable.Id_add_newDescription);
        textfield1.setText("");
        Image addimg = (Image)findComponentById(ResourceTable.Id_add_newImg);
        addimg.setPixelMap(ResourceTable.Media_add_imgadd);
        addimg.setScaleMode(Image.ScaleMode.CENTER);
        TextField textfield2 = (TextField)findComponentById(ResourceTable.Id_add_newbarCode);
        textfield2.setText("");
        TextField textfield3 = (TextField)findComponentById(ResourceTable.Id_add_newCompany);
        textfield3.setText("");
        TextField textfield4 = (TextField)findComponentById(ResourceTable.Id_add_newUsage_1);
        textfield4.setText("");
        TextField textfield5 = (TextField)findComponentById(ResourceTable.Id_add_newUsage_2);
        textfield5.setText("");
        Text text1 = (TextField)findComponentById(ResourceTable.Id_add_newUsage_utils_1);
        text1.setText("包");
        Text text2 = (TextField)findComponentById(ResourceTable.Id_add_newUsage_utils_2);
        text2.setText("天");
        iniCalendarPicker();

    }

    //导航栏和窗口初始化
    private void intPageStart(){
        ArrayList<DependentLayout> fragList = new ArrayList<>();

        LayoutScatter layoutScatter = LayoutScatter.getInstance(getContext());
        DependentLayout inflatedView = (DependentLayout) layoutScatter.parse(
                ResourceTable.Layout_ability_main_things, null, false);
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
                ResourceTable.Layout_ability_main_home, null, false);
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
            idInsert = PreferenceUtils.getInt(this,"idInset");
            System.out.println("ID是"+idInsert);
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


    //定义时间选择器
    private void iniCalendarPicker() {
        Calendar cal = Calendar.getInstance();
        List<String> yearList = new ArrayList<>();
        int year_now = cal.get(Calendar.YEAR);
        for (int i = year_now; i <= year_now+9 ; i++) {
            yearList.add(i +"年");
        }

        Picker pickerYear = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_year);
        pickerYear.setHeight(util.getWindowWidthPx(MainAbilitySlice.this)/4);
        pickerYear.setDisplayedData(yearList.toArray(new String[]{}));
        pickerYear.setValue(0);

        Picker pickerMonth = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_month);
        pickerMonth.setDisplayedData(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"});
        pickerMonth.setHeight(util.getWindowWidthPx(MainAbilitySlice.this)/4);
        pickerMonth.setValue(0);

        Picker pickerOtc = (Picker)findComponentById(ResourceTable.Id_add_newOtc);
        pickerOtc.setDisplayedData(new String[]{"OTC(非处方药)-红","OTC(非处方药)-绿","(空)","RX(处方药)"});
        pickerOtc.setValue(2);




    }


    //圆角图形
    private void intHeadView() {


        //从资源文件加载PixelMap
        PixelMap originMap = getPixelMapFromResource(ResourceTable.Media_head);
        Image imgOrigin = (Image) findComponentById(ResourceTable.Id_things_image_head);
        imgOrigin.setPixelMap(originMap);

        //获取原图片的大小
        assert originMap != null;
        Size originSize = originMap.getImageInfo().size;
        PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
        options.size = new Size(originSize.width, originSize.height);
        options.pixelFormat = PixelFormat.ARGB_8888;
        options.editable = true;
        //创建结果PixelMap
        PixelMap circlePixelMap = PixelMap.create(options);
        Canvas canvas = new Canvas();
        //将结果PixelMap作为画布背景
        Texture texture = new Texture(circlePixelMap);
        canvas.setTexture(texture);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        PixelMapHolder pixelMapHolder = new PixelMapHolder(PixelMap.create(originMap, options));
        PixelMapShader shader = new PixelMapShader(pixelMapHolder, Shader.TileMode.CLAMP_TILEMODE, Shader.TileMode.CLAMP_TILEMODE);
        paint.setShader(shader, Paint.ShaderType.PIXELMAP_SHADER);
        //圆角矩形图
//        RectFloat rect = new RectFloat(50, 50, originSize.width - 20, originSize.height -20);
//        canvas.drawRoundRect(rect, 50,50, paint);
        //圆形图
        canvas.drawCircle(originSize.width * 1.0f / 2, originSize.height * 1.0f / 2, originSize.width * 1.0f / 2, paint);
        Image imgCircle = (Image) findComponentById(ResourceTable.Id_things_image_head);
        imgCircle.setPixelMap(circlePixelMap);



    }
    private PixelMap getPixelMapFromResource(int resourceId) {
        try (InputStream inputStream = getContext().getResourceManager().getResource(resourceId)) {
            // 创建图像数据源ImageSource对象
            ImageSource.SourceOptions srcOpts = new ImageSource.SourceOptions();
            srcOpts.formatHint = "image/jpg";
            ImageSource imageSource = ImageSource.create(inputStream, srcOpts);
            // 设置图片参数
            ImageSource.DecodingOptions decodingOptions = new ImageSource.DecodingOptions();
            return imageSource.createPixelmap(decodingOptions);
        } catch (IOException | NotExistException ignored) {
        }
        return null;
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
            int localP = position;
            Map<String,Object> item = (Map<String,Object>) listContainer.getItemProvider().getItem(position);
            new XPopup.Builder(this)
                    .isDarkTheme(false)
                    .asCenterList("请选择一项", new String[]{"使用", "编辑", "删除", "共享"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
//                                    switch (position){
//                                        case 0:
//                                    }
                                    ToastUtil.showToast(getContext(),"click " +localP+ position+"  ");




                                    //TODO:加一个显示页面显示数据,修改
                                    new XPopup.Builder(getContext())
                                            .hasStatusBarShadow(false)
                                            .autoOpenSoftInput(false)
                                            .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                                            .asCustom(new CustomFullScreenPopup(getContext()))
                                            .show();
                                }
                            })
                    .show();

        });

    }

    private List<Map<String,Object>> queryData() {
        List<Map<String,Object>> list = new ArrayList<>();
        String[] columns = new String[] {DB_COLUMN_ID,
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
        predicates.between(DB_COLUMN_ID, 0, 9999);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return new ArrayList<>();
            }
            resultSet.goToFirstRow();
            int a = 0;
            do {
                Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
                int id = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_ID));
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
                map.put("id",id);
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
                HiLog.info(LABEL_LOG, "query: Id :" + id + " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
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
        Image addimg=(Image)findComponentById(ResourceTable.Id_add_newImg);
        switch (requestCode) {
            case 0:
                if (data != null) {
                    //取得图片路径
                    String paths = data.getStringArrayListParam("photos").get(0);
                    imgpath = data.getStringArrayListParam("photos").get(0);
                    //原组件是居中，这里给他选择填充
                    addimg.setScaleMode(Image.ScaleMode.STRETCH);
//                    定义组件资源
                    ImageSource imageSource = null;
                    DataAbilityHelper helper=DataAbilityHelper.creator(getContext());
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
                    addimg.setPixelMap(pixelMap);
//                    String base64 = util.getImageBase64(paths);
//                    System.out.println("BBBBBBBBBBBBASE"+base64);

                    //TODO:跳转去裁剪图片
//                    Intent newIntent = new Intent();
//                    newIntent.setParam("startcropimage", paths);
//                    present(new ImageCropAbilitySlice(), newIntent);
                }
                break;
            default:
                addimg.setScaleMode(Image.ScaleMode.CENTER);
                addimg.setPixelMap(ResourceTable.Media_add_imgadd);
                break;
        }
        super.onAbilityResult(requestCode, resultCode, data);
    }


    //获取、刷新数据
    private void query() {
        String[] columns = new String[] {DB_COLUMN_ID,
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
        predicates.between(DB_COLUMN_ID, 0, 9999);
        try {
            ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                    columns, predicates);
            if (resultSet == null || resultSet.getRowCount() == 0) {
                HiLog.info(LABEL_LOG, "query: resultSet is null or no result found");
                return;
            }
            resultSet.goToFirstRow();
            do {
                int id = resultSet.getInt(resultSet.getColumnIndexForName(DB_COLUMN_ID));
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
                HiLog.info(LABEL_LOG, "query: Id :" + id + " name:"+name+ " imagepath:"+imagepath+ " description:"+description+ " outdate:"+outdate
                        + " otc:"+otc+ " barcode:"+barcode+ " :"+usage+ " company:"+company+ " yu:"+yu+ " elabel:"+elabel);
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "query: dataRemote exception | illegalStateException");
        }
    }
    //插入数据
    private void insert(int id, String name, String imagepath,
                        String description, String outdate, String otc,
                        String barcode, String usage, String company,
                        String yu ,String elabel) {
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putInteger(DB_COLUMN_ID, id);
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putString(DB_COLUMN_IMAGEPATH, imagepath);
        valuesBucket.putString(DB_COLUMN_DESCRIPTION, description);
        valuesBucket.putString(DB_COLUMN_OUTDATE, outdate);
        valuesBucket.putString(DB_COLUMN_OTC, otc);
        valuesBucket.putString(DB_COLUMN_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_YU, yu);
        valuesBucket.putString(DB_COLUMN_ELABEL, elabel);

        try {
            if (databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket) != -1) {
                HiLog.info(LABEL_LOG, "insert successful");
                idInsert++;
                PreferenceUtils.putInt(this,"idInset",id + 1);
                ToastUtil.showToast(this,"添加成功");
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "insert: dataRemote exception|illegalStateException");
            ToastUtil.showToast(this,"添加失败，请重试");
        }
    }
    //更新
    private void update(int id, String name, String imagepath,
                        String description, String outdate, String otc,
                        String barcode, String usage, String company,
                        String yu ,String elabel) {
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo(DB_COLUMN_ID, id);
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putString(DB_COLUMN_IMAGEPATH, imagepath);
        valuesBucket.putString(DB_COLUMN_DESCRIPTION, description);
        valuesBucket.putString(DB_COLUMN_OUTDATE, outdate);
        valuesBucket.putString(DB_COLUMN_OTC, otc);
        valuesBucket.putString(DB_COLUMN_BARCODE, barcode);
        valuesBucket.putString(DB_COLUMN_USAGE, usage);
        valuesBucket.putString(DB_COLUMN_COMPANY, company);
        valuesBucket.putString(DB_COLUMN_YU, yu);
        valuesBucket.putString(DB_COLUMN_ELABEL, elabel);
        try {
            if (databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates) != -1) {
                HiLog.info(LABEL_LOG, "update successful");
                ToastUtil.showToast(this,"修改成功");

            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "update: dataRemote exception | illegalStateException");
            ToastUtil.showToast(this,"修改失败，请重试");

        }
    }
    //删除
    private void delete(int id) {
        DataAbilityPredicates predicates = new DataAbilityPredicates()
                .equalTo(DB_COLUMN_ID, id);
        try {
            if (databaseHelper.delete(Uri.parse(BASE_URI + DATA_PATH), predicates) != -1) {
                HiLog.info(LABEL_LOG, "delete successful");
                idInsert--;
                PreferenceUtils.putInt(this,"idInset",id - 1);
                ToastUtil.showToast(this,"删除成功");
                //TODO：删除是否id会空余，修复空余
            }
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "delete: dataRemote exception | illegalStateException");
            ToastUtil.showToast(this,"删除失败，请重试");
        }
    }
}


