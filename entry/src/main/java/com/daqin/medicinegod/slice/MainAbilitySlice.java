package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.CommentPopup;
import com.daqin.medicinegod.FlowLayout;
import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.ChatListItemProvider;
import com.daqin.medicinegod.utils.HomePageListItemProvider;
import com.daqin.medicinegod.utils.ScreenSlidePagerProvider;
import com.daqin.medicinegod.utils.WindowsUtil;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.lxj.xpopup.XPopup;
import com.zzti.fengyongge.imagepicker.ImagePickerInstance;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.render.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.IBundleManager;
import ohos.global.resource.NotExistException;
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

import static java.lang.Math.abs;
//TODO：图片圆角、流式布局
public class MainAbilitySlice extends AbilitySlice {
    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 0;   //自定义的一个权限请求识别码，用于处理权限回调
    private BubbleNavigationLinearView mBubbleNavigationLinearView;
    private FlowLayout mflowLayout;
//    private List<String> labelList = new ArrayList<>();

    static int newUsage_utils_1 = 0,newUsage_utils_2 = 0;



    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
//        mflowLayout = (FlowLayout) findComponentById(ResourceTable.Id_flow_layout);
//        labelList.clear();


        intPageStart();
        initHomepageListContainer();

        intHeadView();


        //清空添加药品的列表
        Button btn_clear = (Button)findComponentById(ResourceTable.Id_add_clear);
        btn_clear.setClickedListener(l->clearAddTextfield());
        //otc疑问按钮
        Text btn_otc_question = (Text)findComponentById(ResourceTable.Id_add_newOtc_question);
        btn_otc_question.setClickedListener(l->{
            new XPopup.Builder(getContext())
                    .moveUpToKeyboard(false) // 如果不加这个，评论弹窗会移动到软键盘上面
                    .enableDrag(true)
                    .asCustom(new CommentPopup(getContext()))
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
                        new ToastDialog(getContext()).setText("请进入系统设置进行授权").show();
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
        btn_newUsage_utils_1.setClickedListener(l->{
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1){
                case 1:
                    btn_newUsage_utils_1.setText("克");
                    break;
                case 2:
                    btn_newUsage_utils_1.setText("包");
                    newUsage_utils_1 = 0;
                    break;
            }
        });
        Text btn_newUsage_utils_2 = (Text)findComponentById(ResourceTable.Id_add_newUsage_utils_2);
        btn_newUsage_utils_2.setClickedListener(l->{
            newUsage_utils_2 += 1;
            switch (newUsage_utils_2){
                case 1:
                    btn_newUsage_utils_2.setText("时");
                    break;
                case 2:
                    btn_newUsage_utils_2.setText("天");
                    newUsage_utils_2 = 0;
                    break;
            }
        });

        //添加药效标签
//        Button btn_addNewLabel = (Button)findComponentById(ResourceTable.Id_add_newLabel_addButton);
//        btn_addNewLabel.setClickedListener(l->addNewLabel());


    }


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
        intCalendarPicker();

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
                ResourceTable.Layout_ability_main_find, null, false);
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
            switch (position){
                case 2:
                    intCalendarPicker();
                    break;
                case 3:
                    initChatListContainer();
                    break;
                default:
                    break;
            }
            viewPager.setCurrentPage(position, true);

        });
    }

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
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();
        });

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
    private void intCalendarPicker() {
        Calendar cal = Calendar.getInstance();
        List<String> yearList = new ArrayList<>();
        int year_now = cal.get(Calendar.YEAR);
        for (int i = year_now; i <= year_now+9 ; i++) {
            yearList.add(i +"年");
        }

        Picker pickerYear = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_year);
        pickerYear.setHeight(WindowsUtil.getWindowWidthPx(MainAbilitySlice.this)/4);
        pickerYear.setDisplayedData(yearList.toArray(new String[]{}));
        pickerYear.setValue(0);

        Picker pickerMonth = (Picker)findComponentById(ResourceTable.Id_add_newOutdate_month);
        pickerMonth.setDisplayedData(new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"});
        pickerMonth.setHeight(WindowsUtil.getWindowWidthPx(MainAbilitySlice.this)/4);
        pickerMonth.setValue(0);

        Picker pickerOtc = (Picker)findComponentById(ResourceTable.Id_add_newOtc);
        pickerOtc.setDisplayedData(new String[]{"OTC(非处方药)","(空)","RX(处方药)"});
        pickerOtc.setValue(1);

        Picker pickerYu = (Picker)findComponentById(ResourceTable.Id_add_newYu);
        pickerYu.setDisplayedData(new String[]{"1","2","3","4","5","6","7","8","9","10"});
        pickerYu.setValue(0);


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

        listContainer.setHeight(abs(WindowsUtil.getWindowHeightPx(MainAbilitySlice.this)- WindowsUtil.getWindowWidthPx(MainAbilitySlice.this))+2*findComponentById(ResourceTable.Id_head_of_top).getHeight()+130);


        // 2.实例化数据源
        List<Map<String,Object>> list = getData();
        // 3.初始化Provider对象
        HomePageListItemProvider listItemProvider = new HomePageListItemProvider(list,this);
        // 4.适配要展示的内容数据
        listContainer.setItemProvider(listItemProvider);
        // 5.设置每个Item的点击事件
        listContainer.setItemClickedListener((container, component, position, id) -> {
            Map<String,Object> item = (Map<String,Object>) listContainer.getItemProvider().getItem(position);
            new ToastDialog(this)
                    .setDuration(2000)
                    .setText("你点击了:" + item.get("name")+"，"+item.get("outdate"))
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();

        });

    }
    // 初始化药品主页的数据源
    private List<Map<String,Object>> getData(){
        List<Map<String,Object>> list;
        // icon图标
        int[] images = {ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test,
                ResourceTable.Media_test,ResourceTable.Media_test};

        String[] names={"曹操","刘备","关羽","诸葛亮","小乔","貂蝉","吕布","赵云","黄盖","周瑜"};
        String[] outdate={"一代枭雄","卖草鞋","财神","卧龙先生","周瑜媳妇","四大镁铝","天下无双","常胜将军","愿意挨打","愿意打人"};
        String[] otc={"OTC","Rx","OTC","Rx","OTC","Rx","OTC","Rx","OTC","Rx"};

        list= new ArrayList<>();
        for(int i=0;i<images.length;i++){
            Map<String, Object> map = new HashMap<>();
//            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", images[i]);
            map.put("name", names[i]);
            map.put("otc",otc[i]);
            map.put("outdate", outdate[i]);
            list.add(map);
        }


        return list;
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

}

