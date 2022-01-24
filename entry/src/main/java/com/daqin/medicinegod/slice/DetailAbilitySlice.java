package com.daqin.medicinegod.slice;

import com.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import com.bingoogolapple.qrcode.zxing.QRCodeEncoder;
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
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.media.image.PixelMap;

import java.text.DecimalFormat;
import java.util.*;


public class DetailAbilitySlice extends AbilitySlice {
    private Map<String,Object> mdc_SingleData;
    private int localID = 0;
    private String localKEY = null;



    TextField mdc_name ;
    TextField mdc_desp ;
    TextField mdc_outdate ;
    TextField mdc_outdate_day ;
    TextField mdc_otc ;
    TextField mdc_barcode ;
    TextField mdc_usage ;
    TextField mdc_yu ;
    TextField mdc_company ;
    TextField mdc_elabel1 ;
    TextField mdc_elabel2 ;
    TextField mdc_elabel3 ;
    TextField mdc_elabel4 ;
    TextField mdc_elabel5 ;


    Image mdc_img ;
    Image mdc_img_barcode ;

    Text mdc_more ;
    Text mdc_back;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_detail);

        mdc_img = (Image)findComponentById(ResourceTable.Id_dtl_mdc_img);
        mdc_img.setCornerRadius(25);
        mdc_img_barcode = (Image)findComponentById(ResourceTable.Id_dtl_mdc_barcode_img);
        mdc_name = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_name);
        mdc_desp = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_desp);
        mdc_outdate = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_outdate);
        mdc_outdate_day = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_outdate_day);
        mdc_otc = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_otc);
        mdc_barcode = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_barcode);
        mdc_usage = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_usage);
        mdc_yu = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_yu);
        mdc_company = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_company);
        mdc_elabel1 = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel1);
        mdc_elabel2 = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel2);
        mdc_elabel3 = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel3);
        mdc_elabel4 = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel4);
        mdc_elabel5 = (TextField) findComponentById(ResourceTable.Id_dtl_mdc_elabel5);

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


        System.out.println("DADADAD" +  mdc_SingleData);
        mdc_name.setText((String) mdc_SingleData.get("name"));
        mdc_desp.setText("        "+(String) mdc_SingleData.get("description"));

        //TODO：背景板添加类似 OTC样子的色块
        mdc_elabel1.setText((String) mdc_SingleData.get("elabel"));

        //过期提醒
        //XXXX年X月
        Calendar cl = Calendar.getInstance();
        cl.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
        String[] outdateAll =  ((String)mdc_SingleData.get("outdate")).split("-");
        int outyear,outmonth,res_out;
        int[] res_date;
        outyear = Integer.parseInt(outdateAll[0].replace("年",""));
        outmonth = Integer.parseInt(outdateAll[1].replace("月",""));
        String timeA,timeB,res_text = "";
        //timeA  2020-1 药品的时间
        //timeB  2022-1 现在的时间
        timeA = outyear + "-" + outmonth + "-1";
        timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH)+1) + "-"+cl.get(Calendar.DAY_OF_MONTH);
        res_out = util.isTimeOut(timeA,timeB);//返回是否过期
        switch (res_out){
            case -1:
                mdc_outdate.setText("[药品过期]"+"\n"+"禁止服用 请妥善处理。" );
                mdc_outdate.setTextColor(new Color(Color.rgb(255,67,54)));
                break;
            case 0:
                mdc_outdate.setText("[即将过期]"+"\n"+"请提前准备新的药品。" );
                mdc_outdate.setTextColor(new Color(Color.rgb(255,152,0)));
                break;
            case 1:
                mdc_outdate.setText("[正常使用]"+"\n"+"请遵医嘱、说明书使用。" );
                mdc_outdate.setTextColor(new Color(Color.rgb(76,175,80)));
                break;
        }
        res_date = util.getRemainTime(timeA,timeB);//返回包含结束日期的数组（年月天时分秒）
//        System.out.println(res_date[0]+" "+res_date[1]+" "+res_date[2]+" "+res_date[3]+" "+res_date[4]+" "+res_date[5]);
        res_text = "药品将于" + ((res_date[0]==0)?"":res_date[0]+"年");
        res_text = res_text + ((res_date[1]==0)?"":res_date[1]+"月");
        res_text = res_text + ((res_date[2]==0)?"":res_date[2]+"天") + "后过期";
//        res_text = res_text + ((res_date[3]==0)?"":res_date[3]+"时");
//        res_text = res_text + ((res_date[4]==0)?"":res_date[4]+"分");
//        res_text = res_text + ((res_date[5]==0)?"":res_date[5]+"秒");
//        System.out.println(res_text);
        if (res_text.equals("药品将于后过期")){
            //数组出错就不管了
            res_text = "";
        }
        mdc_outdate_day.setText(res_text);


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




        String barcode = (String) mdc_SingleData.get("barcode");
        mdc_barcode.setText(barcode + " (点击复制) ");

        createWidthContent(barcode);


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


    //创建条码
    private void createWidthContent(String barcode) {
        new EventHandler(EventRunner.create("createWidthContent")).postTask(() -> {
            int width = BGAQRCodeUtil.dp2px(getContext(), 160);
            int height = BGAQRCodeUtil.dp2px(getContext(), 60);
//            int textSize = BGAQRCodeUtil.sp2px(getContext(), 16);
            int textSize = 0;
            PixelMap pixelMap = QRCodeEncoder.syncEncodeBarcode(barcode, width, height, textSize);
            getUITaskDispatcher().asyncDispatch(() -> {
                mdc_img_barcode.setPixelMap(pixelMap);
            });
        });
    }

}
