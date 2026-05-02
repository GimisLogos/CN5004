package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
        if (statusLabel != null) {
            statusLabel.setText("Appointments loaded: " + dataManager.getAllAppointments().size());
        }
    }

    @FXML
    private void handleAddAppointment() {
        try {
            if (appointmentPatientCombo.getValue() == null || appointmentDoctorCombo.getValue() == null) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Επιλέξτε ασθενή και ιατρό");
                return;
            }

            if (appointmentDatePicker.getValue() == null) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Επιλέξτε ημερομηνία");
                return;
            }

            if (appointmentTimeField.getText() == null || appointmentTimeField.getText().trim().isEmpty()) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Συμπληρώστε ώρα");
                return;
            }

            if (appointmentReasonField.getText() == null || appointmentReasonField.getText().trim().isEmpty()) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Συμπληρώστε λόγο επίσκεψης");
                return;
            }

            String appointmentId = generateAppointmentId();

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

            if (dataManager.addAppointment(appointment)) {
                loadData();
                clearForm();
                if (statusLabel != null) statusLabel.setText("Ραντεβού κλείστηκε");
            } else {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Υπάρχει ήδη ραντεβού");
            }
        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearAppointmentForm() {
        clearForm();
    }

    private void clearForm() {
        appointmentPatientCombo.setValue(null);
        appointmentDoctorCombo.setValue(null);
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
        appointmentReasonField.clear();
        appointmentNotesArea.clear();
    }

    @FXML
    private void handleSearchAppointments() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
            return;
        }

        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        for (Appointment a : dataManager.getAllAppointments()) {
            if (a.getPatient().getFullName().toLowerCase().contains(searchText) ||
                    a.getDoctor().getFullName().toLowerCase().contains(searchText) ||
                    (a.getReason() != null && a.getReason().toLowerCase().contains(searchText))) {
                filtered.add(a);
            }
        }
        appointmentsTable.setItems(filtered);
        if (statusLabel != null) {
            statusLabel.setText("Βρέθηκαν " + filtered.size() + " ραντεβού");
        }
    }

    @FXML
    private void handleTodayAppointments() {
        List<Appointment> todayAppointments = dataManager.getAppointmentsForToday();
        appointmentsTable.setItems(FXCollections.observableArrayList(todayAppointments));
        if (statusLabel != null) {
            statusLabel.setText("Ραντεβού σήμερα: " + todayAppointments.size());
        }
    }

    @FXML
    private void handleShowAllAppointments() {
        appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
        if (statusLabel != null) {
            statusLabel.setText("Εμφανίζονται όλα τα ραντεβού");
        }
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
                if (statusLabel != null) statusLabel.setText("Εξαγωγή ραντεβού σε " + file.getName());
            } catch (Exception e) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }
}