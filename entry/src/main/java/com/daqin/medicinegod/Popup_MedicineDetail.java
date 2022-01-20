package com.daqin.medicinegod;

import com.daqin.medicinegod.slice.MainAbilitySlice;
import com.daqin.medicinegod.utils.PersonDataAbility;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.preferences.Preferences;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.global.icu.text.UnicodeSet;
import ohos.global.icu.text.UnicodeSetSpanner;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

import java.util.*;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 *
 */
public class Popup_MedicineDetail extends FullScreenPopupView {
    public Popup_MedicineDetail(Context context) {
        super(context, null);
    }

    private Map<String,Object> mdc_SingleData;
    private int localID = 0;
    private String localKEY = null;

    Text mdc_name ;
    Text mdc_desp ;
    Text mdc_outdate ;
    Text mdc_otc ;
    Text mdc_barcode ;
    Text mdc_usage ;
    Text mdc_yu ;
    Text mdc_company ;
    Text mdc_elabel ;

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_popup_medicine_detail;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        mdc_name = (Text) findComponentById(ResourceTable.Id_dtl_mdc_name);
        mdc_desp = (Text) findComponentById(ResourceTable.Id_dtl_mdc_desp);
        mdc_outdate = (Text) findComponentById(ResourceTable.Id_dtl_mdc_outdate);
        mdc_otc = (Text) findComponentById(ResourceTable.Id_dtl_mdc_otc);
        mdc_barcode = (Text) findComponentById(ResourceTable.Id_dtl_mdc_barcode);
        mdc_usage = (Text) findComponentById(ResourceTable.Id_dtl_mdc_usage);
        mdc_yu = (Text) findComponentById(ResourceTable.Id_dtl_mdc_yu);
        mdc_company = (Text) findComponentById(ResourceTable.Id_dtl_mdc_company);
        mdc_elabel = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel);
        localID = util.PreferenceUtils.getInt(getContext(), "mglocalid");
        localKEY = util.PreferenceUtils.getString(getContext(), "mglocalkey");
        if (localID == -1 || localKEY == null || localKEY.equals("null")) {
            //TODO:添加提示
            Popup_MedicineDetail.super.dismiss();
        }
        initListener(LayoutScatter.getInstance(getContext()).parse(getImplLayoutId(), this, true));
        //设置滑动监听，仿滑到关闭动画
        ScrollView s = (ScrollView) findComponentById(ResourceTable.Id_detail_scrollview);
        s.setScrolledListener(new ScrolledListener() {
            @Override
            public void onContentScrolled(Component component, int i, int i1, int i2, int i3) {
                if (i1 <= -350) {
                    Popup_MedicineDetail.super.dismiss();
                }
            }
        });
        s.setClickedListener(new ClickedListener() {
            @Override
            public void onClick(Component component) {
                Popup_MedicineDetail.super.dismiss();
            }
        });
        mdc_SingleData = MainAbilitySlice.querySingleData(localKEY);
        if (mdc_SingleData == null) {
            //TODO:添加提示
            Popup_MedicineDetail.super.dismiss();
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





    public void initListener(final Component com) {
        com.findComponentById(ResourceTable.Id_dtl_mdc_take).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_share).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_delete).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_name).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_img).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_desp).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_outdate).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_otc).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_barcode).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_usage).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_yu).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_company).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_elabel).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_name_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_desp_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_outdate_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_otc_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_barcode_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_usage_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_yu_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_company_edit).setClickedListener(this::onClick);
        com.findComponentById(ResourceTable.Id_dtl_mdc_elabel_edit).setClickedListener(this::onClick);
    }


    public void onClick(Component com){
        switch (com.getId()) {
            case ResourceTable.Id_dtl_mdc_take:
                break;
            case ResourceTable.Id_dtl_mdc_share:
                break;
            case ResourceTable.Id_dtl_mdc_delete:
                break;
            case ResourceTable.Id_dtl_mdc_img:
                break;
            case ResourceTable.Id_dtl_mdc_name_edit:
                break;
            case ResourceTable.Id_dtl_mdc_desp_edit:
                break;
            case ResourceTable.Id_dtl_mdc_outdate_edit:
                break;
            case ResourceTable.Id_dtl_mdc_otc_edit:
                break;
            case ResourceTable.Id_dtl_mdc_barcode_edit:
                break;
            case ResourceTable.Id_dtl_mdc_usage_edit:
                break;
            case ResourceTable.Id_dtl_mdc_yu_edit:
                break;
            case ResourceTable.Id_dtl_mdc_company_edit:
                break;
            case ResourceTable.Id_dtl_mdc_elabel_edit:
                break;
            case ResourceTable.Id_dtl_mdc_name:
            case ResourceTable.Id_dtl_mdc_desp:
            case ResourceTable.Id_dtl_mdc_outdate:
            case ResourceTable.Id_dtl_mdc_otc:
            case ResourceTable.Id_dtl_mdc_barcode:
            case ResourceTable.Id_dtl_mdc_usage:
            case ResourceTable.Id_dtl_mdc_yu:
            case ResourceTable.Id_dtl_mdc_company:
            case ResourceTable.Id_dtl_mdc_elabel:

                break;








        }
    }


}
