package debug;


import android.database.sqlite.SQLiteDatabase;

import com.czm.module.common.base.BaseApplication;
import com.czm.module.greendao.db.DaoMaster;
import com.czm.module.greendao.db.DaoSession;

import java.sql.SQLDataException;

/**
 * <p>类说明</p>
 *
 * @author 曹志敏 2018/3/30
 * @version V1.0.0
 * @name GreenDaoApplication
 */
public class GreenDaoApplication extends BaseApplication {

    private DaoSession daoSession;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        login();
        initGreenDao();
    }

    /**
     * 在这里模拟登陆，然后拿到sessionId或者Token
     * 这样就能够在组件请求接口了
     */
    private void login() {

    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "czmGreenDao.db");
        sqLiteDatabase = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDatabase;
    }
}
