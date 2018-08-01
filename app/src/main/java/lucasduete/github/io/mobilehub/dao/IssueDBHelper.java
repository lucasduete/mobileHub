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

import lucasduete.github.io.mobilehub.models.Issue;
import lucasduete.github.io.mobilehub.models.Repository;
import lucasduete.github.io.mobilehub.models.UserLocation;

public class IssueDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "issues.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Issue, Integer> issueDao = null;
    private RuntimeExceptionDao<Issue, Integer> runtimeExceptionDao = null;

    public IssueDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(IssueDBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, UserLocation.class);
        } catch (SQLException e) {
            Log.e(IssueDBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(IssueDBHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, UserLocation.class, true);

            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(IssueDBHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Issue, Integer> getDao() throws SQLException {

        if (issueDao == null)
            issueDao = getDao(Issue.class);

        return issueDao;
    }

    public RuntimeExceptionDao<Issue, Integer> getSimpleDataDao() {

        if (runtimeExceptionDao == null)
            runtimeExceptionDao = getRuntimeExceptionDao(Issue.class);

        return runtimeExceptionDao;
    }

    @Override
    public void close() {
        super.close();
        issueDao = null;
        runtimeExceptionDao = null;
    }

}