package org.example;

import org.example.dao.Dao;

import java.util.List;
import java.util.logging.Logger;


public class EntityService<T> {
    private static final Logger logger = Logger.getLogger(EntityService.class.getName());
    private final Dao<T> dao;

    public EntityService(Dao<T> Dao) {
        this.dao = Dao;
    }
    public long create(T object) {
        long id = dao.save(object);
        logger.info("Saved new entity to the database" + object.toString());
        return id;
    }

    public String getNameById(long id, Class<T> type) {
        return dao.getById(id, type);
    }

    public List<T> getEntitiesList(Class<T> type) {
        return dao.listAll(type);
    }

    public void updateName(long id, String name, Class<T> type) {
        dao.setName(id, name, type);
        logger.info("Updated entity name in the database: ID=" + id + ", New Name=" + name);
    }

    public void deleteById(long id, Class<T> type) {
        dao.deleteById(id, type);
        logger.info("Deleted entity from the database: ID=" + id);
    }
}

