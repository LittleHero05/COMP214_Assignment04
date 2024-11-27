package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.CallableStatement;

public class EmployeeHiringApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Labels and Inputs
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField salaryField = new TextField();
        ComboBox<String> jobComboBox = new ComboBox<>();
        ComboBox<String> managerComboBox = new ComboBox<>();
        ComboBox<String> departmentComboBox = new ComboBox<>();
        jobComboBox.setItems(DropdownUnits.fetchJobs());
        managerComboBox.setItems(DropdownUnits.fetchManagers());
        departmentComboBox.setItems(DropdownUnits.fetchDepartments());


        // Fetch dropdown data from the database (to be implemented)
        // populateComboBoxes(jobComboBox, managerComboBox, departmentComboBox);

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Salary:"), 0, 4);
        grid.add(salaryField, 1, 4);
        grid.add(new Label("Job:"), 0, 5);
        grid.add(jobComboBox, 1, 5);
        grid.add(new Label("Manager:"), 0, 6);
        grid.add(managerComboBox, 1, 6);
        grid.add(new Label("Department:"), 0, 7);
        grid.add(departmentComboBox, 1, 7);

        Button hireButton = new Button("Hire");
        hireButton.setOnAction(e -> hireEmployee(firstNameField.getText(), lastNameField.getText(),
                emailField.getText(), phoneField.getText(), salaryField.getText(),
                jobComboBox.getValue(), managerComboBox.getValue(), departmentComboBox.getValue()));

        grid.add(hireButton, 1, 8);

        // Scene and Stage
        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setTitle("Employee Hiring");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void hireEmployee(String firstName, String lastName, String email,
                              String phone, String salary, String jobId, String managerId, String departmentId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            CallableStatement stmt = conn.prepareCall("{CALL employee_hire_sp(?, ?, ?, ?, SYSDATE, ?, ?, ?, ?)}");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setDouble(5, Double.parseDouble(salary));
            stmt.setString(6, jobId);
            stmt.setInt(7, Integer.parseInt(managerId));
            stmt.setInt(8, Integer.parseInt(departmentId));
            stmt.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Employee hired successfully!");
            alert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
