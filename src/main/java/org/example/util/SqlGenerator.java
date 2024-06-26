package org.example.util;

import org.example.annotation.Column;
import org.example.annotation.Id;
import org.example.annotation.Table;
import org.example.entity.Client;

import java.lang.reflect.Field;
import java.util.*;

public class SqlGenerator {
    public static final String INSERT_ENTITY = "INSERT INTO %s (%s) VALUES(%s)";
    public static final String SELECT_ENTITY_BY_ID = "SELECT * FROM %s WHERE %s = ?";
    public static final String SELECT_ALL_ENTITIES = "SELECT * FROM %s";
    public static final String UPDATE_NAME = "UPDATE %s SET %s WHERE %s= ?";
    public static final String DELETE_BY_ID = "DELETE FROM %s WHERE %s = ?";

    public static String createDeleteQuery(Class<?> type) {
        String tableName = getTableName(type);
        String idColName = getIdColumnName(type);
        return DELETE_BY_ID.formatted(tableName, idColName);
    }

    public static void main(String[] args) {
        System.out.println(createUpdateNameQuery(Client.class));
    }

    public static String createUpdateNameQuery(Class<?> type) {
        String tableName = getTableName(type);
        List<String> columns = getColumnsForInsert(type);
        List<String> colWithPlaceholder = new ArrayList<>();
        for (String string : columns) {
            colWithPlaceholder.add(string + "= ?");
        }
        String idColName = getIdColumnName(type);
        return UPDATE_NAME.formatted(tableName,
                String.join(",", colWithPlaceholder),
                idColName);
    }

    public static String createSelectByIdQuery(Class<?> type) {
        String tableName = getTableName(type);
        String idColName = getIdColumnName(type);
        return SELECT_ENTITY_BY_ID.formatted(tableName, idColName);
    }

    public static String createInsertEntityQuery(Class<?> type) {
        String tableName = getTableName(type);
        List<String> columnNames = getColumnsForInsert(type);
        List<String> values = Collections.nCopies(columnNames.size(), "?");
        return INSERT_ENTITY.formatted(
                tableName,
                String.join(",", columnNames),
                String.join(",", values)
        );
    }

    private SqlGenerator() {}

    private static List<String> getColumnsForInsert(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .map(SqlGenerator::getColumnName)
                .toList();
    }

    public static String createSelectAllEntitiesQuery(Class<?> type) {
        String tableName = getTableName(type);
        return SELECT_ALL_ENTITIES.formatted(tableName);
    }

    public static String getIdColumnName(Class<?> type) {
        return getColumnName(getIdField(type));
    }

    public static String getColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElseGet(field::getName);
    }

    public static Field getIdField(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No field with @Id annotation found"));
    }

    private static String getTableName(Class<?> type) {
        return Optional.ofNullable(type.getAnnotation(Table.class))
                .map(Table::value)
                .orElseGet(() -> type.getSimpleName().toLowerCase());
    }
}
