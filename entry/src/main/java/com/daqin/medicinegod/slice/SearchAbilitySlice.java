package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;


import com.daqin.medicinegod.provider.search.*;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.miscservices.pasteboard.PasteData;
import ohos.miscservices.pasteboard.SystemPasteboard;

import java.util.*;

//TODO:还差点击跳转到主页更改信息联动
public class SearchAbilitySlice extends AbilitySlice {
    //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
    /**
     * @param src_method 搜索方法：0药名1描述2条码3公司4标签5用量
     * @param src_screen 筛选方法：0过期时间1余量2类型3标签
     * @param src_screen_again 二次筛选方法：0过期时间1余量2类型3标签
     */

    private int src_method = 0;
    private int src_screen = 0;
    private String src_screen_method = null;
    Text btn_src_method;
    Text tf_src_src_box;
    Text btn_src;
    Text btn_src_screen;
    Text btn_src_back;
    String lowKey = null, highKey = null;

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

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_search);
        lowKey = util.PreferenceUtils.getString(this, "lowKey");
        highKey = util.PreferenceUtils.getString(this, "highKey");
        if (lowKey == null) {
            lowKey = "";
        }
        if (highKey == null) {
            highKey = "";
        }
        iniView();
        iniClicklistener();
        initSearchListContainer();

    }
    public void clearMethod(){
        src_method = 0;
        btn_src_method.setText("药 名 ▼");
        btn_src_back.setText("◀ 退出");
        btn_src_method.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_select));
        btn_src_screen.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_normal));
        initSearchListContainer();
    }
    public void clearScreen(){
        src_screen = 0;
        src_screen_method = "";
        btn_src_screen.setText("筛 选 ▼");
        btn_src_back.setText("◀ 退出");
        btn_src_screen.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_normal));
        btn_src_method.setVisibility(Component.VISIBLE);
        btn_src_method.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_select));
        initSearchListContainer();

    }
    private void iniClicklistener() {
        //搜索方法切换
        btn_src_method.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .maxHeight(850)
                    .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                    .asCenterList("请选择搜索条件", new String[]{"药 名", "描 述", "条 码", "公 司", "标 签"},
                            null, src_method,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    src_method = position;
                                    btn_src_method.setText(text + " ▼");
                                    btn_src_method.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_select));
                                    btn_src_screen.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_normal));
                                    initSearchListContainer();
                                }
                            })
                    .show();
        });
        //筛选方法切换
        btn_src_screen.setClickedListener(component -> {
            //如果选择了则添加清空当前条件 的选项
            if (src_screen != 0) {
                new XPopup.Builder(getContext())
                        .maxHeight(900)
                        .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                        .asCenterList("请选择筛选条件", new String[]{"清空当前条件"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        //选择【清空当前条件】
                                        clearScreen();
                                    }
                                })
                        .show();
            } else {
                new XPopup.Builder(getContext())
                        .maxHeight(850)
                        .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                        .asCenterList("请选择筛选条件", new String[]{"以\"过期时间\"为条件筛选", "以\"药品余量\"为条件筛选", "以\"药品类型\"为条件筛选", "以\"药品标签\"为条件筛选"},
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        src_screen = position + 1;
                                        btn_src_method.setVisibility(Component.HIDE);
                                        btn_src_screen.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_search_method_select));
                                        btn_src_back.setText("◀ 清空当前条件");
                                        src_screen_method = "";
                                        switch (src_screen) {
                                            case 1:
                                                new XPopup.Builder(getContext())
                                                        .isDarkTheme(false)
                                                        .dismissOnBackPressed(false)
                                                        .dismissOnTouchOutside(false)
                                                        .isDestroyOnDismiss(true)
                                                        .maxHeight(850)
                                                        .asCenterList("请选择一项", new String[]{"早于...", "在...与...间", "晚于..."},
                                                                new OnSelectListener() {
                                                                    @Override
                                                                    public void onSelect(int position, String text) {
                                                                        Calendar cal = Calendar.getInstance();
                                                                        List<String> yearList = new ArrayList<>();
                                                                        int year_now = cal.get(Calendar.YEAR);
                                                                        for (int i = year_now - 3; i < year_now; i++) {
                                                                            yearList.add(i + "年");
                                                                        }
                                                                        for (int i = year_now; i < year_now + 10; i++) {
                                                                            yearList.add(i + "年");
                                                                        }
                                                                        switch (position) {
                                                                            case 0:
                                                                                new XPopup.Builder(getContext())
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .maxHeight(850)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .asCenterList("早于...(年份)", yearList.toArray(new String[]{}),
                                                                                                new OnSelectListener() {
                                                                                                    @Override
                                                                                                    public void onSelect(int position, String text) {
                                                                                                        src_screen_method = text.replace("年", "");
                                                                                                        new XPopup.Builder(getContext())
                                                                                                                .isDarkTheme(false)
                                                                                                                .maxHeight(850)
                                                                                                                .dismissOnBackPressed(false)
                                                                                                                .dismissOnTouchOutside(false)
                                                                                                                .isDestroyOnDismiss(true)
                                                                                                                .asCenterList("早于" + text + "的(月份)", new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"},
                                                                                                                        new OnSelectListener() {
                                                                                                                            @Override
                                                                                                                            public void onSelect(int position, String text) {
                                                                                                                                src_screen_method = src_screen_method + "-" + text.replace("月", "") + "-1";
                                                                                                                                btn_src_screen.setText("过期时间:早于" + src_screen_method + " ▼");
                                                                                                                                searchScreen(11, DB_COLUMN_OUTDATE, src_screen_method, null);
                                                                                                                            }
                                                                                                                        })
                                                                                                                .show();
                                                                                                    }
                                                                                                })
                                                                                        .show();
                                                                                break;
                                                                            case 1:
                                                                                new XPopup.Builder(getContext())
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .maxHeight(850)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .asCenterList("在...(年份)", yearList.toArray(new String[]{}),
                                                                                                new OnSelectListener() {
                                                                                                    @Override
                                                                                                    public void onSelect(int position, String text) {
                                                                                                        src_screen_method = text.replace("年", "");
                                                                                                        new XPopup.Builder(getContext())
                                                                                                                .isDarkTheme(false)
                                                                                                                .maxHeight(850)
                                                                                                                .dismissOnBackPressed(false)
                                                                                                                .dismissOnTouchOutside(false)
                                                                                                                .isDestroyOnDismiss(true)
                                                                                                                .asCenterList("在" + text + "的(月份)", new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"},
                                                                                                                        new OnSelectListener() {
                                                                                                                            @Override
                                                                                                                            public void onSelect(int position, String text) {
                                                                                                                                src_screen_method = src_screen_method + "-" + text.replace("月", "") + "-1:";
                                                                                                                                new XPopup.Builder(getContext())
                                                                                                                                        .isDarkTheme(false)
                                                                                                                                        .dismissOnBackPressed(false)
                                                                                                                                        .maxHeight(850)
                                                                                                                                        .dismissOnTouchOutside(false)
                                                                                                                                        .isDestroyOnDismiss(true)
                                                                                                                                        .asCenterList("与...(年份)间", yearList.toArray(new String[]{}),
                                                                                                                                                new OnSelectListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSelect(int position, String text) {
                                                                                                                                                        src_screen_method = src_screen_method + text.replace("年", "");
                                                                                                                                                        new XPopup.Builder(getContext())
                                                                                                                                                                .isDarkTheme(false)
                                                                                                                                                                .maxHeight(850)
                                                                                                                                                                .dismissOnBackPressed(false)
                                                                                                                                                                .dismissOnTouchOutside(false)
                                                                                                                                                                .isDestroyOnDismiss(true)
                                                                                                                                                                .asCenterList("在" + text + "的(月份)", new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"},
                                                                                                                                                                        new OnSelectListener() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onSelect(int position, String text) {
                                                                                                                                                                                src_screen_method = src_screen_method + "-" + text.replace("月", "") + "-1";
                                                                                                                                                                                String[] list = src_screen_method.split(":");
                                                                                                                                                                                if (list[0].equals(list[1])){
                                                                                                                                                                                    btn_src_screen.setText("过期时间:在" + list[0].split("-")[0]+"-"+list[0].split("-")[1] + "之内 ▼");
                                                                                                                                                                                }else {
                                                                                                                                                                                    btn_src_screen.setText("过期时间:在" + list[0] + "与" + list[1] + "之间 ▼");
                                                                                                                                                                                }
                                                                                                                                                                                searchScreen(12, DB_COLUMN_OUTDATE, list[0], list[1]);
                                                                                                                                                                            }
                                                                                                                                                                        })
                                                                                                                                                                .show();
                                                                                                                                                    }
                                                                                                                                                })
                                                                                                                                        .show();


                                                                                                                            }
                                                                                                                        })
                                                                                                                .show();
                                                                                                    }
                                                                                                })
                                                                                        .show();
                                                                                break;
                                                                            case 2:
                                                                                new XPopup.Builder(getContext())
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .maxHeight(850)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .asCenterList("晚于...(年份)", yearList.toArray(new String[]{}),
                                                                                                new OnSelectListener() {
                                                                                                    @Override
                                                                                                    public void onSelect(int position, String text) {
                                                                                                        src_screen_method = text.replace("年", "");
                                                                                                        new XPopup.Builder(getContext())
                                                                                                                .isDarkTheme(false)
                                                                                                                .maxHeight(850)
                                                                                                                .dismissOnBackPressed(false)
                                                                                                                .dismissOnTouchOutside(false)
                                                                                                                .isDestroyOnDismiss(true)
                                                                                                                .asCenterList("早于" + text + "的(月份)", new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"},
                                                                                                                        new OnSelectListener() {
                                                                                                                            @Override
                                                                                                                            public void onSelect(int position, String text) {
                                                                                                                                src_screen_method = src_screen_method + "-" + text.replace("月", "") + "-1";
                                                                                                                                btn_src_screen.setText("过期时间:晚于" + src_screen_method + " ▼");
                                                                                                                                searchScreen(13, DB_COLUMN_OUTDATE, src_screen_method, null);
                                                                                                                            }
                                                                                                                        })
                                                                                                                .show();
                                                                                                    }
                                                                                                })
                                                                                        .show();

                                                                                break;
                                                                        }
                                                                    }
                                                                })
                                                        .show();
                                                break;
                                            case 2:
                                                new XPopup.Builder(getContext())
                                                        .isDarkTheme(false)
                                                        .dismissOnBackPressed(false)
                                                        .dismissOnTouchOutside(false)
                                                        .isDestroyOnDismiss(true)
                                                        .maxHeight(850)
                                                        .asCenterList("请选择一项", new String[]{"少于...", "在...与...间", "多于..."},
                                                                new OnSelectListener() {
                                                                    @Override
                                                                    public void onSelect(int position, String text) {
                                                                        switch (position){
                                                                            case 0:
                                                                                new XPopup.Builder(getContext())
                                                                                        .hasStatusBarShadow(true) // 暂无实现
                                                                                        .autoOpenSoftInput(true)
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                                                                                        .asInputConfirm("少于...(数量)", "请填入数量", null, "少于...", new OnInputConfirmListener() {
                                                                                            @Override
                                                                                            public void onConfirm(String s) {
                                                                                                searchScreen(21, DB_COLUMN_YU, s, null);
                                                                                            }
                                                                                        }, null,ResourceTable.Layout_popup_comfirm_with_input_number)
                                                                                        .show();
                                                                                break;
                                                                            case 1:
                                                                                new XPopup.Builder(getContext())
                                                                                        .hasStatusBarShadow(true) // 暂无实现
                                                                                        .autoOpenSoftInput(true)
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                                                                                        .asInputConfirm("在...(数量)与", "请填入数量", null, "前数量...", new OnInputConfirmListener() {
                                                                                            @Override
                                                                                            public void onConfirm(String s) {
                                                                                                src_screen_method = s;
                                                                                                new XPopup.Builder(getContext())
                                                                                                        .hasStatusBarShadow(true) // 暂无实现
                                                                                                        .autoOpenSoftInput(true)
                                                                                                        .isDarkTheme(false)
                                                                                                        .dismissOnBackPressed(false)
                                                                                                        .dismissOnTouchOutside(false)
                                                                                                        .isDestroyOnDismiss(true)
                                                                                                        .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                                                                                                        .asInputConfirm("在...(数量)与", "请填入数量", null, "前数量...", new OnInputConfirmListener() {
                                                                                                            @Override
                                                                                                            public void onConfirm(String s) {
                                                                                                                searchScreen(22, DB_COLUMN_YU, src_screen_method, s);
                                                                                                            }
                                                                                                        }, null,ResourceTable.Layout_popup_comfirm_with_input_number)
                                                                                                        .show();
                                                                                            }
                                                                                        }, null,ResourceTable.Layout_popup_comfirm_with_input_number)
                                                                                        .show();
                                                                                break;
                                                                            case 2:
                                                                                new XPopup.Builder(getContext())
                                                                                        .hasStatusBarShadow(true) // 暂无实现
                                                                                        .autoOpenSoftInput(true)
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .isDestroyOnDismiss(true)
                                                                                        .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                                                                                        .asInputConfirm("多于...(数量)", "请填入数量", null, "多于...", new OnInputConfirmListener() {
                                                                                            @Override
                                                                                            public void onConfirm(String s) {
                                                                                                searchScreen(23, DB_COLUMN_YU, s, null);
                                                                                            }
                                                                                        }, null,ResourceTable.Layout_popup_comfirm_with_input_number)
                                                                                        .show();
                                                                                break;
                                                                        }
                                                                    }
                                                                })
                                                        .show();
                                                break;
                                        }
                                    }
                                })
                        .show();
            }

        });
        //点击返回
        btn_src_back.setClickedListener(component -> {
            if (src_screen!=0){
                clearScreen();
            }else if(src_method!=0){
                clearMethod();
            }else{
                terminate();
            }
        });
        //搜索方法
        btn_src.setClickedListener(component -> {
            //src_screen是筛选变量，当筛选条件存在时，不进行目标性搜索
            //src_screen = 0时说明未进行筛选
            if (src_screen != 0) {
                switch (src_screen) {
                    case 1:
//                        searchScreen(src_screen,DB_COLUMN_OUTDATE,tf_src_src_box.getText().trim());
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;

                }
            } else {
                btn_src_back.setText("◀ 返回上一级");
                switch (src_method) {
                    case 0:
                        searchAssign(src_method, DB_COLUMN_NAME, tf_src_src_box.getText().trim());
                        break;
                    case 1:
                        searchAssign(src_method, DB_COLUMN_DESCRIPTION, tf_src_src_box.getText().trim());
                        break;
                    case 2:
                        searchAssign(src_method, DB_COLUMN_BARCODE, tf_src_src_box.getText().trim());
                        break;
                    case 3:
                        searchAssign(src_method, DB_COLUMN_COMPANY, tf_src_src_box.getText().trim());
                        break;
                    case 4:
                        searchAssign(src_method, DB_COLUMN_ELABEL, tf_src_src_box.getText().trim());
                        break;
                }
            }

        });


    }


    private void iniView() {
        btn_src_method = (Text) findComponentById(ResourceTable.Id_src_srcmethod);
        tf_src_src_box = (TextField) findComponentById(ResourceTable.Id_src_srcbox);
        btn_src = (Text) findComponentById(ResourceTable.Id_src_src);
        btn_src_back = (Text) findComponentById(ResourceTable.Id_src_back);
        btn_src_screen = (Text) findComponentById(ResourceTable.Id_src_srceen);

    }

    //筛选搜索
    private void searchScreen(int method, String field, String value1, String value2) {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_src_list);
        // 2.实例化数据源
        List<Map<String, Object>> list = MainAbilitySlice.queryScreenData(method, field, value1, value2);
        if (list == null) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("没有数据", "未搜索到任何数据",
                            " ", "好", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    clearScreen();
                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else {
            switch (method) {
                case 11:
                    //这里是早于...年份的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_11 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_11);
                    break;
                case 12:
                    //这里是在...与...间的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_12 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_12);
                case 13:
                    //这里是晚于...年份的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_13 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_13);

                case 21:
                    //这里是早于...年份的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_21 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_21);
                    break;
                case 22:
                    //这里是在...与...间的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_22 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_22);
                case 23:
                    //这里是晚于...年份的筛选处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_23 = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_23);



            }


        }
    }

    //指定搜索
    private void searchAssign(int method, String field, String value) {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_src_list);
        // 2.实例化数据源
        List<Map<String, Object>> list = MainAbilitySlice.queryAssignData(field, value);
        if (list == null) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("没有数据", "未搜索到任何数据",
                            " ", "好", new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    clearMethod();
                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else {
            switch (method) {
                case 0:
                    //这里是药名的搜索处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_name = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_name);
                    break;
                case 1:
                    //这里是描述的搜索处理对象
                    // 3.初始化Provider对象,
                    DespProvider listItemProvider_desp = new DespProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_desp);
                    break;
                case 2:
                    //这里是条码的搜索处理对象
                    // 3.初始化Provider对象,
                    BarCodeProvider listItemProvider_barcode = new BarCodeProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_barcode);
                    break;
                case 3:
                    //这里是公司的搜索处理对象
                    // 3.初始化Provider对象,
                    CompanyProvider listItemProvider_company = new CompanyProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_company);
                    break;
                case 4:
                    //这里是标签的搜索处理对象
                    // 3.初始化Provider对象,
                    ElabelProvider listItemProvider_elabel = new ElabelProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_elabel);
                    break;

            }


        }
    }

    // 初始化搜索页默认的的ListContainer
    private void initSearchListContainer() {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_src_list);
        // 2.实例化数据源
        List<Map<String, Object>> list = MainAbilitySlice.querySearchData(lowKey, highKey);
        if (list == null) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("没有数据", "您还未添加任何数据！\n\n即将退出此界面。",
                            " ", "好", new OnConfirmListener() {
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

        } else {
            // 3.初始化Provider对象
            NormalProvider listItemProvider = new NormalProvider(list, this);
            // 4.适配要展示的内容数据
            listContainer.setItemProvider(listItemProvider);
            // 5.设置每个Item的点击事件
            listContainer.setItemClickedListener((container, component, position, id) -> {
//                Map<String, Object> item = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                Map<String, Object> res = list.get(position);
                util.PreferenceUtils.putString(this, "mglocalkey", res.getOrDefault("keyid", null).toString());
                //弹出弹框查看详情后再返回
                Intent intentDeital = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                        .withBundleName(getBundleName())    // 包名
                        .withAbilityName("com.daqin.medicinegod.DetailAbility")
                        // Ability页面的名称，在本地可以缺省前面的路径
                        .build();    // 构建代码
                intentDeital.setOperation(operation);    // 将operation存入到intent中
                startAbilityForResult(intentDeital, 999);    // 实现Ability跳转
            });
            listContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
                @Override
                public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                    if (src_method == 2) {
                        //将编码放到剪贴板
                        Map<String, Object> res_barcode = list.get(i);
                        SystemPasteboard mPasteboard = SystemPasteboard.getSystemPasteboard(getContext());
                        ;
                        PasteData pasteData = PasteData.creatPlainTextData((String) res_barcode.get("barcode"));
                        mPasteboard.setPasteData(pasteData);
                        ToastUtil.showToast(getContext(), "条码已复制  ");
                    } else {
                        //将编码放到剪贴板
                        Map<String, Object> res_name = list.get(i);
                        SystemPasteboard mPasteboard = SystemPasteboard.getSystemPasteboard(getContext());
                        ;
                        PasteData pasteData = PasteData.creatPlainTextData((String) res_name.get("name"));
                        mPasteboard.setPasteData(pasteData);
                        ToastUtil.showToast(getContext(), "名称已复制  ");
                    }
                    return false;
                }
            });


        }
    }


    @Override
    protected void onActive() {
        super.onActive();
        //editok属性包括{ ok (修改完成) , none(无) }
        String editok = util.PreferenceUtils.getString(getContext(), "editok");
        if (editok.equals("ok")) {
            initSearchListContainer();
        }
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
    }
}
