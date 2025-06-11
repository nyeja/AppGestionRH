package com.rhplus.rhplus.dao;

import com.rhplus.rhplus.utils.ConnexionDB;

import java.sql.Connection;

public class EmployeDAO {
    private Connection conn = ConnexionDB.getConnection();

    public List<Employe> getAllEmployes() { /* ... */ }

    public void addEmploye(Employe emp) { /* ... */ }

    public void updateEmploye(Employe emp) { /* ... */ }

    public void deleteEmploye(int id) { /* ... */ }
}
