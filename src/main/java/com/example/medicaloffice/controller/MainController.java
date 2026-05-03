/*
 * ΑΡΜΟΔΙΟΤΗΤΕΣ:
 * 1. Φόρτωση όλων των tabs (Ασθενείς, Ιατροί, Ραντεβού, Ημερολόγιο)
 * 2. Δημιουργία και διαχείριση των επιμέρους controllers
 * 3. Διαχείριση status bar (χρωματικά μηνύματα)
 * 4. Κεντρική γραμμή κατάστασης (ready, success, error, info, warning)
 *
 * ΚΥΡΙΕΣ ΜΕΘΟΔΟΙ & ΓΡΑΜΜΕΣ:
 *
 * loadTabs()          - Δημιουργία και φόρτωση όλων των tabs (γραμμή ~48)
 * createTab()         - Δημιουργία ενός tab και σύνδεση controller (γραμμή ~68)
 * setDataManager()    - Πέρασμα DataManager σε όλους (γραμμή ~42)
 * setStatusSuccess()  - Πράσινο μήνυμα επιτυχίας (γραμμή ~100)
 * setStatusError()    - Κόκκινο μήνυμα σφάλματος (γραμμή ~106)
 * setStatusInfo()     - Μπλε μήνυμα πληροφορίας (γραμμή ~112)
 * setStatusWarning()  - Πορτοκαλί μήνυμα προειδοποίησης (γραμμή ~118)
 * setStatusReady()    - Επαναφορά σε "Ready" (γραμμή ~124)
 */
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
        setStatusReady();
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

            // Get controller and pass DataManager and statusLabel
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
            setStatusError("Σφάλμα φόρτωσης: " + title);
        }

        return tab;
    }

    // ==================== STATUS METHODS ====================

    public void setStatusSuccess(String message) {
        if (statusLabel != null) {
            statusLabel.setText("✅ " + message);
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    public void setStatusError(String message) {
        if (statusLabel != null) {
            statusLabel.setText("❌ " + message);
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    public void setStatusInfo(String message) {
        if (statusLabel != null) {
            statusLabel.setText("ℹ️ " + message);
            statusLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    public void setStatusWarning(String message) {
        if (statusLabel != null) {
            statusLabel.setText("⚠️ " + message);
            statusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    public void setStatusReady() {
        if (statusLabel != null) {
            statusLabel.setText("✅ Ready");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }
}