package org.example.dao;

import java.util.List;

public interface Dao<T> {
    long save(T object);
    String getById(Long id, Class<T> type);
    void setName(long id, String name, Class<T> type);
    List<T> listAll(Class<T> type);
    void deleteById(Long id, Class<T> type);
}
