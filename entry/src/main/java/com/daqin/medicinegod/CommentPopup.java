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
public class CommentPopup extends BottomPopupView {
    ListContainer listContainer;
    private ArrayList<String> data;

    public CommentPopup(Context context) {
        super(context, null);
    }

    @Override
    protected int getImplLayoutId() {
        return ResourceTable.Layout_custom_bottom_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        /*
        findComponentById(ResourceTable.Id_tv_temp).setClickedListener(new ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 弹出新的弹窗用来输入
                final CustomEditTextBottomPopup textBottomPopup = new CustomEditTextBottomPopup(getContext());
                new XPopup.Builder(getContext())
                        .autoOpenSoftInput(true)
                        .setComponent(ZhihuCommentPopup.this) // 用于获取页面根容器，监听页面高度变化，解决输入法盖住弹窗的问题
                        .setPopupCallback(new SimpleCallback() {
                            @Override
                            public void onShow(BasePopupView popupView) {
                            }

                            @Override
                            public void onDismiss(BasePopupView popupView) {
                                String comment = textBottomPopup.getComment();
                                if (!comment.isEmpty()) {
                                    data.add(0, comment);
                                    commonAdapter.notifyDataChanged();
                                }
                            }
                        })
                        .asCustom(textBottomPopup)
                        .show();
            }
        });

        listContainer.setItemClickedListener(new ListContainer.ItemClickedListener() {
            @Override
            public void onItemClicked(ListContainer listContainer, Component component, int position, long id) {
                // 可以等消失动画执行完毕再开启新界面
                dismissWith(new Runnable() {
                    @Override
                    public void run() {
                        Intent secondIntent = new Intent();
                        Operation operation = new Intent.OperationBuilder()
                                .withBundleName(getContext().getBundleName())
                                .withAbilityName(DemoAbility.class.getName())
                                .build();
                        secondIntent.setOperation(operation);
                        getContext().startAbility(secondIntent, 0);
                    }
                });
            }
        });
        listContainer.setItemProvider(commonAdapter);
        */
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getAppHeight(getContext()) * .7f);
    }
}