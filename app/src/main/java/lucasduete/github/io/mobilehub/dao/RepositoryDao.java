package lucasduete.github.io.mobilehub.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lucasduete.github.io.mobilehub.models.Repository;

public class RepositoryDao extends BaseDaoImpl<Repository, String> {

    public RepositoryDao(ConnectionSource connectionSource) throws SQLException {

        super(Repository.class);
        setConnectionSource(connectionSource);

        initialize();
    }
}
