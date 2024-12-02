package application;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class EmployeeManagementApp {

    public VBox getView() {
        TableView<Employee> employeeTable = new TableView<>(DropdownUnits.fetchEmployees());

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        employeeTable.getColumns().addAll(idColumn, firstNameColumn, salaryColumn);

        Button editButton = new Button("Edit Salary");
        editButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getSalary()));
                dialog.setHeaderText("Edit Salary");
                dialog.showAndWait().ifPresent(newSalary -> updateSalary(selected.getEmployeeId(), Double.parseDouble(newSalary)));
            }
        });

        return new VBox(10, employeeTable, editButton);
    }

    private void updateSalary(int employeeId, double newSalary) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE HR_EMPLOYEES SET SALARY = ? WHERE EMPLOYEE_ID = ?");
            stmt.setDouble(1, newSalary);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
            Utils.showAlert("Success", "Salary updated successfully!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
