package graduation.tatev.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import graduation.tatev.myapplication.dao.DaoFactory;
import graduation.tatev.myapplication.dao.TerminalDao;
import graduation.tatev.myapplication.dao.TruckDao;
import graduation.tatev.myapplication.dao_implementation.MsSqlDaoFacroty;

import java.sql.Connection;

/**
 * Created by Tatka on 4/16/2016.
 */
public class ConnectionService {
    private static Connection connection;
    private static DaoFactory daoFactory;
    private static TerminalDao terminalDao = null;
    private static TruckDao truckDao = null;

    public static void initialize() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    daoFactory = new MsSqlDaoFacroty();
                    connection = daoFactory.getConnection();
                    terminalDao = daoFactory.getTerminalDao(connection);
                    truckDao = daoFactory.getTruckDao(connection);

                } catch (Exception e) {
                    Log.d("excaptionOnConnecting", e.toString());
                }
                return null;
            }
        }.execute();


    }

    public static Connection getConnection() {
        if (connection == null)
            throw new IllegalStateException("service not initialized");
        return connection;
    }

    public static DaoFactory getDaoFactory() {
        if (daoFactory == null)
            throw new IllegalStateException("service not initialized");
        return daoFactory;
    }

    public static TerminalDao getTerminalDao() {
        if (terminalDao == null)
            throw new IllegalStateException("service not initialized");
        return terminalDao;
    }

    public static TruckDao getTruckDao() {
        if (terminalDao == null)
            throw new IllegalStateException("service not initialized");
        return truckDao;
    }

}
