package com.syncretis.tunix.jdbcTask.dao;

import java.util.List;

public interface GenericDAO<T, ID>{
    List<T> getAll();
    T getByID(ID id);
    T save(T t);
    T update(T t);
    void deleteById(ID id);

}
