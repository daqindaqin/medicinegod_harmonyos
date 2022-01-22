package com.daqin.medicinegod.utils;


import com.daqin.medicinegod.ResourceTable;

import ohos.aafwk.ability.AbilitySlice;


import ohos.agp.components.*;
import ohos.agp.components.element.*;
import ohos.agp.utils.Color;


import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HomePageListItemProvider extends  BaseItemProvider{

    private List<Map<String,Object>> list;
    private AbilitySlice  slice;

    public HomePageListItemProvider(List<Map<String, Object>> list, AbilitySlice slice) {
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
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_list_item_homepage,null,false);
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

        Text textName = (Text) cpt.findComponentById(ResourceTable.Id_text_name);
        Text textUsage = (Text)cpt.findComponentById(ResourceTable.Id_text_usage);
        Text textMargin = (Text) cpt.findComponentById(ResourceTable.Id_text_margin);
        Text textOtc = (Text) cpt.findComponentById(ResourceTable.Id_text_otc);
        Text textOutdate = (Text) cpt.findComponentById(ResourceTable.Id_text_outdate);
        Image image =(Image) cpt.findComponentById(ResourceTable.Id_image_png);
//        textName.setMaxTextWidth(image.getWidth()/2+10);


        textName.setText((String)map.get("name"));
        //OTC标识设置提醒
        String otc = (String)map.get("otc");
        switch (otc){
            case "OTC-G":
                textOtc.setText("OTC");
                textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_green));
                break;
            case "OTC-R":
                textOtc.setText("OTC");
                textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_otc_red));
                break;
            case "Rx":
                textOtc.setText("Rx");
                textOtc.setBackground(ElementScatter.getInstance(slice).parse(ResourceTable.Graphic_bg_text_otc_rx));
                break;
        }

        //用量提醒
        String[] textUagesAll = ((String)map.get("usage")).split("-");
        //String[] textUagesAll = { XX , 包/克/片 , XX , 次 , XX , 时/天 };
        if ((Integer.parseInt(textUagesAll[4].toString())) == 1 ){
            if ((Integer.parseInt(textUagesAll[4])) == 1 ){
                textUsage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[3]+"/"+textUagesAll[5]);
            }else{
                textUsage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[3]+"/"+textUagesAll[4]+textUagesAll[5]);
            }
        }else {
            if ((Integer.parseInt(textUagesAll[4])) == 1 ){
                textUsage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[2]+textUagesAll[3]+"/"+textUagesAll[5]);
            }else{
                textUsage.setText(textUagesAll[0]+textUagesAll[1]+"/"+textUagesAll[2]+textUagesAll[3]+"/"+textUagesAll[4]+textUagesAll[5]);
            }
        }

        //余量提醒
        //剩余：yu  包/克/片
        int yuall = Integer.parseInt(map.get("yu").toString());
        int yuus = Integer.parseInt(textUagesAll[0]);
        double yures = yuall / yuus;
        //TODO：修复bug
        DecimalFormat df = new DecimalFormat("#.0");
        String yu = df.format(yures);
        textMargin.setText(yu);


        //过期提醒
        //XXXX年X月
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
                textOutdate.setText("[药品过期]"+"\n"+"禁止服用 请妥善处理。");
                textOutdate.setTextColor(new Color(Color.rgb(255,67,54)));
                break;
            case 0:
                textOutdate.setText("[即将过期]"+"\n"+"请提前准备新的药品。");
                textOutdate.setTextColor(new Color(Color.rgb(255,152,0)));
                break;
            case 1:
                textOutdate.setText("[正常使用]"+"\n"+"请遵医嘱、说明书使用。");
                textOutdate.setTextColor(new Color(Color.rgb(76,175,80)));
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
//        image.setPixelMap((int)map.get("image"));

        return cpt;
    }




}
