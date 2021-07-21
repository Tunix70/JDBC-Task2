package com.syncretis.tunix.jdbcTask;

import com.syncretis.tunix.jdbcTask.dao.JDBC.DepartmentDAOImpl;
import com.syncretis.tunix.jdbcTask.dao.JDBC.PersonDAOImpl;
import com.syncretis.tunix.jdbcTask.entity.Department;
import com.syncretis.tunix.jdbcTask.entity.Person;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DepartmentDAOImpl departmentDAO = new DepartmentDAOImpl();
        PersonDAOImpl personDAO = new PersonDAOImpl();

        Date date = new Date(1212121222121L);

//SAVE LIST and UPDATE LIST
        Department departmentOne = new Department(13L, "NULL Department");
        Department departmentTwo = new Department(15L, "Sport Department");

        Person personOne = new Person(null, "LAST", "LASTER", date, departmentOne);
        Person personTwo = new Person(null, "TwoTwo", "Mamkin", date, departmentTwo);
        Person personThree = new Person(null, "ThreeThree", "Mamkin", date, departmentOne);

        List<Person> secondPersonList = new ArrayList<>();
        secondPersonList.add(personOne);
        secondPersonList.add(personTwo);
        secondPersonList.add(personThree);

        System.out.println(personDAO.saveFromList(secondPersonList));
//        System.out.println(personDAO.getByID(12L));
//        System.out.println(departmentDAO.getByID(13L));
//        System.out.println(personDAO.save(personThree));
//        personDAO.deleteById(25L);


    }
}
