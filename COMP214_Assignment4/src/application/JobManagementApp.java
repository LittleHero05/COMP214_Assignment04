package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.sql.*;

public class JobManagementApp {

    private TableView<Job> jobTable;
    private ObservableList<Job> jobList;

    public BorderPane getView() {
        BorderPane layout = new BorderPane();

        // Job Table
        jobTable = new TableView<>();
        configureJobTable();
        loadJobs();

        // Buttons
        Button addJobButton = new Button("Add New Job");
        addJobButton.setOnAction(e -> showAddJobDialog());

        Button editJobButton = new Button("Edit Selected Job");
        editJobButton.setOnAction(e -> editSelectedJob());

        VBox buttonBox = new VBox(10, addJobButton, editJobButton);
        buttonBox.setPadding(new Insets(10));

        layout.setCenter(jobTable);
        layout.setRight(buttonBox);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private void configureJobTable() {
        TableColumn<Job, String> idColumn = new TableColumn<>("Job ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("jobId"));

        TableColumn<Job, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));

        TableColumn<Job, Double> minSalaryColumn = new TableColumn<>("Min Salary");
        minSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("minSalary"));

        TableColumn<Job, Double> maxSalaryColumn = new TableColumn<>("Max Salary");
        maxSalaryColumn.setCellValueFactory(new PropertyValueFactory<>("maxSalary"));

        jobTable.getColumns().addAll(idColumn, titleColumn, minSalaryColumn, maxSalaryColumn);
    }

    private void loadJobs() {
        jobList = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT JOB_ID, JOB_TITLE, MIN_SALARY, MAX_SALARY FROM HR_JOBS")) {

            while (rs.next()) {
                jobList.add(new Job(
                        rs.getString("JOB_ID"),
                        rs.getString("JOB_TITLE"),
                        rs.getDouble("MIN_SALARY"),
                        rs.getDouble("MAX_SALARY")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jobTable.setItems(jobList);
    }

    private void showAddJobDialog() {
        // Dialog for adding jobs (code similar to previous example)
    }

    private void editSelectedJob() {
        // Edit job logic (code similar to previous example)
    }
}
