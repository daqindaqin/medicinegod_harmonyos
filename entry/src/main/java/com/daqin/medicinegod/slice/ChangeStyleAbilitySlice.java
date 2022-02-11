package com.daqin.medicinegod.slice;

import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;

public class ChangeStyleAbilitySlice extends AbilitySlice {
    DirectionalLayout default_layout;
    Text default_title;
    Image default_img;
    DirectionalLayout one_layout;
    Text one_title;
    Image one_img;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_changestyle);
        default_layout=(DirectionalLayout)findComponentById(ResourceTable.Id_sytle_default_layout);
        default_title=(Text)findComponentById(ResourceTable.Id_sytle_default_title);
        default_img =(Image)findComponentById(ResourceTable.Id_sytle_default_img);

        one_layout=(DirectionalLayout)findComponentById(ResourceTable.Id_sytle_one_layout);
        one_title=(Text)findComponentById(ResourceTable.Id_sytle_one_title);
        one_img =(Image)findComponentById(ResourceTable.Id_sytle_one_img);

        Text back =(Text)findComponentById(ResourceTable.Id_cg_mdc_back);
        int style = util.PreferenceUtils.getInt(getContext(), "style");
        back.setClickedListener(component -> {
            Intent intent1 = new Intent();
            intent1.setParam("changeok","ok");
            getAbility().setResult(300,intent1);
            terminate();
        });
        switch (style){
            case 1:
                one_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_choose));
                one_title.setTextColor(new Color(Color.rgb(198,140,208)));
                one_title.setTextSize(100);
                one_img.setPixelMap(ResourceTable.Media_style_one_choose);
                default_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_normal));
                default_title.setTextColor(new Color(Color.rgb(165,165,165)));
                default_title.setTextSize(50);
                default_img.setPixelMap(ResourceTable.Media_style_default);
                break;
            case 0:
            default:
                default_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_choose));
                default_title.setTextColor(new Color(Color.rgb(198,140,208)));
                default_title.setTextSize(100);
                default_img.setPixelMap(ResourceTable.Media_style_default_choose);
                util.PreferenceUtils.putInt(getContext(),"style",0);
                one_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_normal));
                one_title.setTextColor(new Color(Color.rgb(165,165,165)));
                one_title.setTextSize(70);
                one_img.setPixelMap(ResourceTable.Media_style_one);
                break;
        }
        default_img.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                default_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_choose));
                default_title.setTextColor(new Color(Color.rgb(198,140,208)));
                default_title.setTextSize(100);
                default_img.setPixelMap(ResourceTable.Media_style_default_choose);
                util.PreferenceUtils.putInt(getContext(),"style",0);
                one_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_normal));
                one_title.setTextColor(new Color(Color.rgb(165,165,165)));
                one_title.setTextSize(70);
                one_img.setPixelMap(ResourceTable.Media_style_one);


            }
        });
        one_img.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                one_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_choose));
                one_title.setTextColor(new Color(Color.rgb(198,140,208)));
                one_title.setTextSize(100);
                one_img.setPixelMap(ResourceTable.Media_style_one_choose);
                util.PreferenceUtils.putInt(getContext(),"style",1);
                default_layout.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_changestyle_normal));
                default_title.setTextColor(new Color(Color.rgb(165,165,165)));
                default_title.setTextSize(50);
                default_img.setPixelMap(ResourceTable.Media_style_default);
            }
        });

    }

}
