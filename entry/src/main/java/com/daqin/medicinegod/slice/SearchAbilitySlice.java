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
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
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
    private int src_screen_again = 0;
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
            //如果选择了则添加清空当前条件 的选项
            if (src_screen != 0) {
                new XPopup.Builder(getContext())
                        .maxHeight(900)
                        .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                        .asCenterList("请选择筛选条件", new String[]{"清空当前条件", "再以\"过期时间\"为条件筛选", "再以\"药品余量\"为条件筛选", "再以\"药品类型\"为条件筛选", "再以\"药品标签\"为条件筛选"},
                                null, src_screen,
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        //如果没选择【清空当前条件】
                                        if (position != 0) {
                                            //如果二次条件没选中，则这次选的是二次条件
                                            if (src_screen_again==0){
                                                src_screen_again = position;
                                                btn_src_screen.setText(btn_src_screen.getText().substring(btn_src_screen.getText().indexOf("\"")+1,btn_src_screen.getText().lastIndexOf("\""))+"+"+text.substring(text.indexOf("\"")+1,text.lastIndexOf("\"")) + " ▼");
                                                btn_src_method.setVisibility(Component.HIDE);
                                            }else {
                                                //最大两个条件
                                                ToastUtil.showToast(getContext(),"已经达到最大的筛选条件  ");
                                            }
                                        } else {
                                            //选择了那就依次跳回
                                            if (src_screen_again!=0){
                                                src_screen_again = 0;
                                                btn_src_screen.setText("以\""+btn_src_screen.getText().substring(0,btn_src_screen.getText().lastIndexOf("+"))+"\"为条件筛选");

                                            }else{
                                                src_screen = 0;
                                                src_screen_again = 0;
                                                src_screen_method = null;
                                                btn_src_screen.setText("筛 选 ▼");
                                                btn_src_method.setVisibility(Component.VISIBLE);
                                            }


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
                                        src_screen_method = "";
                                        switch (src_screen){
                                            case 1:
                                                new XPopup.Builder(getContext())
                                                        .isDarkTheme(false)
                                                        .dismissOnBackPressed(false)
                                                        .dismissOnTouchOutside(false)
                                                        .asCenterList("请选择一项", new String[]{"早于...", "在...与...间", "晚于..."},
                                                                new OnSelectListener() {
                                                                    @Override
                                                                    public void onSelect(int position, String text) {
                                                                        switch (position){
                                                                            case 0:
                                                                                Calendar cal = Calendar.getInstance();
                                                                                List<String> yearList = new ArrayList<>();
                                                                                int year_now = cal.get(Calendar.YEAR);
                                                                                for (int i = year_now-3;i<year_now;i++){
                                                                                    yearList.add(i +"年");
                                                                                }
                                                                                for (int i = year_now; i <= year_now+5 ; i++) {
                                                                                    yearList.add(i +"年");
                                                                                }
                                                                                new XPopup.Builder(getContext())
                                                                                        .isDarkTheme(false)
                                                                                        .dismissOnBackPressed(false)
                                                                                        .dismissOnTouchOutside(false)
                                                                                        .asCenterList("早于...(年份)", yearList.toArray(new String[]{}),
                                                                                                new OnSelectListener() {
                                                                                                    @Override
                                                                                                    public void onSelect(int position, String text) {
                                                                                                        src_screen_method = text.replace("年","");
                                                                                                        new XPopup.Builder(getContext())
                                                                                                                .isDarkTheme(false)
                                                                                                                .dismissOnBackPressed(false)
                                                                                                                .dismissOnTouchOutside(false)
                                                                                                                .asCenterList("早于"+text+"的(月份)", new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"},
                                                                                                                        new OnSelectListener() {
                                                                                                                            @Override
                                                                                                                            public void onSelect(int position, String text) {
                                                                                                                                src_screen_method = src_screen_method+"-"+text.replace("月","")+"-1";
                                                                                                                                System.out.println("输出了"+src_screen_method);
                                                                                                                                searchScreen(src_screen,DB_COLUMN_OUTDATE,util.getDateFromString(src_screen_method));
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
                                                break;
                                        }
                                    }
                                })
                        .show();
            }

        });
        //点击返回
        btn_src_back.setClickedListener(component -> terminate());
        //搜索方法
        btn_src.setClickedListener(component -> {
            //src_screen是筛选变量，当筛选条件存在时，不进行目标性搜索
            //src_screen = 0时说明未进行筛选
            if (src_screen!=0){
                switch (src_screen){
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
            }else{
                switch (src_method){
                    case 0:
                        searchAssign(src_method,DB_COLUMN_NAME,tf_src_src_box.getText().trim());
                        break;
                    case 1:
                        searchAssign(src_method,DB_COLUMN_DESCRIPTION,tf_src_src_box.getText().trim());
                        break;
                    case 2:
                        searchAssign(src_method,DB_COLUMN_BARCODE,tf_src_src_box.getText().trim());
                        break;
                    case 3:
                        searchAssign(src_method,DB_COLUMN_COMPANY,tf_src_src_box.getText().trim());
                        break;
                    case 4:
                        searchAssign(src_method,DB_COLUMN_ELABEL,tf_src_src_box.getText().trim());
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
    private void searchScreen(int method,String field,long value) {
        //1.获取xml布局中的ListContainer组件
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_src_list);
        // 2.实例化数据源
        List<Map<String, Object>> list = MainAbilitySlice.queryScreenData(field, value);
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
                    break;

            }



        }
    }
    //指定搜索
    private void searchAssign(int method,String field,String value) {
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
                            }, false, ResourceTable.Layout_popup_comfrim_without_cancel)
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
                util.PreferenceUtils.putString(this,"mglocalkey", res.getOrDefault("keyid",null).toString());
                //弹出弹框查看详情后再返回
                Intent intentDeital = new Intent();
                Operation operation = new Intent.OperationBuilder()
                        .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                        .withBundleName(getBundleName())    // 包名
                        .withAbilityName("com.daqin.medicinegod.DetailAbility")
                        // Ability页面的名称，在本地可以缺省前面的路径
                        .build();    // 构建代码
                intentDeital.setOperation(operation);    // 将operation存入到intent中
                startAbilityForResult(intentDeital,999);    // 实现Ability跳转
            });
            listContainer.setItemLongClickedListener(new ListContainer.ItemLongClickedListener() {
                @Override
                public boolean onItemLongClicked(ListContainer listContainer, Component component, int i, long l) {
                    if (src_method == 2){
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


        }
    }

    @Override
    protected void onResult(int requestCode, Intent resultIntent) {
        super.onResult(requestCode, resultIntent);
        switch (requestCode){
            case 999:
                String[] confrimDeleteFromDetail = resultIntent.getStringArrayParam("confirmDelete");
                String keyid;
                keyid = confrimDeleteFromDetail[1];
                //确认就删除
                //confrimDelete = { "chancel" , null } 无操作
                //confrimDelete = { "confirm" , keyid } 删除此条key指向的药品
                Intent intent = new Intent();
                intent.setParam("confirmDelete",new String[]{"confirm",keyid});
                getAbility().setResult(200,intent);
                terminate();
        }
    }

    @Override
    protected void onActive() {
        super.onActive();
        //editok属性包括{ ok (修改完成) , none(无) }
        String editok = util.PreferenceUtils.getString(getContext(),"editok");
        if (editok.equals("ok")){
            initSearchListContainer();
        }
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
    }
}
