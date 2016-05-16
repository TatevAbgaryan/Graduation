package graduation.tatev.myapplication.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Tatka on 2/6/2016.
 */
public interface DaoFactory {

    public Connection getConnection() throws SQLException;

    public TerminalDao getTerminalDao(Connection connection);

    public TruckDao getTruckDao(Connection connection);

}