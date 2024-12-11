package application;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EmployeeManagementApp {

	private TableView<Employee> employeeTable;
    
	public VBox getView() {
        employeeTable = new TableView<>(DropdownUnits.fetchEmployees());

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        
        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        
        TableColumn<Employee, Double> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        TableColumn<Employee, Double> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        employeeTable.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn, salaryColumn,phoneNumberColumn,emailColumn);

        Button editSalaryButton = new Button("Edit Salary");
        editSalaryButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getSalary()));
                dialog.setHeaderText("Edit Salary");
                dialog.showAndWait().ifPresent(newSalary -> updateSalary(selected.getEmployeeId(), Double.parseDouble(newSalary)));
            }
        });

        Button editPhoneButton = new Button("Edit Phone");
        editPhoneButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(selected.getPhoneNumber());
                dialog.setHeaderText("Edit Phone Number");
                dialog.showAndWait().ifPresent(newPhone -> updatePhone(selected.getEmployeeId(), newPhone));
            }
        });

        Button editEmailButton = new Button("Edit Email");
        editEmailButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(selected.getEmail());
                dialog.setHeaderText("Edit Email");
                dialog.showAndWait().ifPresent(newEmail -> updateEmail(selected.getEmployeeId(), newEmail));
            }
        });
        
        Button refreshButton = new Button ("Refresh");
        refreshButton.setOnAction(e -> refreshEmployeeTable());
        
        HBox buttonBox = new HBox(10, editSalaryButton, editPhoneButton, editEmailButton,refreshButton);
        buttonBox.setSpacing(20);
        
        return new VBox(10, employeeTable, buttonBox);
    }

    private void updateSalary(int employeeId, double newSalary) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE HR_EMPLOYEES SET SALARY = ? WHERE EMPLOYEE_ID = ?");
            stmt.setDouble(1, newSalary);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
            Utils.showAlert("Success", "Salary updated successfully!", Alert.AlertType.INFORMATION);

            refreshEmployeeTable();

        } catch (Exception e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    
    private void updatePhone(int employeeId, String newPhone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE HR_EMPLOYEES SET PHONE_NUMBER = ? WHERE EMPLOYEE_ID = ?");
            stmt.setString(1, newPhone);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
            Utils.showAlert("Success", "Phone number updated successfully!", Alert.AlertType.INFORMATION);

            refreshEmployeeTable();

        } catch (Exception e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateEmail(int employeeId, String newEmail) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE HR_EMPLOYEES SET EMAIL = ? WHERE EMPLOYEE_ID = ?");
            stmt.setString(1, newEmail);
            stmt.setInt(2, employeeId);
            stmt.executeUpdate();
            Utils.showAlert("Success", "Email updated successfully!", Alert.AlertType.INFORMATION);

            refreshEmployeeTable();

        } catch (Exception e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void refreshEmployeeTable() {
        ObservableList<Employee> updatedEmployees = DropdownUnits.fetchEmployees();
        employeeTable.setItems(updatedEmployees);
        Utils.showAlert("Success", "Table refreshed successfully!", Alert.AlertType.INFORMATION);
    }
}