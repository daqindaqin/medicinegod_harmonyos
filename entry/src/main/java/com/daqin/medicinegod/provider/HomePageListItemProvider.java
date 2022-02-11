package com.daqin.medicinegod.provider;


import com.daqin.medicinegod.ResourceTable;

import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.AbilitySlice;


import ohos.agp.components.*;
import ohos.agp.components.element.*;
import ohos.agp.utils.Color;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HomePageListItemProvider extends BaseItemProvider {

    private List<Map<String, Object>> list;
    private AbilitySlice slice;
    private int style;

    public HomePageListItemProvider(List<Map<String, Object>> list, AbilitySlice slice, int style) {
        this.list = list;
        this.slice = slice;
        this.style = style;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if (list != null && position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Component getComponent(int position, Component convertComponent, ComponentContainer componentContainer) {
        Component cpt ;
        // 如果还没有convertComponent对象，那么将xml布局文件转为一个Component对象。
        if (convertComponent == null) {
            //从当前的AbilitySlice对应的xml布局中，
            switch (style) {
                case 1:
                    cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_homepage_style_one, null, false);
                    break;
                case 0:
                default:
                    cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_homepage_style_default, null, false);
                    break;
            }


        } else {
            cpt = convertComponent;
        }
/*
        map.put("imgpath", imagepath);
        map.put("name", name);
        map.put("description",description);
        map.put("outdate", outdate);
        map.put("otc", otc);
        map.put("barcode", barcode);
        map.put("usage", usage);
        map.put("company",company);
        map.put("yu", yu);
        map.put("elabel", elabel);
*/
        Text textName ;
        Text textUsage ;
        Text textMargin;
        Text textOtc ;
        Text textOutdate ;
        Image image ;
        Map<String, Object> map = list.get(position);//获取数据
        switch (style) {
            case 1:
                textName = (Text) cpt.findComponentById(ResourceTable.Id_style_one_text_name);
                textUsage = (Text) cpt.findComponentById(ResourceTable.Id_style_one_text_usage);
                textMargin = (Text) cpt.findComponentById(ResourceTable.Id_style_one_text_margin);
                textOtc = (Text) cpt.findComponentById(ResourceTable.Id_style_one_text_otc);
                textOutdate = (Text) cpt.findComponentById(ResourceTable.Id_style_one_text_outdate);
                image = (Image) cpt.findComponentById(ResourceTable.Id_style_one_image_png);
                textName.setText((String) map.get("name"));
                //OTC标识设置提醒
                String otc0 = (String) map.get("otc");
                switch (otc0) {
                    case "none":
                        textOtc.setVisibility(Component.HIDE);
                        break;
                    case "OTC-G":
                        textOtc.setText("OTC");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_green_one));
                        break;
                    case "OTC-R":
                        textOtc.setText("OTC");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_red_one));
                        break;
                    case "Rx":
                        textOtc.setText("Rx");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_rx_one));
                        break;
                }
                //用量提醒
                String[] textUagesAll0 = ((String) map.get("usage")).split("-");
                //String[] textUagesAll = { XX , 包/克/片 , XX , 次 , XX , 时/天 };
                if ((Integer.parseInt(textUagesAll0[4].toString())) == 1) {
                    if ((Integer.parseInt(textUagesAll0[4])) == 1) {
                        textUsage.setText(textUagesAll0[0] + textUagesAll0[1] + "/" + textUagesAll0[3] + "/" + textUagesAll0[5]);
                    } else {
                        textUsage.setText(textUagesAll0[0] + textUagesAll0[1] + "/" + textUagesAll0[3] + "/" + textUagesAll0[4] + textUagesAll0[5]);
                    }
                } else {
                    if ((Integer.parseInt(textUagesAll0[4])) == 1) {
                        textUsage.setText(textUagesAll0[0] + textUagesAll0[1] + "/" + textUagesAll0[2] + textUagesAll0[3] + "/" + textUagesAll0[5]);
                    } else {
                        textUsage.setText(textUagesAll0[0] + textUagesAll0[1] + "/" + textUagesAll0[2] + textUagesAll0[3] + "/" + textUagesAll0[4] + textUagesAll0[5]);
                    }
                }
                //余量提醒
                //剩余：yu  包/克/片
                //TODO:决定是否改成long
                int yuall = Integer.parseInt(map.get("yu").toString());
                int yuus = Integer.parseInt(textUagesAll0[0]);
                double yures = yuall / yuus;
                DecimalFormat df = new DecimalFormat("#.0");
                String yu = df.format(yures);
                textMargin.setText(yu);
                //过期提醒
                //示例：
                //date0  1646064000000
                //date1  2022-03-01
                Calendar cl = Calendar.getInstance();
                long date0 = Long.valueOf((long) map.get("outdate"));
                String date1 = util.getStringFromDate(date0);
                int res;
                //date1  2022-03-01 药品的时间
                //timeB  2022-01-01 现在的时间
                String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-1";
                res = util.isTimeOut(date1, timeB);
                switch (res) {
                    case -1:
                        textOutdate.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                        textOutdate.setTextColor(new Color(Color.rgb(255, 67, 54)));
                        break;
                    case 0:
                        textOutdate.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                        textOutdate.setTextColor(new Color(Color.rgb(255, 152, 0)));
                        break;
                    case 1:
                        textOutdate.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                        textOutdate.setTextColor(new Color(Color.rgb(76, 175, 80)));
                        break;
                }
                byte[] img0 = (byte[]) map.get("img");
                if (img0 == null) {
                    image.setPixelMap(ResourceTable.Media_addpng_default);
                    image.setScaleMode(Image.ScaleMode.CENTER);
                } else {
                    image.setPixelMap(util.byte2PixelMap(img0));
                    image.setScaleMode(Image.ScaleMode.STRETCH);
                    image.setCornerRadius(25);
                }
                break;
            case 0:
            default:
                textName = (Text) cpt.findComponentById(ResourceTable.Id_style_default_text_name);
                textUsage = (Text) cpt.findComponentById(ResourceTable.Id_style_default_text_usage);
                textOtc = (Text) cpt.findComponentById(ResourceTable.Id_style_default_text_otc);
                textOutdate = (Text) cpt.findComponentById(ResourceTable.Id_style_default_text_outdate);
                image = (Image) cpt.findComponentById(ResourceTable.Id_style_default_image_png);
                textName.setText((String) map.get("name"));
                //OTC标识设置提醒
                String otc1 = (String) map.get("otc");
                switch (otc1) {
                    case "none":
                        textOtc.setVisibility(Component.HIDE);
                        break;
                    case "OTC-G":
                        textOtc.setText("OTC");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_green_default));
                        break;
                    case "OTC-R":
                        textOtc.setText("OTC");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_red_default));
                        break;
                    case "Rx":
                        textOtc.setText("Rx");
                        textOtc.setVisibility(Component.VISIBLE);
                        textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_rx_default));
                        break;
                }
                //用量提醒
                String[] textUagesAll1 = ((String) map.get("usage")).split("-");
                //String[] textUagesAll1 = { XX , 包/克/片 , XX , 次 , XX , 时/天 };
                if ((Integer.parseInt(textUagesAll1[4].toString())) == 1) {
                    if ((Integer.parseInt(textUagesAll1[4])) == 1) {
                        textUsage.setText(textUagesAll1[0] + textUagesAll1[1] + "/" + textUagesAll1[3] + "/" + textUagesAll1[5]);
                    } else {
                        textUsage.setText(textUagesAll1[0] + textUagesAll1[1] + "/" + textUagesAll1[3] + "/" + textUagesAll1[4] + textUagesAll1[5]);
                    }
                } else {
                    if ((Integer.parseInt(textUagesAll1[4])) == 1) {
                        textUsage.setText(textUagesAll1[0] + textUagesAll1[1] + "/" + textUagesAll1[2] + textUagesAll1[3] + "/" + textUagesAll1[5]);
                    } else {
                        textUsage.setText(textUagesAll1[0] + textUagesAll1[1] + "/" + textUagesAll1[2] + textUagesAll1[3] + "/" + textUagesAll1[4] + textUagesAll1[5]);
                    }
                }
                byte[] img1 = (byte[]) map.get("img");
                if (img1 == null) {
                    image.setPixelMap(ResourceTable.Media_addpng_default);
                    image.setScaleMode(Image.ScaleMode.CENTER);
                } else {
                    image.setPixelMap(util.byte2PixelMap(img1));
                    image.setScaleMode(Image.ScaleMode.STRETCH);
                    image.setCornerRadius(25);
                }
                //过期提醒
                //示例：
                //date0  1646064000000
                //date1  2022-03-01
                Calendar cl1 = Calendar.getInstance();
                long dateC = Long.valueOf((long) map.get("outdate"));
                String dated1 = util.getStringFromDate(dateC);
                int res1;
                //date1  2022-03-01 药品的时间
                //timeB  2022-01-01 现在的时间
                String timeD = cl1.get(Calendar.YEAR) + "-" + (cl1.get(Calendar.MONTH) + 1) + "-1";
                res = util.isTimeOut(dated1, timeD);
                switch (res) {
                    case -1:
                        textOutdate.setText("药品过期");
                        textOutdate.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_outdate_red_default));
                        break;
                    case 0:
                        textOutdate.setText("即将过期");
                        textOutdate.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_outdate_yellow_default));
                        break;
                    case 1:
                        textOutdate.setText("正常使用");
                        textOutdate.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_outdate_green_default));
                        break;
                }
                break;
        }

        return cpt;
    }
}














