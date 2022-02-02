package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;


import com.daqin.medicinegod.provider.HomePageListItemProvider;
import com.daqin.medicinegod.provider.SearchListItemProvider;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.miscservices.inputmethodability.KeyboardController;

import java.util.Arrays;
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

        nameAll = util.PreferenceUtils.getString(getContext(), "nameALL").split("@@");

        randomnum = new Random().nextInt(nameAll.length);
        tf_src_src_box.setHint("要搜索\"" + nameAll[randomnum ] + "\"吗?");

    }

    private void iniClicklistener() {
        //搜索方法切换
        btn_src_method.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .maxHeight(850)
                    .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                    .asCenterList("请选择搜索条件", new String[]{"药 名", "描 述", "条 码", "公 司", "标 签", "用 量"},
                            null, src_method,
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    src_method = position;
                                    btn_src_method.setText(text + " ▼");
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
            tf_src_src_box.setText(nameAll[randomnum-1]);
        });


    }

    private void iniView() {
        btn_src_method = (Text) findComponentById(ResourceTable.Id_src_srcmethod);
        tf_src_src_box = (TextField) findComponentById(ResourceTable.Id_src_srcbox);
        btn_src = (Text) findComponentById(ResourceTable.Id_src_src);
        btn_src_back = (Text) findComponentById(ResourceTable.Id_src_back);
        btn_src_screen = (Text) findComponentById(ResourceTable.Id_src_srceen);

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
            SearchListItemProvider listItemProvider = new SearchListItemProvider(list, this);
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
