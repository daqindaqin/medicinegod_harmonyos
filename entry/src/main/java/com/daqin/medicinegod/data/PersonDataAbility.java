package com.daqin.medicinegod.data;


import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.PacMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;


public class PersonDataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "person");

    private static final String DB_NAME = "MedicineGod.db";

    private static final String DB_TAB_NAME = "PERSON";

    private static final String DB_COLUMN_ID = "ID";
    private static final String DB_COLUMN_LNAME = "LNAME";
    private static final String DB_COLUMN_SNAME = "SNAME";
    private static final String DB_COLUMN_PWD = "PWD";
    private static final String DB_COLUMN_HEAD = "HEAD";
    private static final String DB_COLUMN_FRIEND = "FRIEND";
    private static final String DB_COLUMN_PHONE = "PHONE";
    private static final String DB_COLUMN_MAIL = "MAIL";
    private static final String DB_COLUMN_RGTIME = "RGTIME";
    private static final String DB_COLUMN_ONLINE = "ONLINE";
    private static final String DB_COLUMN_HAS = "HAS";
    private static final String DB_COLUMN_VIP = "VIP";
    private static final String DB_COLUMN_VIPYU = "VIPYU";

    private static final int DB_VERSION = 1;

    private StoreConfig config = StoreConfig.newDefaultConfig(DB_NAME);

    private RdbStore rdbStore;

    private RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore store) {
            store.executeSql("create table if not exists "
                    + DB_TAB_NAME + " ("
                    + DB_COLUMN_ID + " text not null, "
                    + DB_COLUMN_LNAME + " text not null, "
                    + DB_COLUMN_SNAME + " text not null, "
                    + DB_COLUMN_PWD + " text not null, "
                    + DB_COLUMN_HEAD + " blob, "
                    + DB_COLUMN_FRIEND + " long text not null, "
                    + DB_COLUMN_PHONE + " text not null, "
                    + DB_COLUMN_MAIL + " text not null, "
                    + DB_COLUMN_RGTIME + " text not null, "
                    + DB_COLUMN_ONLINE + " text not null, "
                    + DB_COLUMN_HAS + "  long text not null, "
                    + DB_COLUMN_VIP + "  text not null, "
                    + DB_COLUMN_VIPYU + "  text not null "
                    + ")"
            );
        }

        @Override
        public void onUpgrade(RdbStore store, int oldVersion, int newVersion) {
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LABEL_LOG, "PersonDataAbility onStart");
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        rdbStore = databaseHelper.getRdbStore(config, DB_VERSION, rdbOpenCallback, null);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        ResultSet resultSet = rdbStore.query(rdbPredicates, columns);
        if (resultSet == null) {
            HiLog.info(LABEL_LOG, "resultSet is null");
        }
        return resultSet;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LABEL_LOG, "PersonDataAbility insert");
        String path = uri.getLastPath();
        if (!"person".equals(path)) {
            HiLog.info(LABEL_LOG, "DataAbility insert path is not matched");
            return -1;
        }
        ValuesBucket values = new ValuesBucket();
        values.putString(DB_COLUMN_ID, value.getString(DB_COLUMN_ID));
        values.putString(DB_COLUMN_LNAME, value.getString(DB_COLUMN_LNAME));
        values.putString(DB_COLUMN_SNAME, value.getString(DB_COLUMN_SNAME));
        values.putString(DB_COLUMN_PWD, value.getString(DB_COLUMN_PWD));
        values.putByteArray(DB_COLUMN_HEAD, value.getByteArray(DB_COLUMN_HEAD));
        values.putString(DB_COLUMN_FRIEND, value.getString(DB_COLUMN_FRIEND));
        values.putString(DB_COLUMN_PHONE, value.getString(DB_COLUMN_PHONE));
        values.putString(DB_COLUMN_MAIL, value.getString(DB_COLUMN_MAIL));
        values.putString(DB_COLUMN_RGTIME, value.getString(DB_COLUMN_RGTIME));
        values.putString(DB_COLUMN_ONLINE, value.getString(DB_COLUMN_ONLINE));
        values.putString(DB_COLUMN_HAS, value.getString(DB_COLUMN_HAS));
        values.putString(DB_COLUMN_VIP, value.getString(DB_COLUMN_VIP));
        values.putString(DB_COLUMN_VIPYU, value.getString(DB_COLUMN_VIPYU));
        int index = (int) rdbStore.insert(DB_TAB_NAME, values);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.delete(rdbPredicates);
        HiLog.info(LABEL_LOG, "delete: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.update(value, rdbPredicates);
        HiLog.info(LABEL_LOG, "update: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
