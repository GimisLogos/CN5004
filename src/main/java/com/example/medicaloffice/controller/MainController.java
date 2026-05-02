package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainController {

    private DataManager dataManager;

    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Θα γεμίσουμε τα tabs αφού περάσει το DataManager
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        loadTabs();
    }

    private void loadTabs() {
        if (mainTabPane == null) return;

        // Load Patient Tab
        Tab patientTab = createTab("Ασθενείς", "/com/example/medicaloffice/view/PatientView.fxml");
        mainTabPane.getTabs().add(patientTab);

        // Load Doctor Tab
        Tab doctorTab = createTab("Ιατροί", "/com/example/medicaloffice/view/DoctorView.fxml");
        mainTabPane.getTabs().add(doctorTab);

        // Load Appointment Tab
        Tab appointmentTab = createTab("Ραντεβού", "/com/example/medicaloffice/view/AppointmentView.fxml");
        mainTabPane.getTabs().add(appointmentTab);

        // Load Calendar Tab
        Tab calendarTab = createTab("Ημερολόγιο", "/com/example/medicaloffice/view/CalendarView.fxml");
        mainTabPane.getTabs().add(calendarTab);
    }

    private Tab createTab(String title, String fxmlPath) {
        Tab tab = new Tab(title);
        tab.setClosable(false);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();

            // Get controller and pass DataManager
            Object controller = loader.getController();
            if (controller instanceof PatientController) {
                ((PatientController) controller).setDataManager(dataManager);
                ((PatientController) controller).setStatusLabel(statusLabel);
            } else if (controller instanceof DoctorController) {
                ((DoctorController) controller).setDataManager(dataManager);
                ((DoctorController) controller).setStatusLabel(statusLabel);
            } else if (controller instanceof AppointmentController) {
                ((AppointmentController) controller).setDataManager(dataManager);
                ((AppointmentController) controller).setStatusLabel(statusLabel);
            } else if (controller instanceof CalendarController) {
                ((CalendarController) controller).setDataManager(dataManager);
            }

            tab.setContent(content);

        } catch (Exception e) {
            System.err.println("Error loading " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            VBox errorBox = new VBox();
            errorBox.getChildren().add(new Label("Error loading " + title));
            tab.setContent(errorBox);
        }

        return tab;
    }
}