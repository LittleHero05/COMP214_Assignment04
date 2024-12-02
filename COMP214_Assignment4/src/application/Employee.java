package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

// Employee Data Model
public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private double salary;

    // Constructor and Getters/Setters
    public Employee(int employeeId, String firstName, String lastName, String email, String phoneNumber, double salary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.salary = salary;
    }

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

    // Add getters and setters
	public static ObservableList<Employee> fetchEmployees() {
	    ObservableList<Employee> employees = FXCollections.observableArrayList();
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("SELECT EMPLOYEE_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, SALARY FROM HR_EMPLOYEES")) {
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            employees.add(new Employee(
	                rs.getInt("EMPLOYEE_ID"),
	                rs.getString("FIRST_NAME"),
	                rs.getString("LAST_NAME"),
	                rs.getString("EMAIL"),
	                rs.getString("PHONE_NUMBER"),
	                rs.getDouble("SALARY")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return employees;
	}

}
