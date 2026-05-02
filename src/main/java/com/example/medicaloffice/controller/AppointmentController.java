package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Appointment;
import com.example.medicaloffice.model.Doctor;
import com.example.medicaloffice.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentController {

    private DataManager dataManager;
    private Label statusLabel;

    @FXML private ComboBox<Patient> appointmentPatientCombo;
    @FXML private ComboBox<Doctor> appointmentDoctorCombo;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField appointmentTimeField;
    @FXML private TextField appointmentReasonField;
    @FXML private TextArea appointmentNotesArea;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, String> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> appointmentPatientColumn;
    @FXML private TableColumn<Appointment, String> appointmentDoctorColumn;
    @FXML private TableColumn<Appointment, String> appointmentDateTimeColumn;
    @FXML private TableColumn<Appointment, String> appointmentReasonColumn;
    @FXML private TextField searchField;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeTable();
        loadData();
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    @FXML
    public void initialize() {
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
    }

    private void initializeTable() {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        appointmentDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        appointmentDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        appointmentReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));

        addActionButtons();
    }

    private void addActionButtons() {
        TableColumn<Appointment, Void> actionCol = new TableColumn<>("Ενέργειες");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #2c3e50; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #d9534f; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                editBtn.setPrefWidth(100);
                deleteBtn.setPrefWidth(90);

                editBtn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    openAppointmentEditDialog(a);
                });

                deleteBtn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    deleteAppointment(a);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        actionCol.setPrefWidth(210);
        appointmentsTable.getColumns().add(actionCol);
    }

    private String generateAppointmentId() {
        int maxId = 0;
        for (Appointment a : dataManager.getAllAppointments()) {
            String id = a.getAppointmentId();
            if (id.startsWith("APP")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {}
            }
        }
        return String.format("APP%03d", maxId + 1);
    }

    private void loadData() {
        appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
        appointmentPatientCombo.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        appointmentDoctorCombo.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
    }

    private boolean isValidTime(String time) {
        return time != null && time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private void setFieldError(TextField field, boolean hasError, String errorMessage) {
        if (hasError) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2; -fx-border-radius: 3;");
            field.setTooltip(new Tooltip(errorMessage));
        } else {
            field.setStyle("");
            field.setTooltip(null);
        }
    }

    private void resetFieldStyle(TextField field) {
        field.setStyle("");
        field.setTooltip(null);
    }

    private boolean validateAppointmentFields() {
        boolean isValid = true;

        if (appointmentPatientCombo.getValue() == null) {
            setStatusError("Επιλέξτε ασθενή");
            isValid = false;
        }

        if (appointmentDoctorCombo.getValue() == null) {
            setStatusError("Επιλέξτε ιατρό");
            isValid = false;
        }

        if (appointmentDatePicker.getValue() == null) {
            setStatusError("Επιλέξτε ημερομηνία");
            isValid = false;
        }

        if (!isValidTime(appointmentTimeField.getText())) {
            setFieldError(appointmentTimeField, true, "Ώρα: μορφή HH:MM (π.χ. 14:30)");
            isValid = false;
        } else {
            resetFieldStyle(appointmentTimeField);
        }

        if (appointmentReasonField.getText() == null || appointmentReasonField.getText().trim().isEmpty()) {
            setFieldError(appointmentReasonField, true, "Λόγος Επίσκεψης είναι υποχρεωτικός");
            isValid = false;
        } else {
            resetFieldStyle(appointmentReasonField);
        }

        return isValid;
    }

    private void setStatusSuccess(String message) {
        if (statusLabel != null) {
            statusLabel.setText("✅ " + message);
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    private void setStatusError(String message) {
        if (statusLabel != null) {
            statusLabel.setText("❌ " + message);
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    private void setStatusInfo(String message) {
        if (statusLabel != null) {
            statusLabel.setText("ℹ️ " + message);
            statusLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    @FXML
    private void handleAddAppointment() {
        if (!validateAppointmentFields()) return;

        try {
            String oldId = (String) appointmentPatientCombo.getUserData();
            String appointmentId = (oldId != null && !oldId.isEmpty()) ? oldId : generateAppointmentId();

            String[] timeParts = appointmentTimeField.getText().split(":");
            LocalDateTime dateTime = appointmentDatePicker.getValue()
                    .atTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));

            Appointment appointment = new Appointment(
                    appointmentId,
                    appointmentPatientCombo.getValue(),
                    appointmentDoctorCombo.getValue(),
                    dateTime,
                    appointmentReasonField.getText(),
                    appointmentNotesArea.getText()
            );

            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deleteAppointment(oldId);
                appointmentPatientCombo.setUserData(null);
            }

            if (dataManager.addAppointment(appointment)) {
                loadData();
                clearForm();
                setStatusSuccess("Ραντεβού κλείστηκε");
            } else {
                setStatusError("Ο ιατρός ή ο ασθενής έχει ήδη ραντεβού αυτή την ώρα");
            }
        } catch (Exception e) {
            setStatusError("Σφάλμα: " + e.getMessage());
        }
    }

    private void openAppointmentEditDialog(Appointment a) {
        appointmentPatientCombo.setValue(a.getPatient());
        appointmentDoctorCombo.setValue(a.getDoctor());
        appointmentDatePicker.setValue(a.getDateTime().toLocalDate());
        appointmentTimeField.setText(a.getDateTime().toLocalTime().toString().substring(0, 5));
        appointmentReasonField.setText(a.getReason());
        appointmentNotesArea.setText(a.getNotes());
        appointmentPatientCombo.setUserData(a.getAppointmentId());
        setStatusInfo("Επεξεργασία ραντεβού");
    }

    private void deleteAppointment(Appointment a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Διαγραφή");
        alert.setHeaderText("Διαγραφή ραντεβού");
        alert.setContentText("Θα διαγραφεί το ραντεβού του " + a.getPatient().getFullName());

        if (alert.showAndWait().get() == ButtonType.OK) {
            dataManager.deleteAppointment(a.getAppointmentId());
            loadData();
            setStatusSuccess("Ραντεβού διαγράφηκε");
        }
    }

    @FXML
    private void handleClearAppointmentForm() {
        clearForm();
        setStatusInfo("Η φόρμα καθαρίστηκε");
    }

    private void clearForm() {
        appointmentPatientCombo.setValue(null);
        appointmentDoctorCombo.setValue(null);
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
        appointmentReasonField.clear();
        appointmentNotesArea.clear();
        appointmentPatientCombo.setUserData(null);
    }

    @FXML
    private void handleSearchAppointments() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
            appointmentsTable.refresh();
            setStatusInfo("Εμφανίζονται όλα τα ραντεβού (" + dataManager.getAllAppointments().size() + ")");
            return;
        }

        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        for (Appointment a : dataManager.getAllAppointments()) {
            if ((a.getPatient().getFullName() != null && a.getPatient().getFullName().toLowerCase().contains(searchText)) ||
                    (a.getDoctor().getFullName() != null && a.getDoctor().getFullName().toLowerCase().contains(searchText)) ||
                    (a.getReason() != null && a.getReason().toLowerCase().contains(searchText))) {
                filtered.add(a);
            }
        }

        appointmentsTable.setItems(filtered);
        appointmentsTable.refresh();

        if (filtered.isEmpty()) {
            setStatusInfo("Δεν βρέθηκαν ραντεβού");
        } else {
            setStatusInfo("Βρέθηκαν " + filtered.size() + " από " + dataManager.getAllAppointments().size() + " ραντεβού");
        }
    }

    @FXML
    private void handleTodayAppointments() {
        List<Appointment> todayAppointments = dataManager.getAppointmentsForToday();
        appointmentsTable.setItems(FXCollections.observableArrayList(todayAppointments));
        appointmentsTable.refresh();
        setStatusInfo("Ραντεβού σήμερα: " + todayAppointments.size());
    }

    @FXML
    private void handleShowAllAppointments() {
        searchField.clear();
        appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
        appointmentsTable.refresh();
        setStatusInfo("Εμφανίζονται όλα τα ραντεβού (" + dataManager.getAllAppointments().size() + ")");
    }

    @FXML
    private void exportAppointmentsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ραντεβού");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                 PrintWriter writer = new PrintWriter(osw)) {

                writer.write('\ufeff');
                writer.println("ID,Ασθενής,Ιατρός,Ημερομηνία,Ώρα,Λόγος,Σημειώσεις");
                for (Appointment a : dataManager.getAllAppointments()) {
                    writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                            a.getAppointmentId(),
                            a.getPatient().getFullName(),
                            a.getDoctor().getFullName(),
                            a.getDateTime().toLocalDate(),
                            a.getDateTime().toLocalTime(),
                            a.getReason().replace(",", " "),
                            a.getNotes().replace(",", " "));
                }
                setStatusSuccess("Εξαγωγή ραντεβού σε " + file.getName());
            } catch (Exception e) {
                setStatusError("Σφάλμα: " + e.getMessage());
            }
        }
    }
}