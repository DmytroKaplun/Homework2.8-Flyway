package org.example.dao;


import org.example.EntityService;
import org.example.annotation.Id;
import org.example.database.Database;
import org.example.entity.Worker;
import org.example.util.SqlGenerator;
import org.postgresql.util.PGobject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class EntityDao<T> implements Dao<T> {
    private static final Logger logger = Logger.getLogger(EntityService.class.getName());
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
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
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
            logger.log(java.util.logging.Level.SEVERE, "Error saving client", e);
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public  String getById(Long id, Class<T> type) {
        String name = null;
        String selectById = SqlGenerator.createSelectByIdQuery(type);

        try (Connection conn = db.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(selectById)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                name = resultSet.getString("name");
                System.out.println(mapResultSetToObject(resultSet, type));
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Failed to find client", e);
            throw new RuntimeException(e);
        }
        return name;
    }

    @Override
    public void setName(long id, String name, Class<T> type) {
        String updateName = SqlGenerator.createUpdateNameQuery(type);

        try (Connection conn = db.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(updateName)) {
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Fail to update", e);
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        return entityList;
    }

    @Override
    public void deleteById(Long id, Class<T> type) {
    }

    private T mapResultSetToObject(ResultSet resultSet, Class<T> type) {
        T obj = null;
        try {
            obj = type.getConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = resultSet.getObject(SqlGenerator.getColumnName(field));

                if (value instanceof Date && field.getType().equals(LocalDate.class)) {
                    value = ((Date) value).toLocalDate();
                }

                if (field.getType().isEnum() && value instanceof String) {
                    value = Enum.valueOf((Class<Enum>) field.getType(), ((String) value).toUpperCase());
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
            throw new RuntimeException(e);
        }
        return obj;
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
                if (o instanceof LocalDate) {
                    o = java.sql.Date.valueOf((LocalDate) o);
                    prepared.setObject(i + 1, o);
                } else if (o instanceof Worker.Level) {
                    prepared.setObject(i + 1, ((Worker.Level) o).getDbValue(), java.sql.Types.OTHER);
                } else {
                    prepared.setObject(i + 1, o);
                }
               field.setAccessible(false);
            } catch (SQLException | IllegalAccessException e) {
                logger.log(java.util.logging.Level.SEVERE, "Failed to set prepared statement", e);
                throw new RuntimeException(e);
            }
        }
    }
}
