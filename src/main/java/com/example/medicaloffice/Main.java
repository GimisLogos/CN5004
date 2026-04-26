package com.example.medicaloffice;

import com.example.medicaloffice.controller.MainController;
import com.example.medicaloffice.dao.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private static DataManager dataManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            dataManager = new DataManager();

            // ΣΩΣΤΗ ΔΙΑΔΡΟΜΗ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/medicaloffice/view/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setDataManager(dataManager);

            // Εικονίδιο
            try {
                Image icon = new Image(getClass().getResourceAsStream("/com/example/medicaloffice/images/medicalicon.png"));
                primaryStage.getIcons().add(icon);
                System.out.println("✅ Icon loaded");
            } catch (Exception e) {
                System.out.println("Icon not found");
            }

            // CSS
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(getClass().getResource("/com/example/medicaloffice/style.css").toExternalForm());
                System.out.println("✅ CSS loaded");
            } catch (Exception e) {
                System.out.println("CSS not found");
            }

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