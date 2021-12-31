package com.daqin.medicinegod.utils;


import com.daqin.medicinegod.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;

import java.util.List;
import java.util.Map;

public class ChatListItemProvider extends  BaseItemProvider{

    private List<Map<String,Object>> list;
    private AbilitySlice  slice;


    public ChatListItemProvider(List<Map<String, Object>> list, AbilitySlice slice) {
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
            cpt = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_chat_list_item,null,false);
        }else{
            cpt = convertComponent;
        }
        Map<String,Object> map =list.get(position);//获取数据
        Text textName = (Text) cpt.findComponentById(ResourceTable.Id_chat_text_username);
        Text textLastMsg = (Text) cpt.findComponentById(ResourceTable.Id_chat_text_lastmsg);
        Text textLastTime = (Text) cpt.findComponentById(ResourceTable.Id_chat_text_lasttime);
        Image image =(Image) cpt.findComponentById(ResourceTable.Id_chat_img_head);
//        textName.setMaxTextWidth(image.getWidth()/2+10);


        textName.setText((String)map.get("name"));
        textLastMsg.setText((String)map.get("lastmsg"));
        textLastTime.setText((String)map.get("lasttime"));
        image.setPixelMap((int)map.get("head"));
        return cpt;
    }




}
