package graduation.tatev.myapplication.dao_implementation;

import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.dao.TruckDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tatka on 2/6/2016.
 */
public class MsSqlTruckDao implements TruckDao {
    private final Connection connection;

    MsSqlTruckDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Truck truck) throws SQLException {
        String sql = "INSERT INTO Truck VALUES(? ? ? ? ?)";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, truck.getTruckID());
        stm.setDate(2, truck.getStartTime());
        stm.setDate(3, truck.getRecoveryTime());
        stm.setDate(4, truck.getArrivalTime());
        stm.setInt(5, truck.getSpeed());
        stm.executeUpdate();
    }

    @Override
    public Truck raed(int key) throws SQLException {
        String sql = "SELECT * FROM Truck WHERE id = ?;";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, key);
        ResultSet rs = stm.executeQuery();
        rs.next();
        Truck truck = new Truck();
        truck.setTruckID(rs.getInt("TruckID"));
        truck.setStartTime(rs.getDate("StartTime"));
        truck.setRecoveryTime(rs.getDate("RecoveryTime"));
        truck.setArrivalTime(rs.getDate("ArrivalTime"));
        truck.setSpeed(rs.getInt("Speed"));
        return truck;
    }

    @Override
    public void update(Truck truck) throws SQLException {
        String sql = "UPDATE Truck SET StartTime =  ?,  RecoveryTime =  ?, ArrivalTime =  ?,Speed =  ? WHERE TruckID = ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setDate(1, truck.getStartTime());
        stm.setDate(2, truck.getRecoveryTime());
        stm.setDate(3, truck.getArrivalTime());
        stm.setInt(4, truck.getSpeed());
        stm.setInt(5, truck.getTruckID());
        stm.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Truck WHERE id = ?";
        PreparedStatement stm = connection.prepareStatement(sql);
        stm.setInt(1, id);
        stm.executeUpdate();
    }

    @Override
    public List<Truck> getAllTrucks() throws SQLException {
        String sql = "SELECT * FROM Truck;";
        PreparedStatement stm = connection.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        List<Truck> list = new ArrayList<Truck>();
        while (rs.next()) {
            Truck truck = new Truck();
            truck.setTruckID(rs.getInt("TruckID"));
            truck.setStartTime(rs.getDate("StartTime"));
            truck.setRecoveryTime(rs.getDate("RecoveryTime"));
            truck.setArrivalTime(rs.getDate("ArrivalTime"));
            truck.setSpeed(rs.getInt("Speed"));
            list.add(truck);
        }
        return list;
    }
}
