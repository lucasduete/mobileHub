package lucasduete.github.io.mobilehub.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lucasduete.github.io.mobilehub.models.Issue;

public class IssueDao extends BaseDaoImpl<Issue, String> {

    public IssueDao(ConnectionSource connectionSource) throws SQLException {

        super(Issue.class);
        setConnectionSource(connectionSource);
        
        initialize();
    }
}
