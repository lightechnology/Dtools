package org.adol.tdm.dtools;

import android.app.Application;

import org.adol.tdm.dtools.util.ComParams;
import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

/**
 * Created by adolp on 2017/3/31.
 */

public class DtApplication extends Application {

    public static final String TAG = "DtApplication";
    public static final DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();

    public DtApplication() {
        // 设置数据库名称
        daoConfig.setDbName(ComParams.DB_NAME);
        // 设置数据库存储路径, 不设置dbDir时, 默认存储在app的私有目录
        daoConfig.setDbDir(new File(ComParams.DB_DIR));
        // 设置数据库版本
        daoConfig.setDbVersion(ComParams.DB_VERSION);

        daoConfig.setDbOpenListener(new DbManager.DbOpenListener() {
            @Override
            public void onDbOpened(DbManager db) {
                // 开启WAL, 对写入加速提升巨大
                db.getDatabase().enableWriteAheadLogging();
            }
        });
        /*daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                try {
                    db.addColumn(Sign.class, "test");
                } catch (DbException e) {
                    Log.e(TAG, "数据库更新失败");
                    e.printStackTrace();
                }
                // db.dropTable(...);
                // ...
                // or
                // db.dropDb();
            }
        });*/

        DbManager db = x.getDb(daoConfig);//获取数据库单例
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }

}
