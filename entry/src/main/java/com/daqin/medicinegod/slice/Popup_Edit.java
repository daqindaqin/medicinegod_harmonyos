package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import ohos.aafwk.ability.Ability;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.app.Context;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 */
public class Popup_Edit extends FullScreenPopupView {
    Text edit_back;
    Text edit_ok;

    Image edit_img;
    boolean imgchanged = false;
    TextField edit_name;
    TextField edit_desp;
    Picker edit_outdate_year;
    Picker edit_outdate_month;
    Picker edit_otc;

    TextField edit_usage_total;
    TextField edit_usage_time;
    TextField edit_usage_day;
    TextField edit_barcode;
    TextField edit_yu;
    TextField edit_company;
    Text edit_usage_u1;
    Text edit_usage_u2;
    Text edit_usage_u3;
    Text edit_yu_title;
    Text edit_elabel1;
    Text edit_elabel2;
    Text edit_elabel3;
    Text edit_elabel4;
    Text edit_elabel5;
    Text edit_elabel_add1;
    Text edit_elabel_add2;
    Text edit_elabel_title;
    Map<String, Object> mgDdata;
    String keyid;
    List<String> elabel = new ArrayList<>();
    Text[] elabelview;
    TextField[] textFieldlist;
    int countElabel = 0;
    int newUsage_utils_1 = 0, newUsage_utils_3 = 0;

    public Popup_Edit(Context context) {
        super(context, null);
    }

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_popup_edit;
    }

    public void iniView() {
        edit_back = (Text) findComponentById(ResourceTable.Id_dtl_edit_back);
        edit_ok = (Text) findComponentById(ResourceTable.Id_dtl_edit_editok);
        edit_img = (Image) findComponentById(ResourceTable.Id_dtl_edit_img);
        edit_img.setCornerRadius(25);
        edit_name = (TextField) findComponentById(ResourceTable.Id_dtl_edit_name);
        //TODO:完善这里
        edit_usage_total = (TextField) findComponentById(ResourceTable.Id_dtl_edit_newUsage_1);
        edit_usage_time = (TextField) findComponentById(ResourceTable.Id_dtl_edit_newUsage_2);
        edit_usage_day = (TextField) findComponentById(ResourceTable.Id_dtl_edit_newUsage_3);
        edit_usage_u1 = (Text) findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_1);
        edit_usage_u2 = (Text) findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_2);
        edit_usage_u3 = (Text) findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_3);

        edit_desp = (TextField) findComponentById(ResourceTable.Id_dtl_edit_desp);

        edit_outdate_year = (Picker) findComponentById(ResourceTable.Id_dtl_edit_newOutdate_year);
        edit_outdate_month = (Picker) findComponentById(ResourceTable.Id_dtl_edit_newOutdate_month);
        edit_otc = (Picker) findComponentById(ResourceTable.Id_dtl_edit_newOtc);
        edit_barcode = (TextField) findComponentById(ResourceTable.Id_dtl_edit_barcode);
        edit_yu = (TextField) findComponentById(ResourceTable.Id_dtl_edit_yu);
        edit_yu_title = (Text) findComponentById(ResourceTable.Id_dtl_edit_yu_title);
        edit_company = (TextField) findComponentById(ResourceTable.Id_dtl_edit_company);
        edit_elabel_add1 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel_add1);
        edit_elabel_add2 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel_add2);

        edit_elabel_title = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel_title);
        edit_elabel1 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel1);
        edit_elabel2 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel2);
        edit_elabel3 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel3);
        edit_elabel4 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel4);
        edit_elabel5 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel5);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        keyid = util.PreferenceUtils.getString(getContext(), "mglocalkey");
        mgDdata = MainAbilitySlice.querySingleData(keyid);
        if (keyid == null || keyid.equals("null") || mgDdata == null) {
            //当不存在ID和KEY时打开了屏幕则关闭屏幕并展示弹窗信息
            dismiss();
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(true)
                    .dismissOnBackPressed(true)
                    .isDestroyOnDismiss(true)
                    .asConfirm("错误", "药品信息不存在！或是内部发生错误！",
                            "", "好的",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                }
                            }, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        }
        iniView();
        intclicklistener();
        iniCalendarPicker();
        textFieldlist = new TextField[]{edit_name, edit_desp, edit_usage_total, edit_usage_time, edit_usage_day, edit_company, edit_yu, edit_barcode};

        edit_name.setHint((String) mgDdata.get("name"));
        edit_desp.setHint((String) mgDdata.get("description"));
        edit_barcode.setHint((String) mgDdata.get("barcode"));
        String[] usage = ((String) mgDdata.get("usage")).split("-");
        edit_usage_total.setHint(usage[0]);
        edit_usage_u1.setText(usage[1]);
        edit_usage_time.setHint(usage[2]);
        edit_usage_u2.setText(usage[3]);
        edit_usage_day.setHint(usage[4]);
        edit_usage_u3.setText(usage[5]);
        edit_yu.setHint((String) mgDdata.get("yu"));
        edit_company.setHint((String) mgDdata.get("company"));
        elabel.addAll(Arrays.asList(mgDdata.get("elabel").toString().split("@@")));
        countElabel = elabel.size();
        System.out.println("数组" + elabel);
        elabelview = new Text[]{edit_elabel1, edit_elabel2, edit_elabel3, edit_elabel4, edit_elabel5};
        //刷新标签显示
        refreshElabel();
        System.out.println("数组" + elabel);

    }

    private void intclicklistener() {
        edit_ok.setClickedListener(component -> {


            //TODO:更改完后点这里提交

            String text="";
            text = (edit_name.length() == 0 ? (edit_name.getHint().substring(0,4) + "...     =>     -  \n") : (edit_name.getHint().substring(0,4) + "     =>     " + edit_name.getText().substring(0,4)+"...\n"));
            text = text + (edit_desp.length() == 0 ? (edit_desp.getHint().substring(0,4) + "...     =>     -  \n") : (edit_desp.getHint().substring(0,4) + "...     =>     " + edit_desp.getText().substring(0,4)+"..."+"\n"));

            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("更改确认", "        您本地做出了以下更改，请您再次确认：\n\n     类型   =>   更改后内容\n" + text,
                            "返回", "确认修改", null, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_blueconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        edit_back.setClickedListener(component -> dismiss());
        edit_elabel_add1.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .hasStatusBarShadow(true)
                    .isDestroyOnDismiss(true)
                    .autoOpenSoftInput(true)
                    .isDarkTheme(false)
                    .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                    .asInputConfirm("写入药品标签", null, null, "1-4字符",
                            new OnInputConfirmListener() {
                                @Override
                                public void onConfirm(String text) {
                                    elabelClickAdd(text);
                                }
                            })
                    .show();
        });
        edit_elabel_add2.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .hasStatusBarShadow(true)
                    .isDestroyOnDismiss(true)
                    .autoOpenSoftInput(true)
                    .isDarkTheme(false)
                    .setComponent(component) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                    .asInputConfirm("写入药品标签", null, null, "1-4字符",
                            new OnInputConfirmListener() {
                                @Override
                                public void onConfirm(String text) {
                                    elabelClickAdd(text);
                                }
                            })
                    .show();
        });
        edit_elabel1.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .isDarkTheme(false)
                    .asCenterList("选择你的操作", new String[]{"编辑", "删除"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    elabelClickOther(position, edit_elabel1.getText());
                                }
                            })
                    .show();
        });
        edit_elabel2.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .isDarkTheme(false)
                    .asCenterList("选择你的操作", new String[]{"编辑", "删除"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    elabelClickOther(position, edit_elabel2.getText());
                                }
                            })
                    .show();
        });
        edit_elabel3.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .isDarkTheme(false)
                    .asCenterList("选择你的操作", new String[]{"编辑", "删除"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    elabelClickOther(position, edit_elabel3.getText());
                                }
                            })
                    .show();
        });
        edit_elabel4.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .isDarkTheme(false)
                    .asCenterList("选择你的操作", new String[]{"编辑", "删除"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    elabelClickOther(position, edit_elabel4.getText());
                                }
                            })
                    .show();
        });
        edit_elabel5.setClickedListener(component -> {
            new XPopup.Builder(getContext())
                    .isDestroyOnDismiss(true)
                    .isDarkTheme(false)
                    .asCenterList("选择你的操作", new String[]{"编辑", "删除"},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    elabelClickOther(position, edit_elabel5.getText());
                                }
                            })
                    .show();
        });


        edit_usage_u1.setClickedListener(component -> {
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1) {
                case 1:
                    edit_usage_u1.setText("克");
                    edit_yu_title.setText("剩余余量(单位:克)");
                    break;
                case 2:
                    edit_usage_u1.setText("包");
                    edit_yu_title.setText("剩余余量(单位:包)");
                    break;
                case 3:
                    edit_usage_u1.setText("片");
                    edit_yu_title.setText("剩余余量(单位:片)");
                    newUsage_utils_1 = 0;
                    break;
            }
        });
        edit_usage_u3.setClickedListener(component -> {
            newUsage_utils_3 += 1;
            switch (newUsage_utils_3) {
                case 1:
                    edit_usage_u3.setText("时");
                    break;
                case 2:
                    edit_usage_u3.setText("天");
                    newUsage_utils_3 = 0;
                    break;
            }
        });

    }

    public void elabelClickAdd(String text) {
        if (countElabel >= 5) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("数量受限", "已达到标签最大数量(5)",
                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
            edit_elabel_add1.setVisibility(HIDE);
            edit_elabel_add2.setVisibility(HIDE);
        } else if (elabel.contains(text.trim())) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("标签受限", "您只能添加同一种标签一次",
                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else if (text.trim().length() == 0 || text.trim().length() >= 5) {
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("格式受限", "您在一个标签内只能添加1到4个中文字符且不能为空",
                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        } else if (text.trim().length() > 0 && text.trim().length() < 5) {
            elabel.remove("测试标签");
            elabel.add(text.trim());
            refreshElabel();
        }

    }

    public void elabelClickOther(int position, String textInLabel) {
        //0是编辑1是删除
        switch (position) {
            case 0:
                //弹窗
                new XPopup.Builder(getContext())
                        .hasStatusBarShadow(true)
                        .isDestroyOnDismiss(true)
                        .autoOpenSoftInput(true)
                        .isDarkTheme(false)
                        .setComponent(edit_elabel_title) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .asInputConfirm("编辑标签(1-4个字符)", null, null, textInLabel,
                                new OnInputConfirmListener() {
                                    @Override
                                    public void onConfirm(String text) {
                                        if (text.trim().length() == 0 || text.trim().length() >= 5) {
                                            new XPopup.Builder(getContext())
                                                    //.setPopupCallback(new XPopupListener())
                                                    .dismissOnTouchOutside(false)
                                                    .dismissOnBackPressed(false)
                                                    .isDestroyOnDismiss(true)
                                                    .asConfirm("格式受限", "您在一个标签内只能添加1到4个中文字符且不能为空",
                                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                                                    .show(); // 最后一个参数绑定已有布局
                                        } else if (elabel.contains(text.trim())) {
                                            new XPopup.Builder(getContext())
                                                    //.setPopupCallback(new XPopupListener())
                                                    .dismissOnTouchOutside(false)
                                                    .dismissOnBackPressed(false)
                                                    .isDestroyOnDismiss(true)
                                                    .asConfirm("标签受限", "您只能添加同一种标签一次",
                                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                                                    .show(); // 最后一个参数绑定已有布局
                                        } else if (text.trim().length() > 0 && text.trim().length() < 5) {
                                            elabel.set(elabel.indexOf(textInLabel), text.trim());
                                            refreshElabel();
                                        }
                                    }
                                })
                        .show();
                break;
            case 1:
                //弹窗
                new XPopup.Builder(getContext())
                        //.setPopupCallback(new XPopupListener())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .asConfirm("删除确认", "是否删除？",
                                "返回", "删除", new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        if (countElabel == 1) {
                                            new XPopup.Builder(getContext())
                                                    //.setPopupCallback(new XPopupListener())
                                                    .dismissOnTouchOutside(false)
                                                    .dismissOnBackPressed(false)
                                                    .isDestroyOnDismiss(true)
                                                    .asConfirm("数量受限", "最少需要保留(1)个标签",
                                                            " ", "好", null, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                                                    .show(); // 最后一个参数绑定已有布局
                                        } else {
                                            elabel.remove(textInLabel);
                                            elabel.add("测试标签");
                                            refreshElabel();
                                        }
                                    }
                                }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                        .show(); // 最后一个参数绑定已有布局


                break;
        }
    }

    public void refreshElabel() {
        //始终保持5个以防操作时出现错误
        countElabel = 0;
        //小于5则补足5，大于5则删除至5
        for (Text t : elabelview) {
            t.setText("测试标签");
            t.setVisibility(HIDE);
        }
        if (elabel.size() < 5) {
            for (int i = 0; i < 5 - elabel.size(); i++) {
                elabel.add("测试标签");
            }
        } else if (elabel.size() > 5) {
            for (String s : elabel) {
                if (s.equals("测试标签")) {
                    elabel.remove("测试标签");
                }
            }
            //删除多余的之后如果还大于5，截取前5个
            if (elabel.size() > 5) {
                for (int i = 5; i < elabel.size(); i++) {
                    elabel.remove(elabel.get(i));
                }
                //小了的话加上，把持5个
            } else if (elabel.size() < 5) {
                for (int i = 0; i < 5 - elabel.size(); i++) {
                    elabel.add("测试标签");
                }
            }
        }
        //把'测试标签'的标识全部集中到最后

        System.out.println("之前数组" + elabel);
        int count = 0;
        for (int i = 0; i < elabel.size(); i++) {
            if (elabel.get(i).equals("测试标签")) {
                elabel.remove("测试标签");
                count++;
            }

        }
        for (int i = 0; i < count; i++) {
            elabel.add("测试标签");
        }
        System.out.println(count + "之后数组" + elabel);

        //设置显示
        for (int i = 0; i < elabel.size(); i++) {
            if (!elabel.get(i).equals("测试标签")) {
                elabelview[i].setVisibility(VISIBLE);
                elabelview[i].setText(elabel.get(i));
                countElabel++;
            }
        }
        edit_elabel_title.setText("药品标签(" + countElabel + "/5)");
        //设置添加按钮的显示
        if (countElabel < 3) {
            edit_elabel_add1.setVisibility(VISIBLE);
            edit_elabel_add2.setVisibility(HIDE);
        } else if (countElabel == 3 || countElabel == 4) {
            edit_elabel_add1.setVisibility(HIDE);
            edit_elabel_add2.setVisibility(VISIBLE);
        } else if (countElabel == 5) {
            edit_elabel_add1.setVisibility(HIDE);
            edit_elabel_add2.setVisibility(HIDE);
        }
        System.out.println("数组" + elabel);

    }

    //定义选择器
    private void iniCalendarPicker() {
        String[] dateAll = ((String) mgDdata.get("outdate")).split("-");
        String year, month, otc;
        year = dateAll[0];
        month = dateAll[1];
        int value = 0;
        Calendar cal = Calendar.getInstance();
        List<String> yearList = new ArrayList<>();
        int year_now = cal.get(Calendar.YEAR);
        for (int i = year_now; i <= year_now + 9; i++) {
            yearList.add(i + "年");
        }
        for (int i = 0; i < 10; i++) {
            if (yearList.get(i).equals(year)) {
                value = i;
            }
        }
        edit_outdate_year.setDisplayedData(yearList.toArray(new String[]{}));
        edit_outdate_year.setValue(value);

        String[] monthList = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        for (int i = 0; i < monthList.length; i++) {
            if (monthList[i].equals(month)) {
                value = i;
            }
        }
        edit_outdate_month.setDisplayedData(monthList);
        edit_outdate_month.setValue(value);

        String[] otcList = new String[]{"OTC(非处方药)-红", "OTC(非处方药)-绿", "(留空)", "RX(处方药)"};
        otc = (String) mgDdata.get("otc");
        switch (otc) {
            case "none":
                value = 2;
                break;
            case "OTC-G":
                value = 1;
                break;
            case "OTC-R":
                value = 0;
                break;
            case "Rx":
                value = 3;
                break;
        }
        edit_otc.setDisplayedData(otcList);
        edit_otc.setValue(value);


    }
}
