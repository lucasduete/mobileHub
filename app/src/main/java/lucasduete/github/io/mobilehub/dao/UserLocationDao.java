package lucasduete.github.io.mobilehub.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lucasduete.github.io.mobilehub.models.UserLocation;

public class UserLocationDao extends BaseDaoImpl<UserLocation, Integer> {

    public UserLocationDao(ConnectionSource connectionSource) throws SQLException {

        super(UserLocation.class);
        setConnectionSource(connectionSource);
        
        initialize();
    }
}
