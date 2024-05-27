package org.example.dao;


import org.example.annotation.Id;
import org.example.database.Database;
import org.example.entity.DbEnum;
import org.example.entity.Worker;
import org.example.util.SqlGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class EntityDao<T> implements Dao<T> {
    private static final Logger logger = Logger.getLogger(EntityDao.class.getName());
    private final Database db;

    public EntityDao(Database db) {
        this.db = db;

    }

    @Override
    public long save(T object) {
        String insertQuery = SqlGenerator.createInsertEntityQuery(object.getClass());
        long id = 0;
        try (Connection conn = db.getConnection();
             PreparedStatement prepared = conn.prepareStatement(insertQuery,
                     RETURN_GENERATED_KEYS)) {
            setFieldsValueToPrepState(prepared, object);
            prepared.executeUpdate();
            ResultSet generatedKeys = prepared.getGeneratedKeys();

            while (generatedKeys.next()) {
                Field field = SqlGenerator.getIdField(object.getClass());
                field.setAccessible(true);
                id = generatedKeys.getObject(1, Long.class);
                field.set(object, id);
                field.setAccessible(false);
            }
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error saving client", e);
        }
        return id;
    }

    @Override
    public T getById(Long id, Class<T> type) {
        String selectById = SqlGenerator.createSelectByIdQuery(type);

        T mapped = null;
        try (Connection conn = db.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(selectById)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                mapped = mapResultSetToObject(resultSet, type);
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to find client", e);
        }
        return mapped;
    }

    @Override
    public void setName(T object) {
        String updateName = SqlGenerator.createUpdateNameQuery(object.getClass());

        try (Connection conn = db.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(updateName)) {
            conn.setAutoCommit(false);
            setPrepStatement(object, preparedStatement, conn);
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Fail to update", e);
        }
    }

    @Override
    public List<T> listAll(Class<T> type) {
        List<T> entityList = new ArrayList<>();
        String query = SqlGenerator.createSelectAllEntitiesQuery(type);

        try(Connection conn = db.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                entityList.add(mapResultSetToObject(resultSet, type));
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to find client", e);
        }
        return entityList;
    }

    @Override
    public void deleteById(Long id, Class<T> type) {
        String deleteQuery = SqlGenerator.createDeleteQuery(type);
       try (Connection conn = db.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);) {
           conn.setAutoCommit(false);
           setPrepStateDelete(id, preparedStatement, conn);
       } catch (SQLException e) {
           logger.log(java.util.logging.Level.SEVERE, "Fail to delete", e);
       }
    }

    private void setPrepStateDelete(Long id, PreparedStatement preparedStatement, Connection conn) throws SQLException {
        try {
            preparedStatement.setLong(1, id);
            int update = preparedStatement.executeUpdate();
            if (update != 1) {
                throw new SQLException();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private T mapResultSetToObject(ResultSet resultSet, Class<T> type) {
        T obj = null;
        try {
            obj = type.getConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = resultSet.getObject(SqlGenerator.getColumnName(field));

                if (value instanceof Date date && field.getType().equals(LocalDate.class)) {
                    value = date.toLocalDate();
                }
                if (field.getType().isEnum() && value instanceof String enumString) {
                    value = Enum.valueOf((Class<Enum>) field.getType(), enumString.toUpperCase());
                }
                field.set(obj, value);
                field.setAccessible(false);
            }
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException
                 | SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to parse resultSet", e);
        }
        return obj;
    }

    private void setPrepStatement(T object, PreparedStatement preparedStatement, Connection conn) throws SQLException {
        try {
            setFieldsValueToPrepState(preparedStatement, object);

            Field field = SqlGenerator.getIdField(object.getClass());
            field.setAccessible(true);
            Object o = field.get(object);
            preparedStatement.setObject(preparedStatement.getParameterMetaData().getParameterCount(), o);
            field.setAccessible(false);
            int update = preparedStatement.executeUpdate();
            if (update != 1) {
                throw new SQLException();
            }
            conn.commit();
        } catch (SQLException | IllegalAccessException e) {
            conn.rollback();
            logger.log(Level.SEVERE, "Fail to update", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private void setFieldsValueToPrepState(PreparedStatement prepared, T object) {
        List<Field> list = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();

        for (int i = 0; i < list.size(); i++) {
            Field field = list.get(i);
            field.setAccessible(true);
            try {
                Object o = field.get(object);
                switch (o) {
                    case LocalDate localDate -> {
                        o = java.sql.Date.valueOf(localDate);
                        prepared.setObject(i + 1, o);
                    }
                    case DbEnum dbEnum -> prepared.setObject(i + 1, dbEnum.getDbValue(), Types.OTHER);
                    default -> prepared.setObject(i + 1, o);
                }
                field.setAccessible(false);
            } catch (SQLException | IllegalAccessException e) {
                logger.log(java.util.logging.Level.SEVERE, "Failed to set prepared statement", e);
            }
        }
    }


}
