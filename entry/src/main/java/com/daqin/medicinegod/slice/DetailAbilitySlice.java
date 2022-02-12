package com.daqin.medicinegod.slice;

import com.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import com.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import com.daqin.medicinegod.ResourceTable;
import com.daqin.medicinegod.utils.util;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.util.ToastUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.components.element.ElementScatter;
import ohos.agp.utils.Color;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.hiviewdfx.HiLog;
import ohos.media.image.PixelMap;
import ohos.miscservices.pasteboard.PasteData;
import ohos.miscservices.pasteboard.SystemPasteboard;
import ohos.utils.net.Uri;

import java.util.*;


public class DetailAbilitySlice extends AbilitySlice {
    private static DataAbilityHelper databaseHelper;
    private static final String BASE_URI = "dataability:///com.daqin.medicinegod.MedicineDataAbility";
    private static final String DATA_PATH = "/medicine";
    private static final String DB_COLUMN_KEYID = "KEYID";
    private static final String DB_COLUMN_NAME = "NAME";
    private static final String DB_COLUMN_IMAGE = "IMAGE";
    private static final String DB_COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String DB_COLUMN_OUTDATE = "OUTDATE";
    private static final String DB_COLUMN_OTC = "OTC";
    private static final String DB_COLUMN_BARCODE = "BARCODE";
    private static final String DB_COLUMN_USAGE = "USAGE";
    private static final String DB_COLUMN_COMPANY = "COMPANY";
    private static final String DB_COLUMN_YU = "YU";
    private static final String DB_COLUMN_ELABEL = "ELABEL";
    private static final String DB_COLUMN_LOVE = "LOVE";



    private Map<String, Object> mdc_SingleData;
    private String localKEY = null;
    private String[] textUagesAll;
    Text mdc_name;
    Text mdc_desp;
    Text mdc_outdate;
    Text mdc_outdate_day;
    Text mdc_otc;
    Text mdc_barcode;
    Text mdc_usage;
    Text mdc_yu;
    Text mdc_company;
    Text mdc_elabel1;
    Text mdc_elabel2;
    Text mdc_elabel3;
    Text mdc_elabel4;
    Text mdc_elabel5;

    Image mdc_img;
    Image mdc_img_barcode;


    Image mdc_love;
    Image mdc_use;
    Image mdc_edit;
    Image mdc_share;
    Image mdc_back;

    Text mdc_delete;
    Text mdc_copy;
    String love = "no";
    @Override
    protected void onStop() {
        super.onStop();
    }

    public void iniContext() {
        byte[] img = (byte[]) mdc_SingleData.get("img");
        mdc_img.setPixelMap(util.byte2PixelMap(img));
        mdc_name.setText((String) mdc_SingleData.get("name"));
        mdc_desp.setText("        " + (String) mdc_SingleData.get("description"));
        love = (String) mdc_SingleData.get("love");
        switch (love){
            case "yes":
                mdc_love.setPixelMap(ResourceTable.Media_dtl_mdc_love_yes);
                break;
            case "no":
            default:
                mdc_love.setPixelMap(ResourceTable.Media_dtl_mdc_love_no);
                break;
        }
        String[] tmplist = mdc_SingleData.get("elabel").toString().split("@@");
        String[] otclist = new String[]{"", "", "", "", ""};
        String s = "";
        if (tmplist.length > 5) {
            System.arraycopy(tmplist, 0, otclist, 0, 5);
            s = otclist[0] + "@@" + otclist[1] + "@@" + otclist[2] + "@@" + otclist[3] + "@@" + otclist[4];
            mdc_SingleData.put("elabel", s);
        } else if (tmplist.length > 0 && tmplist.length <= 5) {
            otclist = tmplist;
        }
        Text[] Texts = {mdc_elabel1, mdc_elabel2, mdc_elabel3, mdc_elabel4, mdc_elabel5};
        for (int i = 0; i < otclist.length; i++) {
            Texts[i].setVisibility(Component.VISIBLE);
            Texts[i].setText(otclist[i]);
        }

        //过期提醒
        //示例：
        //date0  1646064000000
        //date1  2022-03-01
        Calendar cl = Calendar.getInstance();
        long date0 = (long) mdc_SingleData.get("outdate");
        String date1 = util.getStringFromDate(date0);
        int res;
        int[] res_date;
        String res_text="";

        //date1  2022-03-01 药品的时间
        //timeB  2022-01-01 现在的时间
        String timeB = cl.get(Calendar.YEAR) + "-" + (cl.get(Calendar.MONTH)+1) + "-1";
        res = util.isTimeOut(date1,timeB);

        switch (res) {
            case -1:
                mdc_outdate.setText("[药品过期]" + "\n" + "禁止服用 请妥善处理。");
                mdc_outdate.setTextColor(new Color(Color.rgb(255, 67, 54)));
                break;
            case 0:
                mdc_outdate.setText("[即将过期]" + "\n" + "请提前准备新的药品。");
                mdc_outdate.setTextColor(new Color(Color.rgb(255, 152, 0)));
                break;
            case 1:
                mdc_outdate.setText("[正常使用]" + "\n" + "请遵医嘱、说明书使用。");
                mdc_outdate.setTextColor(new Color(Color.rgb(76, 175, 80)));
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
        mdc_outdate_day.setText(res_text);


        String otc = (String) mdc_SingleData.get("otc");
        switch (otc) {
            case "none":
                mdc_otc.setText("(未填写)");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_otc_green_one));
                break;
            case "OTC-G":
                mdc_otc.setText("OTC");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_otc_green_one));
                break;
            case "OTC-R":
                mdc_otc.setText("OTC");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_otc_red_one));
                break;
            case "Rx":
                mdc_otc.setText("Rx");
                mdc_otc.setBackground(ElementScatter.getInstance(getContext()).parse(ResourceTable.Graphic_bg_text_otc_rx_one));
                break;
        }


        String barcode = (String) mdc_SingleData.get("barcode");
        mdc_barcode.setText(barcode + " (点击复制) ");
        mdc_barcode.setClickedListener(l -> {
            System.out.println("点击");
            //将编码放到剪贴板
            SystemPasteboard mPasteboard = SystemPasteboard.getSystemPasteboard(this);
            ;
            PasteData pasteData = PasteData.creatPlainTextData(barcode);
            mPasteboard.setPasteData(pasteData);
            ToastUtil.showToast(getContext(), "已复制  ");
        });


        createWidthContent(barcode);


        textUagesAll = ((String) mdc_SingleData.get("usage")).split("-");
        //1-包-3-次-1-天
        if ((Integer.parseInt(textUagesAll[4].toString())) == 1) {
            if ((Integer.parseInt(textUagesAll[4])) == 1) {
                mdc_usage.setText(textUagesAll[0] + textUagesAll[1] + "/" + textUagesAll[3] + "/" + textUagesAll[5]);
            } else {
                mdc_usage.setText(textUagesAll[0] + textUagesAll[1] + "/" + textUagesAll[3] + "/" + textUagesAll[4] + textUagesAll[5]);
            }
        } else {
            if ((Integer.parseInt(textUagesAll[4])) == 1) {
                mdc_usage.setText(textUagesAll[0] + textUagesAll[1] + "/" + textUagesAll[2] + textUagesAll[3] + "/" + textUagesAll[5]);
            } else {
                mdc_usage.setText(textUagesAll[0] + textUagesAll[1] + "/" + textUagesAll[2] + textUagesAll[3] + "/" + textUagesAll[4] + textUagesAll[5]);
            }
        }

        mdc_company.setText((String) mdc_SingleData.get("company"));

        int yuall = Integer.parseInt(mdc_SingleData.get("yu").toString());
        int yuus = Integer.parseInt(textUagesAll[0]);
        mdc_yu.setText("预计可再使用" + (String) mdc_SingleData.get("yu") + textUagesAll[1] + "后购买;\n"
                + "或再使用预计" + (yuall / yuus) + "次后购买新药品。");

        int yu = Integer.parseInt(mdc_SingleData.get("yu").toString());
        int yu_yu = (yu - Integer.parseInt(textUagesAll[0]));
        if (yu_yu<=0){
            //editok属性包括{ ok (修改完成) , none(无) }
            util.PreferenceUtils.putString(getContext(), "editok", "ok");
            mdc_yu.setText("此药品你已经没有啦！\n"
                    + "赶紧去准备一点吧~");
        }else{
            //editok属性包括{ ok (修改完成) , none(无) }
            util.PreferenceUtils.putString(getContext(), "editok", "ok");
            mdc_yu.setText("预计可再使用" + yu_yu + textUagesAll[1] + "后购买;\n"
                    + "或再使用预计" + (yu_yu / yuus) + "次后购买新药品。");
        }

    }

    ;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main_detail);
        databaseHelper = DataAbilityHelper.creator(this);
        mdc_img = (Image) findComponentById(ResourceTable.Id_dtl_mdc_img);
//        mdc_img.setCornerRadius(125);
        mdc_img_barcode = (Image) findComponentById(ResourceTable.Id_dtl_mdc_barcode_img);
        mdc_name = (Text) findComponentById(ResourceTable.Id_dtl_mdc_name);
        mdc_desp = (Text) findComponentById(ResourceTable.Id_dtl_mdc_desp);
        mdc_outdate = (Text) findComponentById(ResourceTable.Id_dtl_mdc_outdate);
        mdc_outdate_day = (Text) findComponentById(ResourceTable.Id_dtl_mdc_outdate_day);
        mdc_otc = (Text) findComponentById(ResourceTable.Id_dtl_mdc_otc);
        mdc_barcode = (Text) findComponentById(ResourceTable.Id_dtl_mdc_barcode);
        mdc_usage = (Text) findComponentById(ResourceTable.Id_dtl_mdc_usage);
        mdc_yu = (Text) findComponentById(ResourceTable.Id_dtl_mdc_yu);
        mdc_company = (Text) findComponentById(ResourceTable.Id_dtl_mdc_company);
        mdc_elabel1 = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel1);
        mdc_elabel2 = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel2);
        mdc_elabel3 = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel3);
        mdc_elabel4 = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel4);
        mdc_elabel5 = (Text) findComponentById(ResourceTable.Id_dtl_mdc_elabel5);

        mdc_delete=(Text)findComponentById(ResourceTable.Id_dtl_mdc_delete);
        mdc_delete.setClickedListener(component -> {
            //删除
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("是否删除", "        您正在进行删除" + mdc_name.getText() + "的操作，是否确认删除？\n" +
                                    "        注意：此操作不可逆！",
                            "返回", "确认删除",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    // 置换key
                                    DataAbilityPredicates predicates = new DataAbilityPredicates();
                                    predicates.equalTo(DB_COLUMN_KEYID, localKEY);
                                    String[] columns = new String[]{
                                            DB_COLUMN_KEYID,
                                            DB_COLUMN_NAME,
                                            DB_COLUMN_IMAGE,
                                            DB_COLUMN_DESCRIPTION,
                                            DB_COLUMN_OUTDATE,
                                            DB_COLUMN_OTC,
                                            DB_COLUMN_BARCODE,
                                            DB_COLUMN_USAGE,
                                            DB_COLUMN_COMPANY,
                                            DB_COLUMN_YU,
                                            DB_COLUMN_ELABEL,
                                            DB_COLUMN_LOVE
                                    };
                                    try {
                                        ResultSet resultSet = databaseHelper.query(Uri.parse(BASE_URI + DATA_PATH),
                                                columns, predicates);
                                        if (resultSet == null || resultSet.getRowCount() == 0) {
                                            ToastUtil.showToast(getContext(), "未找到该条药品信息  ");
                                        }else{
                                            util.PreferenceUtils.putString(getContext(), "editok", "ok");
                                            MainAbilitySlice.delete(localKEY);
                                            terminate();
                                        }
                                    } catch (DataAbilityRemoteException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToast(getContext(), "删除失败  ");
                                    }
                                }
                            }, null, false, ResourceTable.Layout_popup_comfirm_with_cancel_redconfirm)
                    .show(); // 最后一个参数绑定已有布局
        });
        mdc_copy=(Text)findComponentById(ResourceTable.Id_dtl_mdc_copy);
        mdc_copy.setClickedListener(component -> {
            ValuesBucket valuesBucket = new ValuesBucket();
            valuesBucket.putString(DB_COLUMN_KEYID, util.getRandomKeyId());
            valuesBucket.putString(DB_COLUMN_NAME, (String) mdc_SingleData.get("name"));
            valuesBucket.putByteArray(DB_COLUMN_IMAGE, (byte[]) mdc_SingleData.get("img"));
            valuesBucket.putString(DB_COLUMN_DESCRIPTION, (String) mdc_SingleData.get("description"));
            valuesBucket.putLong(DB_COLUMN_OUTDATE, util.getDateFromString(mdc_SingleData.get("outdate").toString()));
            valuesBucket.putString(DB_COLUMN_OTC, (String) mdc_SingleData.get("otc"));
            valuesBucket.putString(DB_COLUMN_BARCODE, (String) mdc_SingleData.get("barcode"));
            valuesBucket.putString(DB_COLUMN_USAGE, (String) mdc_SingleData.get("usage"));
            valuesBucket.putString(DB_COLUMN_COMPANY, (String) mdc_SingleData.get("company"));
            valuesBucket.putString(DB_COLUMN_YU, (String) mdc_SingleData.get("yu"));
            valuesBucket.putString(DB_COLUMN_ELABEL, (String) mdc_SingleData.get("elabel"));
            valuesBucket.putString(DB_COLUMN_LOVE, (String) mdc_SingleData.get("love"));
            try {
                if (databaseHelper.insert(Uri.parse(BASE_URI + DATA_PATH), valuesBucket) != -1) {
                    //消息弹框
                    util.PreferenceUtils.putString(getContext(), "editok", "ok");
                    new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .asConfirm("复制成功", " 您已成功复制此药品，返回主页可查看。\n",
                                    " ", "确认",null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                            .show(); // 最后一个参数绑定已有布局
                }
            } catch (DataAbilityRemoteException | IllegalStateException exception) {
                //消息弹框
                new XPopup.Builder(getContext())
//                        .setPopupCallback(new XPopupListener())
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .asConfirm("复制失败", "出现错误，请稍后重试\n"+exception,
                                " ", "确认",null, null, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                        .show(); // 最后一个参数绑定已有布局
            }

        });

        mdc_back = (Image) findComponentById(ResourceTable.Id_dtl_mdc_back);
        mdc_back.setCornerRadius(100);
        mdc_back.setClickedListener(l -> {
            terminate();
        });

        mdc_love= (Image)findComponentById(ResourceTable.Id_dtl_mdc_love);
        mdc_love.setClickedListener(component -> {
            DataAbilityPredicates predicates = new DataAbilityPredicates();
            predicates.equalTo(DB_COLUMN_KEYID, localKEY);
            ValuesBucket valuesBucket = new ValuesBucket();
            if (love.equals("no")){
                love ="yes";
            }else if (love.equals("yes")){
                love = "no";
            }
            valuesBucket.putString(DB_COLUMN_LOVE,love);
            try {
                if (databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates) != -1) {
                    switch (love){
                        case "yes":
                            mdc_love.setPixelMap(ResourceTable.Media_dtl_mdc_love_yes);
                            break;
                        case "no":
                        default:
                            mdc_love.setPixelMap(ResourceTable.Media_dtl_mdc_love_no);
                            break;
                    }
                }
            } catch (DataAbilityRemoteException | IllegalStateException exception) {
                ToastUtil.showToast(getContext(), "收藏失败  ");
            }
        });
        mdc_share= (Image)findComponentById(ResourceTable.Id_dtl_mdc_share);
        //TODO:分享
        mdc_edit= (Image)findComponentById(ResourceTable.Id_dtl_mdc_edit);
        mdc_edit.setClickedListener(component -> {
            //弹出弹框编辑后再返回
            Intent intentEdit = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")    // 设备Id，在本地上进行跳转可以为空，跨设备进行跳转则需要传入值
                    .withBundleName(getBundleName())    // 包名
                    .withAbilityName("com.daqin.medicinegod.EditAbility")
                    // Ability页面的名称，在本地可以缺省前面的路径
                    .build();    // 构建代码
            intentEdit.setOperation(operation);    // 将operation存入到intent中
            startAbility(intentEdit);    // 实现Ability跳转
        });
        mdc_use= (Image)findComponentById(ResourceTable.Id_dtl_mdc_use);
        mdc_use.setClickedListener(component -> {
            //使用药品(现实生活中的使用，可由此定位)

            DataAbilityPredicates predicates = new DataAbilityPredicates();
            predicates.equalTo(DB_COLUMN_KEYID, localKEY);
            ValuesBucket valuesBucket = new ValuesBucket();

            try {
                if (databaseHelper.update(Uri.parse(BASE_URI + DATA_PATH), valuesBucket, predicates) != -1) {
                    int yu = Integer.parseInt(mdc_SingleData.get("yu").toString());
                    int yu_yu = (yu - Integer.parseInt(textUagesAll[0]));
                    if (yu_yu<=0){
                        valuesBucket.putString(DB_COLUMN_YU, "0");
                        //editok属性包括{ ok (修改完成) , none(无) }
                        util.PreferenceUtils.putString(getContext(), "editok", "ok");
                        int yuus = Integer.parseInt(textUagesAll[0]);
                        mdc_yu.setText("此药品你已经没有啦！\n"
                                + "赶紧去准备一点吧~");
                    }else{
                        valuesBucket.putString(DB_COLUMN_YU, String.valueOf(yu_yu));
                        //editok属性包括{ ok (修改完成) , none(无) }
                        util.PreferenceUtils.putString(getContext(), "editok", "ok");
                        int yuus = Integer.parseInt(textUagesAll[0]);
                        mdc_yu.setText("预计可再使用" + yu_yu + textUagesAll[1] + "后购买;\n"
                                + "或再使用预计" + (yu_yu / yuus) + "次后购买新药品。");
                    }

                    ToastUtil.showToast(getContext(), "已记为使用一次该药品  ");
                }
            } catch (DataAbilityRemoteException | IllegalStateException exception) {
                ToastUtil.showToast(getContext(), "使用失败  ");
            }

        });


        localKEY = util.PreferenceUtils.getString(getContext(), "mglocalkey");
        mdc_SingleData = MainAbilitySlice.querySingleData(localKEY);
        if (localKEY == null || localKEY.equals("null") || mdc_SingleData == null) {
            //当不存在ID和KEY时打开了屏幕则关闭屏幕并展示弹窗信息
            new XPopup.Builder(getContext())
                    //.setPopupCallback(new XPopupListener())
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .asConfirm("错误", "药品信息不存在！或是内部发生错误！",
                            "", "好的",
                            new OnConfirmListener() {
                                @Override
                                public void onConfirm() {
                                    terminate();
                                }
                            }, new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    terminate();
                                }
                            }, false, ResourceTable.Layout_popup_comfirm_without_cancel)
                    .show(); // 最后一个参数绑定已有布局
        }

        iniContext();

    }



    //创建条码
    private void createWidthContent(String barcode) {
        new EventHandler(EventRunner.create("createWidthContent")).postTask(() -> {
            int width = BGAQRCodeUtil.dp2px(getContext(), 160);
            int height = BGAQRCodeUtil.dp2px(getContext(), 60);
//            int textSize = BGAQRCodeUtil.sp2px(getContext(), 16);
            int textSize = 0;
            PixelMap pixelMap = QRCodeEncoder.syncEncodeBarcode(barcode, width, height, textSize);
            getUITaskDispatcher().asyncDispatch(() -> {
                mdc_img_barcode.setPixelMap(pixelMap);
            });
        });
    }

    @Override
    protected void onActive() {
        super.onActive();
        String editok = util.PreferenceUtils.getString(getContext(), "editok");
        if (editok.equals("ok")) {
            mdc_SingleData = MainAbilitySlice.querySingleData(localKEY);
            iniContext();
        }
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
    }
}
