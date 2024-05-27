package org.example;

import org.example.dao.Dao;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;


public class EntityService<T> {
    private static final Logger logger = Logger.getLogger(EntityService.class.getName());
    private final Dao<T> dao;

    public EntityService(Dao<T> dao) {
        this.dao = dao;
    }
    public long create(T object) {
        long id = dao.save(object);
        logger.info("Saved new entity to the database" + object.toString());
        return id;
    }

    public T getNameById(long id, Class<T> type) {
        return dao.getById(id, type);
    }

    public List<T> getEntitiesList(Class<T> type) {
        return dao.listAll(type);
    }

    public void updateName(T object) {
        dao.setName(object);
        logger.info("Updated entity in the database");
    }

    public void deleteById(long id, Class<T> type) {
        dao.deleteById(id, type);
        logger.info(MessageFormat.format("Deleted entity from the database: ID={0}", id));
    }
}

