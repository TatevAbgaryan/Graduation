package graduation.tatev.myapplication.dao_implementation;

import graduation.tatev.myapplication.dao.DaoFactory;
import graduation.tatev.myapplication.dao.TerminalDao;
import graduation.tatev.myapplication.dao.TruckDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Tatka on 2/6/2016.
 */
public class MsSqlDaoFacroty implements DaoFactory {
    private String connectionUrl = "jdbc:jtds:sqlserver://SQL5021.Smarterasp.net;user=DB_9F32C9_truck_admin;password=15253555;database=DB_9F32C9_truck";

    public MsSqlDaoFacroty() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl);
    }

    @Override
    public TruckDao getTruckDao(Connection connection) {
        return new MsSqlTruckDao(connection);
    }

    @Override
    public TerminalDao getTerminalDao(Connection connection) {
        return new MsSqlTerminalDao(connection);
    }

  }
