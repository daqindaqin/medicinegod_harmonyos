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

public class ElabelProvider extends BaseItemProvider {

    private List<Map<String, Object>> list;
    private AbilitySlice slice;

    public ElabelProvider(List<Map<String, Object>> list, AbilitySlice slice) {
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
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_search_method_elabel, null, false);
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
        Text lbael1 = (Text) cpt.findComponentById(ResourceTable.Id_srced_elabel1);
        Text lbael2 = (Text) cpt.findComponentById(ResourceTable.Id_srced_elabel2);
        Text lbael3 = (Text) cpt.findComponentById(ResourceTable.Id_srced_elabel3);
        Text lbael4 = (Text) cpt.findComponentById(ResourceTable.Id_srced_elabel4);
        Text lbael5 = (Text) cpt.findComponentById(ResourceTable.Id_srced_elabel5);
        Image image = (Image) cpt.findComponentById(ResourceTable.Id_srced_image_png);
        Text[] allLabelView = new Text[]{lbael1,lbael2,lbael3,lbael4,lbael5};
        System.out.println("奥利给"+Arrays.toString(allLabelView));
        String[] allLabel = map.get("elabel").toString().split("@@");
        for (Text text : allLabelView){
            text.setVisibility(Component.HIDE);
        }
        textName.setText((String) map.get("name"));
        for (int i=0;i<allLabel.length;i++){
            if (!allLabel[i].equals("测试标签")){
                allLabelView[i].setText(allLabel[i]);
                allLabelView[i].setVisibility(Component.VISIBLE);
            }

        }
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
        String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH)+1) + "-1";
        res = util.isTimeOut(date1,timeB);
        switch (res){
            case -1:
                textName.setTextColor(new Color(Color.rgb(255,67,54)));
                break;
            case 0:
                textName.setTextColor(new Color(Color.rgb(255,152,0)));
                break;
            case 1:
                textName.setTextColor(new Color(Color.rgb(106, 104, 94)));
                break;
        }



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
        image.setPixelMap(ResourceTable.Media_test);
        image.setCornerRadius(5);
//        image.setPixelMap((int)map.get("image"));

        return cpt;
    }


}
