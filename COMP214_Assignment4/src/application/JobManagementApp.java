package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
        Button identifyJobButton = new Button("Identify Job Description");
        identifyJobButton.setOnAction(e -> showIdentifyJobDialog());

        Button editJobButton = new Button("Edit Selected Job");
        editJobButton.setOnAction(e -> editSelectedJob());

        Button addJobButton = new Button("Add New Job");
        addJobButton.setOnAction(e -> showAddJobDialog());

        Button refreshButton = new Button ("Refresh");
        refreshButton.setOnAction(e -> refreshJobsTable ("Table refreshed successfully!"));
        
        VBox buttonBox = new VBox(10, identifyJobButton, editJobButton, addJobButton, refreshButton);
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
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create New Job");

        // Input Fields
        Label jobIdLabel = new Label("Job ID:");
        TextField jobIdField = new TextField();

        Label jobTitleLabel = new Label("Title:");
        TextField jobTitleField = new TextField();

        Label minSalaryLabel = new Label("Minimum Salary:");
        TextField minSalaryField = new TextField();

        Label maxSalaryLabel = new Label("Maximum Salary:");
        TextField maxSalaryField = new TextField();

        // Button to Create Job
        ButtonType createButtonType = new ButtonType("Create Job", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        VBox contentBox = new VBox(10,
                new HBox(10, jobIdLabel, jobIdField),
                new HBox(10, jobTitleLabel, jobTitleField),
                new HBox(10, minSalaryLabel, minSalaryField),
                new HBox(10, maxSalaryLabel, maxSalaryField)
        );
        contentBox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(contentBox);

        // Handle User Input
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String jobId = jobIdField.getText();
                String jobTitle = jobTitleField.getText();
                double minSalary;
                double maxSalary;

                try {
                    minSalary = Double.parseDouble(minSalaryField.getText());
                    maxSalary = Double.parseDouble(maxSalaryField.getText());

                    if (createNewJob(jobId, jobTitle, minSalary, maxSalary)) {
                        refreshJobsTable("A new job has been created.");
                    } else {
                        showAlert("Error", "Failed to create the job. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for salaries.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private boolean createNewJob(String jobId, String title, double minSalary, double maxSalary) {
        String sql = "{ CALL new_job(?, ?, ?, ?) }";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, jobId);
            stmt.setString(2, title);
            stmt.setDouble(3, minSalary);
            stmt.setDouble(4, maxSalary);

            stmt.execute();
            conn.commit();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void editSelectedJob() {
        Job selectedJob = jobTable.getSelectionModel().getSelectedItem();

        if (selectedJob == null) {
            showAlert("No Selection", "Please select a job to edit.");
            return;
        }

        Dialog<Job> dialog = new Dialog<>();
        dialog.setTitle("Edit Job");

        Label titleLabel = new Label("Job Title:");
        TextField titleField = new TextField(selectedJob.getJobTitle());

        Label minSalaryLabel = new Label("Min Salary:");
        TextField minSalaryField = new TextField(String.valueOf(selectedJob.getMinSalary()));

        Label maxSalaryLabel = new Label("Max Salary:");
        TextField maxSalaryField = new TextField(String.valueOf(selectedJob.getMaxSalary()));

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10,
                new HBox(10, titleLabel, titleField),
                new HBox(10, minSalaryLabel, minSalaryField),
                new HBox(10, maxSalaryLabel, maxSalaryField)
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    double minSalary = Double.parseDouble(minSalaryField.getText());
                    double maxSalary = Double.parseDouble(maxSalaryField.getText());
                    return new Job(selectedJob.getJobId(), titleField.getText(), minSalary, maxSalary);
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a number within salary range.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedJob -> {
            updateJobInDatabase(updatedJob);
            refreshJobsTable("Job updated successfully.");
        });
    }

    private void updateJobInDatabase(Job job) {
        String sql = "UPDATE HR_JOBS SET JOB_TITLE = ?, MIN_SALARY = ?, MAX_SALARY = ? WHERE JOB_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, job.getJobId());
            stmt.setString(2, job.getJobTitle());
            stmt.setDouble(3, job.getMinSalary());
            stmt.setDouble(4, job.getMaxSalary());

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update job. Please try again.");
        }
    }

 // Reload jobs from the database
    private void refreshJobsTable(String successMessage) {
        loadJobs(); 
        if (successMessage != null) {
            showAlert("Success", successMessage);
        }
    }

    private void showIdentifyJobDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Identify Job Description");

        Label jobIdLabel = new Label("Enter Job ID:");
        TextField jobIdField = new TextField();
        Button searchButton = new Button("Search");

        Label resultLabel = new Label();

        searchButton.setOnAction(e -> {
            String jobId = jobIdField.getText();
            String jobDescription = getJobDescription(jobId);
            if (jobDescription != null) {
                resultLabel.setText("Job Description: " + jobDescription);
            } else {
                resultLabel.setText("No job found with ID: " + jobId);
            }
        });

        HBox inputBox = new HBox(10, jobIdLabel, jobIdField, searchButton);
        inputBox.setPadding(new Insets(10));

        VBox contentBox = new VBox(10, inputBox, resultLabel);
        contentBox.setPadding(new Insets(10));

        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private String getJobDescription(String jobId) {
        String jobDescription = null;

        String sql = "SELECT JOB_TITLE FROM HR_JOBS WHERE JOB_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    jobDescription = rs.getString("JOB_TITLE");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobDescription;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
