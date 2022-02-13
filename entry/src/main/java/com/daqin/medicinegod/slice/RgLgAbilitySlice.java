package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.provider.RgLgScreenSlidePagerProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;

import java.util.ArrayList;
import java.util.List;


public class RgLgAbilitySlice extends AbilitySlice {


    private List<Component> mPageViewList = new ArrayList<>();
    PageSlider mPageSlider;


    TextField tf_l_userlname;
    Text t_l_userlname_status;
    TextField tf_l_userpwd;
    Text t_l_userpwd_status;
    Text t_l_ok;
    Text t_l_toRegister;

    TextField tf_r_userlname;
    Text t_r_userlname_status;
    TextField tf_r_userpwd;
    Text t_r_userpwd_status;
    TextField tf_r_usermail;
    Text t_r_usermail_status;
    TextField tf_r_usermailCode;
    Text t_r_usermailCode_status;
    TextField tf_r_userphone;
    Text t_r_userphone_status;

    Text t_r_mail_sendCode;
    Text t_r_ok;
    Text t_r_toLogin;


    TextField[] tf_r = new TextField[]{}, tf_l = new TextField[]{};

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_rglg);
        initView();
        setClickerListener();

    }


    private void initView() {
        mPageSlider = (PageSlider) findComponentById(ResourceTable.Id_pager_slider);
        mPageViewList.clear();
        mPageViewList.add(LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_ability_main_register, null, false));
        mPageViewList.add(LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_ability_main_login, null, false));
        RgLgScreenSlidePagerProvider adapter = new RgLgScreenSlidePagerProvider(mPageViewList);
        mPageSlider.setProvider(adapter);
        mPageSlider.setCurrentPage(0);
        tf_r_userlname = (TextField) findComponentById(ResourceTable.Id_rg_userlname);
        t_r_userlname_status = (TextField) findComponentById(ResourceTable.Id_rg_userlname_status);
        tf_r_userpwd = (TextField) findComponentById(ResourceTable.Id_rg_userpwd);
        t_r_userpwd_status = (TextField) findComponentById(ResourceTable.Id_rg_userpwd_status);
        tf_r_usermail = (TextField) findComponentById(ResourceTable.Id_rg_usermail);
        t_r_usermail_status = (TextField) findComponentById(ResourceTable.Id_rg_usermail_status);
        tf_r_usermailCode = (TextField) findComponentById(ResourceTable.Id_rg_usermail_code);
        t_r_usermailCode_status = (TextField) findComponentById(ResourceTable.Id_rg_usermailcode_status);
        tf_r_userphone = (TextField) findComponentById(ResourceTable.Id_rg_userphone);
        t_r_userphone_status = (TextField) findComponentById(ResourceTable.Id_rg_userphone_status);
        t_r_mail_sendCode = (Text) findComponentById(ResourceTable.Id_rg_usermail_sendcode);
        t_r_ok = (Text) findComponentById(ResourceTable.Id_rg_ok);
        tf_r = new TextField[]{tf_r_userlname, tf_r_userpwd, tf_r_usermail, tf_r_usermailCode, tf_r_userphone};
        for (TextField textField : tf_r) {
            textField.setFocusChangedListener(new Component.FocusChangedListener() {
                @Override
                public void onFocusChange(Component component, boolean b) {
                    if (b) {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_foucs));
                    } else {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
                    }
                }
            });
        }
        t_r_toLogin = (Text) findComponentById(ResourceTable.Id_rg_goto);
        t_r_toLogin.setClickedListener(component -> mPageSlider.setCurrentPage(1));
        tf_l_userlname = (TextField) findComponentById(ResourceTable.Id_lg_userlname);
        t_l_userlname_status = (TextField) findComponentById(ResourceTable.Id_lg_userlname_status);
        tf_l_userpwd = (TextField) findComponentById(ResourceTable.Id_lg_userpwd);
        t_l_userpwd_status = (TextField) findComponentById(ResourceTable.Id_lg_userpwd_status);
        t_l_toRegister = (Text) findComponentById(ResourceTable.Id_lg_goto);
        t_l_toRegister.setClickedListener(component -> mPageSlider.setCurrentPage(0));
        t_l_ok = (Text) findComponentById(ResourceTable.Id_lg_ok);
        tf_l = new TextField[]{tf_l_userlname, tf_l_userpwd};
        for (TextField textField : tf_l) {
            textField.setFocusChangedListener(new Component.FocusChangedListener() {
                @Override
                public void onFocusChange(Component component, boolean b) {
                    if (b) {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield_foucs));
                    } else {
                        textField.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_rglg_textfield));
                    }
                }
            });
        }

    }

    private void setClickerListener() {

    }

    private void startRegister() {

    }

    private void startLogin() {

    }
}
