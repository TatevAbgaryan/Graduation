package graduation.tatev.myapplication.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Tatka on 2/6/2016.
 */
public interface GenericDao<T, PK extends Serializable> {

    public T create() throws SQLException;

    public T persist(T object) throws SQLException;

    public T getByPK(int key) throws SQLException;

    public void update(T object) throws SQLException;

    public void delete(T object) throws SQLException;

    public List<T> getAll() throws SQLException;
}
