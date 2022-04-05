package com.daqin.medicinegod.provider.search;


import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.agp.utils.Color;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class BarCodeProvider extends BaseItemProvider {

    private List<Map<String, Object>> list;
    private AbilitySlice slice;

    public BarCodeProvider(List<Map<String, Object>> list, AbilitySlice slice) {
        this.list = list;
        this.slice = slice;
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
        final Component cpt;
        // 如果还没有convertComponent对象，那么将xml布局文件转为一个Component对象。
        if (convertComponent == null) {
            //从当前的AbilitySlice对应的xml布局中，
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_search_method_barcode, null, false);
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

        Map<String, Object> map = list.get(position);//获取数据
        Text textName = (Text) cpt.findComponentById(ResourceTable.Id_srced_name);
        Text textBarcode = (Text) cpt.findComponentById(ResourceTable.Id_srced_barcode);
        Image image = (Image) cpt.findComponentById(ResourceTable.Id_srced_image_png);
        if (map.get("keyid") != null) {
            textName.setText((String) map.get("name"));
            textBarcode.setText((String) map.get("barcode") + "(长按复制)");


            //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
            //过期提醒
            //示例：
            //date0  1646064000000
            //date1  2022-03-01
            Calendar cl = Calendar.getInstance();
            long date0 = Long.parseLong(map.get("outdate").toString());
            String date1 = util.getStringFromDate(date0);
            int res;
            //date1  2022-03-01 药品的时间
            //timeB  2022-01-01 现在的时间
            String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH) + 1) + "-1";
            res = util.isTimeOut(date1, timeB);
            switch (res) {
                case -1:
                    textName.setTextColor(new Color(Color.rgb(255, 67, 54)));
                    break;
                case 0:
                    textName.setTextColor(new Color(Color.rgb(255, 152, 0)));
                    break;
                case 1:
                    textName.setTextColor(new Color(Color.rgb(106, 104, 94)));
                    break;
            }



            image.setVisibility(Component.VISIBLE);
            byte[] img = (byte[]) map.get("img");
            if (img==null){
                image.setPixelMap(ResourceTable.Media_add_imgdefault);
                image.setScaleMode(Image.ScaleMode.CENTER);
            }else {
                image.setPixelMap(util.byte2PixelMap(img));
                image.setScaleMode(Image.ScaleMode.STRETCH);
                image.setCornerRadius(25);
            }

        } else {
            textName.setText((String)map.get("name"));
            image.setVisibility(Component.HIDE);
        }

        return cpt;
    }


}
