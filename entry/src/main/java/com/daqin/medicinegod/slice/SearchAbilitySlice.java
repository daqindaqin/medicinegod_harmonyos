package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;


import com.daqin.medicinegod.provider.search.*;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.miscservices.pasteboard.PasteData;
import ohos.miscservices.pasteboard.SystemPasteboard;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class SearchAbilitySlice extends AbilitySlice {
    //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
    /**
     * @param src_method 搜索方法：0药名1描述2条码3公司4标签5用量
     * @param src_screen 筛选方法：0过期时间1余量2类型3标签
     */
    int randomnum = 0;
    String[] nameAll;
    private int src_method = 0;
    private int src_screen = 0;
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

        if (util.PreferenceUtils.getString(getContext(), "nameALL") != null){
            nameAll= util.PreferenceUtils.getString(getContext(), "nameALL").split("@@");
            randomnum = new Random().nextInt(nameAll.length);
            tf_src_src_box.setHint("要搜索\"" + nameAll[randomnum ] + "\"吗?");
        }



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
                                    initSearchListContainer();
                                }
                            })
                    .show();
        });
        //筛选方法切换
        btn_src_screen.setClickedListener(component -> {
            if (src_screen != 0) {
                new XPopup.Builder(getContext())
                        .maxHeight(850)
                        .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                        .asCenterList("请选择筛选条件", new String[]{"清空当前条件", "再以\"过期时间\"为条件筛选", "再以\"药品余量\"为条件筛选", "再以\"药品类型\"为条件筛选", "再以\"药品标签\"为条件筛选"},
                                null, src_screen,
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        if (position != 0) {
                                            src_screen = position;
                                            btn_src_screen.setText(text + " ▼");
                                            btn_src_method.setVisibility(Component.HIDE);

                                        } else {
                                            src_screen = 0;
                                            btn_src_screen.setText("筛 选 ▼");
                                            btn_src_method.setVisibility(Component.VISIBLE);

                                        }

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
                                        btn_src_screen.setText(text + " ▼");
                                        btn_src_method.setVisibility(Component.HIDE);
                                    }
                                })
                        .show();
            }

        });
        //点击返回
        btn_src_back.setClickedListener(component -> terminate());
        //搜索方法
        btn_src.setClickedListener(component -> {
            if (tf_src_src_box.getText().equals("")){
                tf_src_src_box.setText(nameAll[randomnum]);
            }else{
                switch (src_method){
                    case 0:
                        startSearchListContainer(src_method,DB_COLUMN_NAME,tf_src_src_box.getText().trim());
                        break;
                    case 1:
                        startSearchListContainer(src_method,DB_COLUMN_DESCRIPTION,tf_src_src_box.getText().trim());
                        break;
                    case 2:
                        startSearchListContainer(src_method,DB_COLUMN_BARCODE,tf_src_src_box.getText().trim());
                        break;
                    case 3:
                        startSearchListContainer(src_method,DB_COLUMN_COMPANY,tf_src_src_box.getText().trim());
                        break;
                    case 4:
                        startSearchListContainer(src_method,DB_COLUMN_ELABEL,tf_src_src_box.getText().trim());
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
    private void startSearchListContainer(int method,String field,String value) {
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
                            " ", "好",null,null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else {
            switch (method){
                case 0:
                    //这里是药名的搜索处理对象
                    // 3.初始化Provider对象,
                    NormalProvider listItemProvider_name = new NormalProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_name);
                    // 5.设置每个Item的点击事件
                    listContainer.setItemClickedListener((container, component, position, id) -> {
                        Map<String, Object> item_name = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                        Map<String, Object> res_name = list.get(position);
                    });
                    break;
                case 1:
                    //这里是描述的搜索处理对象
                    // 3.初始化Provider对象,
                    DespProvider listItemProvider_desp = new DespProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_desp);
                    // 5.设置每个Item的点击事件
                    listContainer.setItemClickedListener((container, component, position, id) -> {
                        Map<String, Object> item_desp = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                        Map<String, Object> res_deso = list.get(position);
                    });
                    break;
                case 2:
                    //这里是条码的搜索处理对象
                    // 3.初始化Provider对象,
                    BarCodeProvider listItemProvider_barcode = new BarCodeProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_barcode);
                    // 5.设置每个Item的点击事件
                    listContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
                        @Override
                        public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                            if (src_method == method){
                                //将编码放到剪贴板
                                Map<String, Object> res_barcode = list.get(i);
                                SystemPasteboard mPasteboard = SystemPasteboard.getSystemPasteboard(getContext());;
                                PasteData pasteData =  PasteData.creatPlainTextData((String)res_barcode.get("barcode"));
                                mPasteboard.setPasteData(pasteData);
                                ToastUtil.showToast(getContext(),"条码已复制  ");
                            }else{
                                //将编码放到剪贴板
                                Map<String, Object> res_name = list.get(i);
                                SystemPasteboard mPasteboard = SystemPasteboard.getSystemPasteboard(getContext());;
                                PasteData pasteData =  PasteData.creatPlainTextData((String)res_name.get("name"));
                                mPasteboard.setPasteData(pasteData);
                                ToastUtil.showToast(getContext(),"名称已复制  ");
                            }
                            return false;
                        }
                    });

                    listContainer.setItemClickedListener((container, component, position, id) -> {
                        Map<String, Object> item_desp = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                        Map<String, Object> res_deso = list.get(position);
                    });
                    break;
                case 3:
                    //这里是公司的搜索处理对象
                    // 3.初始化Provider对象,
                    CompanyProvider listItemProvider_company = new CompanyProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_company);
                    // 5.设置每个Item的点击事件
                    listContainer.setItemClickedListener((container, component, position, id) -> {
                        Map<String, Object> item_desp = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                        Map<String, Object> res_deso = list.get(position);
                    });
                    break;
                case 4:
                    //这里是标签的搜索处理对象
                    // 3.初始化Provider对象,
                    ElabelProvider listItemProvider_elabel = new ElabelProvider(list, this);
                    // 4.适配要展示的内容数据
                    listContainer.setItemProvider(listItemProvider_elabel);
                    // 5.设置每个Item的点击事件
                    listContainer.setItemClickedListener((container, component, position, id) -> {
                        Map<String, Object> item_desp = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                        Map<String, Object> res_deso = list.get(position);
                    });
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
                            }, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局

        } else {
            // 3.初始化Provider对象
            NormalProvider listItemProvider = new NormalProvider(list, this);
            // 4.适配要展示的内容数据
            listContainer.setItemProvider(listItemProvider);
            // 5.设置每个Item的点击事件
            listContainer.setItemClickedListener((container, component, position, id) -> {
                Map<String, Object> item = (Map<String, Object>) listContainer.getItemProvider().getItem(position);
                Map<String, Object> res = list.get(position);

            });


        }
    }
}
