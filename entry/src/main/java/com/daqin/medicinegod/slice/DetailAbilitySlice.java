package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;


public class DetailAbilitySlice extends AbilitySlice {
    private Map<String,Object> mdc_SingleData;
    private int localID = 0;
    private String localKEY = null;

    TextField mdc_name ;
    TextField mdc_desp ;
    TextField mdc_outdate ;
    TextField mdc_otc ;
    TextField mdc_barcode ;
    TextField mdc_usage ;
    TextField mdc_yu ;
    TextField mdc_company ;
    TextField mdc_elabel ;

    Image mdc_img ;

    Text mdc_more ;
    Text mdc_back;


    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_detail);
        mdc_img = (Image)findComponentById(ResourceTable.Id_dtl_mdc_img);
        mdc_name = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_name);
        mdc_desp = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_desp);
        mdc_outdate = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_outdate);
        mdc_otc = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_otc);
        mdc_barcode = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_barcode);
        mdc_usage = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_usage);
        mdc_yu = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_yu);
        mdc_company = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_company);
        mdc_elabel = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel);

        mdc_back = (Text) findComponentById(ResourceTable.Id_dtl_mdc_back);
        mdc_back.setClickedListener(l->terminate());
        mdc_more = (Text) findComponentById(ResourceTable.Id_dtl_mdc_more);
        mdc_more.setClickedListener(l->{
            new XPopup.Builder(getContext())
                    .hasShadowBg(true)
                    .isDestroyOnDismiss(true) // 对于只使用一次的弹窗，推荐设置这个
                    .atView(mdc_more)  // 依附于所点击的Commonent，内部会自动判断在上方或者下方显示
                    .isComponentMode(true, mdc_more) // Component实现模式
                    .asAttachList(new String[]{"  使 用  ","  编 辑  ","  分 享  ", "  复 制  ", "  删 除  "},
                            new int[]{ResourceTable.Media_dtl_mdc_use,
                                    ResourceTable.Media_dtl_mdc_edit,
                                    ResourceTable.Media_dtl_mdc_share,
                                    ResourceTable.Media_dtl_mdc_copy,
                                    ResourceTable.Media_dtl_mdc_delete},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    popupClick(position);
                                }
                            }, 0, 0).show();
        });





        localID = util.PreferenceUtils.getInt(getContext(), "mglocalid");
        localKEY = util.PreferenceUtils.getString(getContext(), "mglocalkey");
        mdc_SingleData = MainAbilitySlice.querySingleData(localKEY);

        if (localID == -1 || localKEY == null || localKEY.equals("null") || mdc_SingleData == null ) {
            //当不存在ID和KEY时打开了屏幕则关闭屏幕并展示弹窗信息
            DetailAbilitySlice.super.terminate();
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(true)
                    .dismissOnBackPressed(true)
                    .isDestroyOnDismiss(true)
                    .asConfirm("错误", "不存在的药品",
                            "", "好的",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                }
                            }, null, false, ResourceTable.Layout_popup_comfrim_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        }

        //TODO:调整显示内容
        System.out.println("DADADAD" +  mdc_SingleData);
        mdc_name.setText((String) mdc_SingleData.get("name"));
        mdc_desp.setText("    "+(String) mdc_SingleData.get("description"));

        //TODO：背景板添加类似 OTC样子的色块
        mdc_elabel.setText((String) mdc_SingleData.get("elabel"));

        //过期提醒
        //XXXX年X月
        Calendar cl = Calendar.getInstance();
        String[] outdateAll =  ((String)mdc_SingleData.get("outdate")).split("-");
        int outyear,outmonth,res;
        outyear = Integer.parseInt(outdateAll[0].replace("年",""));
        outmonth = Integer.parseInt(outdateAll[1].replace("月",""));
        //timeA  2020-1 过期的时间
        //timeB  2022-1 现在的时间
        String timeA = outyear + "-" + outmonth + "-1";
        String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH)+1) + "-1";
        res = util.isTimeOut(timeA,timeB);
        //TODO:添加倒计时
        switch (res){
            case -1:
                mdc_outdate.setText("[药品过期]"+"\n"+"禁止服用 请妥善处理。");
                mdc_outdate.setTextColor(new Color(Color.rgb(255,67,54)));
                break;
            case 0:
                mdc_outdate.setText("[即将过期]"+"\n"+"请提前准备新的药品。");
                mdc_outdate.setTextColor(new Color(Color.rgb(255,152,0)));
                break;
            case 1:
                mdc_outdate.setText("[正常使用]"+"\n"+"请遵医嘱、说明书使用。");
                mdc_outdate.setTextColor(new Color(Color.rgb(76,175,80)));
                break;
        }

        String otc = (String)mdc_SingleData.get("otc");
        switch (otc){
            case "OTC-G":
                mdc_otc.setText("OTC");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_otc_green));
                break;
            case "OTC-R":
                mdc_otc.setText("OTC");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_otc_red));
                break;
            case "Rx":
                mdc_otc.setText("Rx");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_rx));
                break;
        }

        mdc_barcode.setText((String) mdc_SingleData.get("barcode"));

        String[] textUagesAll =  ((String) mdc_SingleData.get("usage")).split("-");
        //1-包-3-次-1-天
        if ((Integer.parseInt(textUagesAll[4].toString())) == 1 ){
            if ((Integer.parseInt(textUagesAll[4])) == 1 ){
                mdc_usage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[3]+"/"+textUagesAll[5]);
            }else{
                mdc_usage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[3]+"/"+textUagesAll[4]+textUagesAll[5]);
            }
        }else {
            if ((Integer.parseInt(textUagesAll[4])) == 1 ){
                mdc_usage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[2]+textUagesAll[3]+"/"+textUagesAll[5]);
            }else{
                mdc_usage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[2]+textUagesAll[3]+"/"+textUagesAll[4]+textUagesAll[5]);
            }
        }

        mdc_company.setText((String) mdc_SingleData.get("company"));

        int yuall = Integer.parseInt(mdc_SingleData.get("yu").toString());
        int yuus = Integer.parseInt(textUagesAll[0]);
        double yures = yuall / yuus;
        //TODO：修复bug
        DecimalFormat df = new DecimalFormat("#.#");
        String yu = df.format(yures);
        mdc_yu.setText("预计可再使用"+(String) mdc_SingleData.get("yu")+textUagesAll[1]+"后购买;\n"
                + "或再使用预计"+yu+"次后购买新药品。");


    }
    public static void popupClick(int position){
        switch (position){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }

    }

}
