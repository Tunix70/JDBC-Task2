package com.syncretis.tunix.jdbcTask.dao;

import com.syncretis.tunix.jdbcTask.entity.Person;

import java.util.List;

public interface PersonDAO extends GenericDAO<Person, Long>{
    List<Person> saveFromList(List<Person> personList);
    List<Person> updateFromList(List<Person> personList);
    void deleteListById(List<Person> personList);
    List<Person> getAllWithFetchSize();
}
