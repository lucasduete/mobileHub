package lucasduete.github.io.mobilehub.dao;


import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import lucasduete.github.io.mobilehub.models.UserLocation;

public class UserLocationDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "userLocations.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<UserLocation, Integer> userLocationDao = null;
    private RuntimeExceptionDao<UserLocation, Integer> runtimeExceptionDao = null;

    public UserLocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(UserLocationDBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, UserLocation.class);
        } catch (SQLException e) {
            Log.e(UserLocationDBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(UserLocationDBHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, UserLocation.class, true);

            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(UserLocationDBHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<UserLocation, Integer> getDao() throws SQLException {

        if (userLocationDao == null)
            userLocationDao = getDao(UserLocation.class);

        return userLocationDao;
    }

    public RuntimeExceptionDao<UserLocation, Integer> getSimpleDataDao() {

        if (runtimeExceptionDao == null)
            runtimeExceptionDao = getRuntimeExceptionDao(UserLocation.class);

        return runtimeExceptionDao;
    }

    @Override
    public void close() {
        super.close();
        userLocationDao = null;
        runtimeExceptionDao = null;
    }

}