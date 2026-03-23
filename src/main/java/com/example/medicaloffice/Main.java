package com.example.medicaloffice;

import com.example.medicaloffice.controller.MainController;
import com.example.medicaloffice.dao.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static DataManager dataManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize DataManager
            dataManager = new DataManager();

            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();

            // Pass DataManager to controller
            MainController controller = loader.getController();
            controller.setDataManager(dataManager);

            // Setup stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Σύστημα Διαχείρισης Ιατρείου");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}