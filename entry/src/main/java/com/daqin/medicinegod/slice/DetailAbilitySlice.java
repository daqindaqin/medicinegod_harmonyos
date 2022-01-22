package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.TextField;

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

        if (localID == -1 || localKEY == null || localKEY.equals("null") || mdc_SingleData == null) {
            //TODO:添加提示
            DetailAbilitySlice.super.terminate();
        }

        //TODO:调整显示内容
        System.out.println("DADADADAD" + mdc_name + "DADADAD" + (String) mdc_SingleData.get("name"));
        mdc_name.setText((String) mdc_SingleData.get("name"));
        mdc_desp.setText((String) mdc_SingleData.get("description"));
        mdc_elabel.setText((String) mdc_SingleData.get("elabel"));
        mdc_outdate.setText((String) mdc_SingleData.get("outdate"));
        mdc_otc.setText((String) mdc_SingleData.get("otc"));
        mdc_barcode.setText((String) mdc_SingleData.get("barcode"));
        mdc_usage.setText((String) mdc_SingleData.get("usage"));
        mdc_company.setText((String) mdc_SingleData.get("company"));
        mdc_yu.setText((String) mdc_SingleData.get("yu"));


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
