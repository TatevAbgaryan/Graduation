package graduation.tatev.myapplication.dao;

import graduation.tatev.myapplication.components.Truck;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by tatev on 02.02.2016.
 */
public interface TruckDao {

    public void insert(Truck truck) throws SQLException;

    public Truck raed(int id) throws SQLException;

    public void update(Truck truck) throws SQLException;

    public void delete(int id) throws SQLException;

    public List<Truck> getAllTrucks() throws SQLException;


}
