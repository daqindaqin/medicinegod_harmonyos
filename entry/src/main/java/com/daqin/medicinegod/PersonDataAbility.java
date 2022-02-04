package com.daqin.medicinegod;


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
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "allmedicine");

    private static final String DB_NAME = "allmedicine.db";

    private static final String DB_TAB_NAME = "MEDICINE";

    private static final String DB_COLUMN_KEYID = "KEYID";
    private static final String DB_COLUMN_NAME = "NAME";
    private static final String DB_COLUMN_IMAGEPATH = "IMAGEPATH";
    private static final String DB_COLUMN_DESCRIPTION = "DESCRIPTION";
    private static final String DB_COLUMN_OUTDATE = "OUTDATE";
    private static final String DB_COLUMN_OTC = "OTC";
    private static final String DB_COLUMN_BARCODE = "BARCODE";
    private static final String DB_COLUMN_USAGE = "USAGE";
    private static final String DB_COLUMN_COMPANY = "COMPANY";
    private static final String DB_COLUMN_YU = "YU";
    private static final String DB_COLUMN_ELABEL = "ELABEL";

    private static final int DB_VERSION = 1;

    private StoreConfig config = StoreConfig.newDefaultConfig(DB_NAME);

    private RdbStore rdbStore;

    private RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore store) {
            store.executeSql("create table if not exists "
                    + DB_TAB_NAME + " ("
                    + DB_COLUMN_KEYID + " text not null, "
                    + DB_COLUMN_NAME + " text not null, "
                    + DB_COLUMN_IMAGEPATH + " long text not null, "
                    + DB_COLUMN_DESCRIPTION + " long text not null, "
                    + DB_COLUMN_OUTDATE + " long int not null, "
                    + DB_COLUMN_OTC + " text not null, "
                    + DB_COLUMN_BARCODE + " text not null, "
                    + DB_COLUMN_USAGE + " text not null, "
                    + DB_COLUMN_COMPANY + " text not null, "
                    + DB_COLUMN_YU + " text not null, "
                    + DB_COLUMN_ELABEL + "  long text not null "
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
        if (!"mg".equals(path)) {
            HiLog.info(LABEL_LOG, "DataAbility insert path is not matched");
            return -1;
        }
        ValuesBucket values = new ValuesBucket();
        values.putString(DB_COLUMN_KEYID, value.getString(DB_COLUMN_KEYID));
        values.putString(DB_COLUMN_NAME, value.getString(DB_COLUMN_NAME));
        values.putString(DB_COLUMN_IMAGEPATH, value.getString(DB_COLUMN_IMAGEPATH));
        values.putString(DB_COLUMN_DESCRIPTION, value.getString(DB_COLUMN_DESCRIPTION));
        values.putLong(DB_COLUMN_OUTDATE, value.getLong(DB_COLUMN_OUTDATE));
        values.putString(DB_COLUMN_OTC, value.getString(DB_COLUMN_OTC));
        values.putString(DB_COLUMN_BARCODE, value.getString(DB_COLUMN_BARCODE));
        values.putString(DB_COLUMN_USAGE, value.getString(DB_COLUMN_USAGE));
        values.putString(DB_COLUMN_COMPANY, value.getString(DB_COLUMN_COMPANY));
        values.putString(DB_COLUMN_YU, value.getString(DB_COLUMN_YU));
        values.putString(DB_COLUMN_ELABEL, value.getString(DB_COLUMN_ELABEL));

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
