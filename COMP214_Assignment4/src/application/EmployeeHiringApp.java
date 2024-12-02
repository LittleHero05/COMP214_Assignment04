package application;
import javafx.scene.layout.GridPane;

import java.sql.CallableStatement;
import java.sql.Connection;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class EmployeeHiringApp {

    public VBox getView() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField salaryField = new TextField();

        ComboBox<String> jobComboBox = new ComboBox<>(DropdownUnits.fetchJobs());
        ComboBox<String> managerComboBox = new ComboBox<>(DropdownUnits.fetchManagers());
        ComboBox<String> departmentComboBox = new ComboBox<>(DropdownUnits.fetchDepartments());

        grid.addRow(0, new Label("First Name:"), firstNameField);
        grid.addRow(1, new Label("Last Name:"), lastNameField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Phone:"), phoneField);
        grid.addRow(4, new Label("Salary:"), salaryField);
        grid.addRow(5, new Label("Job:"), jobComboBox);
        grid.addRow(6, new Label("Manager:"), managerComboBox);
        grid.addRow(7, new Label("Department:"), departmentComboBox);

        Button hireButton = new Button("Hire Employee");
        hireButton.setOnAction(e -> hireEmployee(
            firstNameField.getText(),
            lastNameField.getText(),
            emailField.getText(),
            phoneField.getText(),
            salaryField.getText(),
            jobComboBox.getValue(),
            managerComboBox.getValue(),
            departmentComboBox.getValue()
        ));

        VBox layout = new VBox(10, grid, hireButton);
        return layout;
    }

    private void hireEmployee(String firstName, String lastName, String email, String phone, String salary, String job, String manager, String department) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL employee_hire_sp(?, ?, ?, ?, SYSDATE, ?, ?, ?, ?)}");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setDouble(5, Double.parseDouble(salary));
            stmt.setString(6, job.split(" - ")[0]);
            stmt.setInt(7, Integer.parseInt(manager.split(" - ")[0]));
            stmt.setInt(8, Integer.parseInt(department.split(" - ")[0]));
            stmt.execute();

            Utils.showAlert("Success", "Employee hired successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            Utils.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
