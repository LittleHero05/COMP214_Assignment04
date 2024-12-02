package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Job {

    private String jobId;
    private String jobTitle;
    private double minSalary;
    private double maxSalary;

    // Constructor and Getters/Setters


	public Job(String jobId, String jobTitle, double minSalary, double maxSalary) {
		super();
		this.jobId = jobId;
		this.jobTitle = jobTitle;
		this.minSalary = minSalary;
		this.maxSalary = maxSalary;
	}

		
    
    public String getJobId() {
		return jobId;
	}



	public void setJobId(String jobId) {
		this.jobId = jobId;
	}



	public String getJobTitle() {
		return jobTitle;
	}



	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}



	public double getMinSalary() {
		return minSalary;
	}



	public void setMinSalary(double minSalary) {
		this.minSalary = minSalary;
	}



	public double getMaxSalary() {
		return maxSalary;
	}



	public void setMaxSalary(double maxSalary) {
		this.maxSalary = maxSalary;
	}



	// Add getters and setters
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
}
