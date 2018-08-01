package lucasduete.github.io.mobilehub.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.models.UserLocation;

public class RepositoryDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "repositories.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Repository, String> repositoryDao = null;
    private RuntimeExceptionDao<Repository, String> runtimeExceptionDao = null;

    public RepositoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(RepositoryDBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, UserLocation.class);
        } catch (SQLException e) {
            Log.e(RepositoryDBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(RepositoryDBHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, UserLocation.class, true);

            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(RepositoryDBHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Repository, String> getDao() throws SQLException {

        if (repositoryDao == null)
            repositoryDao = getDao(Repository.class);

        return repositoryDao;
    }

    public RuntimeExceptionDao<Repository, String> getSimpleDataDao() {

        if (runtimeExceptionDao == null)
            runtimeExceptionDao = getRuntimeExceptionDao(Repository.class);

        return runtimeExceptionDao;
    }

    @Override
    public void close() {
        super.close();
        repositoryDao = null;
        runtimeExceptionDao = null;
    }

}