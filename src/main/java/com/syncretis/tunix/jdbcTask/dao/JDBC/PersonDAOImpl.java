package com.syncretis.tunix.jdbcTask.dao.JDBC;

import com.syncretis.tunix.jdbcTask.dao.PersonDAO;
import com.syncretis.tunix.jdbcTask.entity.Department;
import com.syncretis.tunix.jdbcTask.entity.Person;
import com.syncretis.tunix.jdbcTask.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAOImpl implements PersonDAO {
    private List<Person> personList = null;
    private final ConnectionUtil connectionUtil = new ConnectionUtil();
    private final Connection connection = connectionUtil.getConnection();

    private final String GET_ALL = "SELECT person.id, person.first_name, person.second_name, person.birthday, department.id, department.name\n" +
            "FROM person, department\n" +
            "WHERE person.department_id = department.id";

    @Override
    public List<Person> getAll() {
        personList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement(GET_ALL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            personList = getListPersonFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return personList;
    }

    @Override
    public List<Person> getAllWithFetchSize() {
        personList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement(GET_ALL)) {
            connection.setAutoCommit(false);
            preparedStatement.setFetchSize(2);
            ResultSet resultSet = preparedStatement.executeQuery();
            personList = getListPersonFromResultSet(resultSet);
            connection.setAutoCommit(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return personList;
    }

    @Override
    public Person getByID(Long id) {
        Person person = null;
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                (GET_ALL + " AND person.id = ?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            person = getPersonFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return person;
    }

    @Override
    public Person save(Person person) {
        Person savePerson = person;
        try (Connection connection = connectionUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO person (first_name, second_name, birthday, department_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, savePerson.getFirst_name());
            preparedStatement.setString(2, savePerson.getSecond_name());
            preparedStatement.setDate(3, (Date) savePerson.getBirthday());
            preparedStatement.setLong(4, savePerson.getDepartment().getId());
            preparedStatement.executeUpdate();

            ResultSet generatesSet = preparedStatement.getGeneratedKeys();
            while(generatesSet.next()) {
                person.setId(generatesSet.getLong(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return savePerson;
    }

    @Override
    public Person update(Person person) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("UPDATE person SET first_name = ?, second_name = ?, birthday = ?, department_id = ? WHERE id = ?")) {
            preparedStatement.setString(1, person.getFirst_name());
            preparedStatement.setString(2, person.getSecond_name());
            preparedStatement.setDate(3, (Date) person.getBirthday());
            preparedStatement.setLong(4, person.getDepartment().getId());
            preparedStatement.setLong(5, person.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return person;
    }

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("DELETE FROM person WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public List<Person> saveFromList(List<Person> personList) {
        try (Connection connection = connectionUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement
                ("INSERT INTO person (first_name, second_name, birthday, department_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            for (Person person : personList) {
                preparedStatement.setString(1, person.getFirst_name());
                preparedStatement.setString(2, person.getSecond_name());
                preparedStatement.setDate(3, (Date) person.getBirthday());
                preparedStatement.setLong(4, person.getDepartment().getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();

            ResultSet generateSet = preparedStatement.getGeneratedKeys();
            generateListId(personList, generateSet);
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        }
        return personList;
    }

    @Override
    public List<Person> updateFromList(List<Person> personList) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("UPDATE person SET first_name = ?, second_name = ?, birthday = ?, department_id = ? WHERE id = ?")) {
            connection.setAutoCommit(false);
            for (Person person : personList) {
                preparedStatement.setString(1, person.getFirst_name());
                preparedStatement.setString(2, person.getSecond_name());
                preparedStatement.setDate(3, (Date) person.getBirthday());
                preparedStatement.setLong(4, person.getDepartment().getId());
                preparedStatement.setLong(5, person.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        }
        return personList;
    }

    @Override
    public void deleteListById(List<Person> personList) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("DELETE FROM person WHERE id = ?")) {
            connection.setAutoCommit(false);
            for (Person person : personList) {
                preparedStatement.setLong(1, person.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private List<Person> getListPersonFromResultSet(ResultSet resultSet) {
        List<Person> personList = new ArrayList<>();
        Department department = new Department();
        try {
            while (resultSet.next()) {
                Person person = new Person();
                person.setId(resultSet.getLong("id"));
                person.setFirst_name(resultSet.getString("first_name"));
                person.setSecond_name(resultSet.getString("second_name"));
                person.setBirthday(resultSet.getDate("birthday"));
                department.setId(resultSet.getLong(5));
                department.setName(resultSet.getString(6));
                person.setDepartment(department);
                personList.add(person);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return personList;
    }

    private Person getPersonFromResultSet(ResultSet resultSet) {
        Person person = new Person();
        Department department = new Department();
        try {
            while (resultSet.next()) {
                person.setId(resultSet.getLong("id"));
                person.setFirst_name(resultSet.getString("first_name"));
                person.setSecond_name(resultSet.getString("second_name"));
                person.setBirthday(resultSet.getDate("birthday"));
                department.setId(resultSet.getLong(5));
                department.setName(resultSet.getString(6));
                person.setDepartment(department);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return person;
    }

    private void generateListId(List<Person> personList, ResultSet gererateId){
        List<Long> listId = new ArrayList<>();
        try {
            while(gererateId.next()){
                listId.add(gererateId.getLong(1));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        for (int i = 0; i < personList.size(); i++) {
            personList.get(i).setId(listId.get(i));
        }
    }
}
