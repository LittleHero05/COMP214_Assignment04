package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        

        Tab hireTab = new Tab("Hire Employee", new EmployeeHiringApp().getView());
        Tab manageEmployeeTab = new Tab("Manage Employees", new EmployeeManagementApp().getView());
        Tab manageJobsTab = new Tab("Manage Jobs", new JobManagementApp().getView());
        

        tabPane.getTabs().addAll(hireTab, manageEmployeeTab, manageJobsTab);

        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("../resources/styles.css").toExternalForm());

        primaryStage.setTitle("HR Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
