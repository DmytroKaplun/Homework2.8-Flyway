package org.example.dao;

import java.util.List;

public interface Dao<T> {
    long save(T object);
    T getById(Long id, Class<T> type);
    void setName(T object);
    List<T> listAll(Class<T> type);
    void deleteById(Long id, Class<T> type);
}
