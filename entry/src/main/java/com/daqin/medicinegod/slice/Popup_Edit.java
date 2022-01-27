package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.impl.FullScreenPopupView;
import ohos.aafwk.ability.Ability;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.app.Context;

import java.util.Map;

/**
 * Description: 自定义全屏弹窗
 * Create by lxj, at 2019/3/12
 */
public class Popup_Edit extends FullScreenPopupView {
    Text edit_back ;
    Text edit_ok ;

    Image edit_img ;
    Text edit_name ;
    Text edit_desp ;
    Text edit_outdate ;
    Text edit_otc ;
    Text edit_barcode ;
    Text edit_usage ;
    Text edit_yu ;
    Text edit_company ;
    Text edit_elabel ;
    Map<String, Object> mgDdata ;
    String keyid ;

    public Popup_Edit(Context context) {
        super(context, null);
    }

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_popup_edit;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        keyid = util.PreferenceUtils.getString(getContext(),"editid");
        mgDdata = MainAbilitySlice.querySingleData(keyid);
        edit_back = (Text) findComponentById(ResourceTable.Id_dtl_edit_back);
        edit_ok = (Text) findComponentById(ResourceTable.Id_dtl_edit_editok);
        edit_ok.setClickedListener(component -> {
            //TODO:更改完后点这里

        });
        edit_back.setClickedListener(component -> dismiss());
        edit_img = (Image)findComponentById(ResourceTable.Id_dtl_edit_img);
        edit_img.setCornerRadius(25);
        edit_name = (Text) findComponentById(ResourceTable.Id_dtl_edit_name);
        edit_name.setHint((String)mgDdata.get("name"));
        //TODO:完善这里
        edit_desp = (Text) findComponentById(ResourceTable.Id_dtl_edit_desp);
        edit_desp.setHint((String) mgDdata.get("description"));
        edit_outdate = (Text) findComponentById(ResourceTable.Id_dtl_edit_outdate);
        edit_otc = (Text) findComponentById(ResourceTable.Id_dtl_edit_otc);
        edit_barcode = (Text) findComponentById(ResourceTable.Id_dtl_edit_barcode);
        edit_usage = (Text) findComponentById(ResourceTable.Id_dtl_edit_usage);
        edit_yu = (Text) findComponentById(ResourceTable.Id_dtl_edit_yu);
        edit_company = (Text) findComponentById(ResourceTable.Id_dtl_edit_company);
        edit_elabel = (Text) findComponentById(ResourceTable.Id_dtl_edit_elabel);



    }
}
