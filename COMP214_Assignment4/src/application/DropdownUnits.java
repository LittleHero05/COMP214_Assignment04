package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DropdownUnits {

    public static ObservableList<String> fetchJobs() {
        ObservableList<String> jobs = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT JOB_ID, JOB_TITLE FROM HR_JOBS")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                jobs.add(rs.getString("JOB_ID") + " - " + rs.getString("JOB_TITLE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobs;
    }

    public static ObservableList<String> fetchManagers() {
        ObservableList<String> managers = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT EMPLOYEE_ID, FIRST_NAME || ' ' || LAST_NAME AS NAME FROM HR_EMPLOYEES")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                managers.add(rs.getString("EMPLOYEE_ID") + " - " + rs.getString("NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return managers;
    }

    public static ObservableList<String> fetchDepartments() {
        ObservableList<String> departments = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DEPARTMENT_ID, DEPARTMENT_NAME FROM HR_DEPARTMENTS")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                departments.add(rs.getString("DEPARTMENT_ID") + " - " + rs.getString("DEPARTMENT_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return departments;
    }
}
