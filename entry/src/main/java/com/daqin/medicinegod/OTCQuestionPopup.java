package com.daqin.medicinegod;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import ohos.agp.components.ListContainer;
import ohos.app.Context;

import java.util.ArrayList;

/**
 * Description: 仿知乎底部评论弹窗
 * Create by dance, at 2018/12/25
 */
public class OTCQuestionPopup extends BottomPopupView {
    ListContainer listContainer;
    private ArrayList<String> data;

    public OTCQuestionPopup(Context context) {
        super(context, null);
    }

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_custom_otc_question_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getAppHeight(getContext()) * .7f);
    }
}