package graduation.tatev.myapplication.dao_implementation;

import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.dao.TerminalDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tatka on 2/6/2016.
 */
public class MsSqlTerminalDao implements TerminalDao {
    private final Connection connection;

    MsSqlTerminalDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Terminal terminal) throws SQLException {
        String sql = "INSERT INTO Terminal VALUES(? ? ? ?)";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, terminal.getTerminalID());
        stm.setInt(2, terminal.getDuration());
        stm.setFloat(3, terminal.getLatitude());
        stm.setFloat(4, terminal.getLongitude());
        stm.executeUpdate();
    }

    @Override
    public Terminal read(int id) throws SQLException {
        String sql = "SELECT * FROM Terminal WHERE id = ?;";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, id);
        ResultSet rs = stm.executeQuery();
        rs.next();
        Terminal terminal = new Terminal();
        terminal.setTerminalID(rs.getInt("TerminalID"));
        terminal.setDuration(rs.getInt("Duration"));
        terminal.setLatitude(rs.getFloat("Latitude"));
        terminal.setLongitude(rs.getFloat("Longitude"));
        terminal.setName(rs.getString("Name"));
        return terminal;
    }

    @Override
    public void update(Terminal terminal) throws SQLException {
        String sql = "UPDATE Terminal"
                + " SET Duration =  ? "
                + " SET Latitude =  ? "
                + " SET Longitude =  ? "
                + " WHERE USER_ID = ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, terminal.getDuration());
        stm.setFloat(2, terminal.getLatitude());
        stm.setFloat(3, terminal.getLongitude());
        stm.setInt(4, terminal.getTerminalID());
        stm.executeUpdate();
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM Terminal WHERE id = ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, id);
        stm.executeUpdate();
    }

    @Override
    public List<Terminal> getAllTerminals() throws SQLException {
        String sql = "SELECT * FROM Terminal;";
        PreparedStatement stm = connection.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        List<Terminal> list = new ArrayList<Terminal>();
        while (rs.next()) {
            Terminal terminal = new Terminal();
            terminal.setTerminalID(rs.getInt("TerminalID"));
            terminal.setDuration(rs.getInt("Duration"));
            terminal.setLatitude(rs.getFloat("Latitude"));
            terminal.setLongitude(rs.getFloat("Longitude"));
            terminal.setName(rs.getString("Name"));
            list.add(terminal);
        }
        return list;
    }

    @Override
    public Terminal getTerminalByName(String name) throws SQLException {
        String sql = "SELECT * FROM Terminal WHERE Name  = ?;";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setString(1, name);
        ResultSet rs = stm.executeQuery();
        rs.next();
        Terminal terminal = new Terminal();
        terminal.setTerminalID(rs.getInt("TerminalID"));
        terminal.setDuration(rs.getInt("Duration"));
        terminal.setLatitude(rs.getFloat("Latitude"));
        terminal.setLongitude(rs.getFloat("Longitude"));
        terminal.setName(rs.getString("name"));
        return terminal;
    }
}
