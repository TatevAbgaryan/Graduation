package graduation.tatev.myapplication.dao;


import graduation.tatev.myapplication.components.Terminal;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by tatev on 02.02.2016.
 */
public interface TerminalDao {

    public void insert(Terminal terminal) throws SQLException;

    public Terminal read(int id) throws SQLException;

    public void update(Terminal terminal) throws SQLException;

    public void delete(Integer id) throws SQLException;

    public List<Terminal> getAllTerminals() throws SQLException;

    public Terminal getTerminalByName(String name) throws SQLException;

}
