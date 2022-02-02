package com.daqin.medicinegod.provider;


import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class SearchListItemProvider extends  BaseItemProvider{

    private List<Map<String,Object>> list;
    private AbilitySlice  slice;

    public SearchListItemProvider(List<Map<String, Object>> list, AbilitySlice slice) {
        this.list = list;
        this.slice = slice;
    }

    @Override
    public int getCount() {
        return list == null?0: list.size();//一般返回数据源的长度
    }

    @Override
    public Object getItem(int position) {
        if(list!= null && position >= 0 && position < list.size()){
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
        if(convertComponent == null){
            //从当前的AbilitySlice对应的xml布局中，
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_search_normal,null,false);
        }else{
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

        Map<String,Object> map =list.get(position);//获取数据

        Text textName = (Text) cpt.findComponentById(ResourceTable.Id_srced_name);
        Image image =(Image) cpt.findComponentById(ResourceTable.Id_srced_image_png);


        textName.setText((String)map.get("name"));
        //过期提醒,红色过期，黑色正常，黄色临期，蓝色搜索到的内容
        Calendar cl = Calendar.getInstance();
        String[] outdateAll =  ((String)map.get("outdate")).split("-");
        int outyear,outmonth,res;
        outyear = Integer.parseInt(outdateAll[0].replace("年",""));
        outmonth = Integer.parseInt(outdateAll[1].replace("月",""));
        //timeA  2020-1 过期的时间
        //timeB  2022-1 现在的时间
        String timeA = outyear + "-" + outmonth + "-1";
        String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH)+1) + "-1";
        res = util.isTimeOut(timeA,timeB);
        switch (res){
            case -1:
                textName.setTextColor(new Color(Color.rgb(255,67,54)));
                break;
            case 0:
                textName.setTextColor(new Color(Color.rgb(255,152,0)));
                break;
            case 1:
                textName.setTextColor(new Color(Color.rgb(106,104,94)));
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
