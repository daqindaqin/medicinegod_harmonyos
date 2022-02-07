package com.daqin.medicinegod.provider.search;


import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.agp.utils.Color;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class OutdateProvider extends BaseItemProvider {

    private List<Map<String, Object>> list;
    private AbilitySlice slice;

    public OutdateProvider(List<Map<String, Object>> list, AbilitySlice slice) {
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
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_search_method_outdate, null, false);
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
        Text textOutdate = (Text) cpt.findComponentById(ResourceTable.Id_srced_outdate);
        Image image = (Image) cpt.findComponentById(ResourceTable.Id_srced_image_png);
        if (map.get("keyid") != null) {



            textName.setText((String) map.get("name"));

            //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
            //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
            //过期提醒
            //示例：
            //date0  1646064000000
            //date1  2022-03-01
            Calendar cl = Calendar.getInstance();
            long date0 = Long.parseLong(map.get("outdate").toString());
            String res_text,date1 = util.getStringFromDate(date0);
            int res;
            int[] res_date;
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
            res_date = util.getRemainTime(date1, timeB);//返回包含结束日期的数组（年月天时分秒）
//        System.out.println(res + "输出了" + date1 + "-" + timeB + "-" + Arrays.toString(res_date));
//        System.out.println(res_date[0]+" "+res_date[1]+" "+res_date[2]+" "+res_date[3]+" "+res_date[4]+" "+res_date[5]);
            res_text = "药品将于" + ((res_date[0] == 0) ? "" : res_date[0] + "年");
            res_text = res_text + ((res_date[1] == 0) ? "" : res_date[1] + "个月");
            res_text = res_text + ((res_date[2] == 0) ? "" : res_date[2] + "天") + "后过期";
//        res_text = res_text + ((res_date[3]==0)?"":res_date[3]+"时");
//        res_text = res_text + ((res_date[4]==0)?"":res_date[4]+"分");
//        res_text = res_text + ((res_date[5]==0)?"":res_date[5]+"秒");
//        System.out.println(res_text);
            if (Arrays.toString(res_date).equals("[0, 0, 0, 0, 0, 0]")) {
                res_text = "请注意：药品已过期！！！";
            } else if (res_text.equals("药品将于后过期")) {
                //数组出错就不管了
                res_text = "";
            }
            textOutdate.setText(res_text);

//        DataAbilityHelper helper = DataAbilityHelper.creator(slice.getContext());
//        //定义文件
//        FileDescriptor file = null;
//        try {
//            file = helper.openFile(Uri.parse((String)map.get("imagepath")), "r");
//        } catch (DataAbilityRemoteException | FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        //创建文件对象
//        ImageSource imageSource = ImageSource.create(file, null);
//        //创建位图
//        PixelMap pixelMap = imageSource.createPixelmap(null);
//        image.setPixelMap(pixelMap);
//        本机资源ID可使用下方命令
            image.setVisibility(Component.VISIBLE);
            image.setPixelMap(ResourceTable.Media_test);
            image.setCornerRadius(5);
//        image.setPixelMap((int)map.get("image"));

        } else {
            textName.setText((String)map.get("name"));
            image.setVisibility(Component.HIDE);

        }

        return cpt;
    }


}
