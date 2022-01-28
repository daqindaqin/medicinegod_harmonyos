package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import ohos.aafwk.ability.Ability;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.app.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 */
public class Popup_Edit extends FullScreenPopupView {
    Text edit_back ;
    Text edit_ok ;

    Image edit_img ;
    boolean imgchanged = false;
    TextField edit_name ;
    TextField edit_desp ;
    Picker edit_outdate_year ;
    Picker edit_outdate_month ;
    Picker edit_otc ;

    TextField edit_usage_total ;
    TextField edit_usage_time ;
    TextField edit_usage_day ;
    TextField edit_barcode ;
    TextField edit_yu ;
    TextField edit_company ;
    Text edit_usage_u1;
    Text edit_usage_u2;
    Text edit_usage_u3;
    Text edit_yu_title ;
    Text edit_elabel1 ;
    Text edit_elabel2 ;
    Text edit_elabel3 ;
    Text edit_elabel4 ;
    Text edit_elabel5 ;
    Text edit_elabel_add1;
    Text edit_elabel_add2;
    Map<String, Object> mgDdata ;
    String keyid ;
    String[] elabel;
    Text[] elabelview;
    TextField[] textFieldlist;

    int newUsage_utils_1 = 0,newUsage_utils_3 = 0;

    public Popup_Edit(Context context) {
        super(context, null);
    }

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_popup_edit;
    }

    public void iniView(){
        edit_back = (Text) findComponentById(ResourceTable.Id_dtl_edit_back);
        edit_ok = (Text) findComponentById(ResourceTable.Id_dtl_edit_editok);
        edit_img = (Image)findComponentById(ResourceTable.Id_dtl_edit_img);
        edit_img.setCornerRadius(25);
        edit_name = (TextField) findComponentById(ResourceTable.Id_dtl_edit_name);
        //TODO:完善这里
        edit_usage_total = (TextField)findComponentById(ResourceTable.Id_dtl_edit_newUsage_1);
        edit_usage_time = (TextField)findComponentById(ResourceTable.Id_dtl_edit_newUsage_2);
        edit_usage_day = (TextField)findComponentById(ResourceTable.Id_dtl_edit_newUsage_3);
        edit_usage_u1 = (Text)findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_1);
        edit_usage_u2 = (Text)findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_2);
        edit_usage_u3= (Text)findComponentById(ResourceTable.Id_dtl_edit_newUsage_utils_3);

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


        edit_elabel1 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel1);
        edit_elabel2= (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel2);
        edit_elabel3= (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel3);
        edit_elabel4 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel4);
        edit_elabel5 = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel5);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        keyid = util.PreferenceUtils.getString(getContext(),"mglocalkey");
        mgDdata = MainAbilitySlice.querySingleData(keyid);
        if (keyid == null || keyid.equals("null") || mgDdata == null ) {
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
        textFieldlist = new TextField[]{edit_name,edit_desp,edit_usage_total,edit_usage_time,edit_usage_day,edit_company,edit_yu,edit_barcode};

        edit_name.setHint((String)mgDdata.get("name"));
        edit_desp.setHint((String)mgDdata.get("description"));
        edit_barcode.setHint((String)mgDdata.get("barcode"));
        String[] usage =  ((String) mgDdata.get("usage")).split("-");
        edit_usage_total.setHint(usage[0]);
        edit_usage_u1.setText(usage[1]);
        edit_usage_time.setHint(usage[2]);
        edit_usage_u2.setText(usage[3]);
        edit_usage_day.setHint(usage[4]);
        edit_usage_u3.setText(usage[5]);
        edit_yu.setHint((String) mgDdata.get("yu"));
        edit_company.setHint((String) mgDdata.get("company"));
        elabel = mgDdata.get("elabel").toString().split("@@");
        elabelview = new Text[]{edit_elabel1,edit_elabel2,edit_elabel3,edit_elabel4,edit_elabel5};
        for (int i = 0;i<elabel.length;i++){
            elabelview[i].setVisibility(VISIBLE);
            elabelview[i].setText(elabel[i]);
        }
        if (elabelview.length<3){
            edit_elabel_add1.setVisibility(VISIBLE);
            edit_elabel_add2.setVisibility(HIDE);
        }else if ((elabelview.length == 3) || (elabelview.length == 4)){
            edit_elabel_add1.setVisibility(HIDE);
            edit_elabel_add2.setVisibility(VISIBLE);
        }
    }

    private void intclicklistener() {
        edit_ok.setClickedListener(component -> {
            //TODO:更改完后点这里提交
        });
        edit_back.setClickedListener(component -> dismiss());

        edit_usage_u1.setClickedListener(component -> {
            newUsage_utils_1 += 1;
            switch (newUsage_utils_1){
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
            switch (newUsage_utils_3){
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

    //定义选择器
    private void iniCalendarPicker() {
        String[] dateAll =   ((String)mgDdata.get("outdate")).split("-");
        String year,month,otc;
        year = dateAll[0];
        month = dateAll[1];
        int value = 0;
        Calendar cal = Calendar.getInstance();
        List<String> yearList = new ArrayList<>();
        int year_now = cal.get(Calendar.YEAR);
        for (int i = year_now; i <= year_now+9 ; i++) {
            yearList.add(i +"年");
        }
        for (int i = 0; i<10;i++){
            if (yearList.get(i).equals(year)){
                value = i;
            }
        }
        edit_outdate_year.setDisplayedData(yearList.toArray(new String[]{}));
        edit_outdate_year.setValue(value);

        String[] monthList = new String[]{"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        for (int i = 0; i < monthList.length ; i++) {
            if (monthList[i].equals(month)){
                value = i;
            }
        }
        edit_outdate_month.setDisplayedData(monthList);
        edit_outdate_month.setValue(value);

        String[] otcList = new String[]{"OTC(非处方药)-红","OTC(非处方药)-绿","(留空)","RX(处方药)"};
        otc = (String)mgDdata.get("otc");
        switch (otc){
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
