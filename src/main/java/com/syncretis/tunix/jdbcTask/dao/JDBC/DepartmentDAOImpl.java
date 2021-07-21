package com.syncretis.tunix.jdbcTask.dao.JDBC;

import com.syncretis.tunix.jdbcTask.dao.DepartmentDAO;
import com.syncretis.tunix.jdbcTask.entity.Department;
import com.syncretis.tunix.jdbcTask.util.ConnectionUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {
    private List<Department> departmentList = null;
    private final ConnectionUtil connectionUtil = new ConnectionUtil();

    @Override
    public List<Department> getAll() {
        departmentList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("SELECT * FROM department")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            departmentList = getListFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return departmentList;
    }

    @Override
    public Department getByID(Long id) {
        Department department = null;
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("SELECT * FROM person WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            department = getDepartmentFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return department;
    }

    @Override
    public Department save(Department department) {
        Department saveDepartment = department;
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("INSERT INTO department (name) VALUES (?)")) {
            preparedStatement.setString(1, saveDepartment.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return saveDepartment;
    }

    @Override
    public Department update(Department department) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("UPDATE department SET name = ? WHERE id = ?")) {
            preparedStatement.setString(1, department.getName());
            preparedStatement.setLong(2, department.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return department;
    }

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement preparedStatement = connectionUtil.getPreparedStatement
                ("DELETE FROM department WHERE id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private List<Department> getListFromResultSet(ResultSet resultSet) {
        List<Department> departmentList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Department department = new Department();
                department.setId(resultSet.getLong("id"));
                department.setName(resultSet.getString("name"));
                departmentList.add(department);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return departmentList;
    }

    private Department getDepartmentFromResultSet(ResultSet resultSet) {
        Department department = new Department();
        try {
            while (resultSet.next()) {
                department.setId(resultSet.getLong("id"));
                department.setName(resultSet.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return department;
    }
}
