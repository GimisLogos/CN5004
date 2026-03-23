package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MainController {

    private DataManager dataManager;

    // Patient tab controls
    @FXML private TextField patientIdField;
    @FXML private TextField patientNameField;
    @FXML private TextField patientSurnameField;
    @FXML private TextField patientPhoneField;
    @FXML private TextField patientEmailField;
    @FXML private DatePicker patientBirthDatePicker;
    @FXML private TextField patientAmkaField;
    @FXML private TextArea patientHistoryArea;
    @FXML private TableView<Patient> patientsTable;
    @FXML private TableColumn<Patient, String> patientIdColumn;
    @FXML private TableColumn<Patient, String> patientNameColumn;
    @FXML private TableColumn<Patient, String> patientSurnameColumn;
    @FXML private TableColumn<Patient, String> patientPhoneColumn;
    @FXML private TableColumn<Patient, String> patientEmailColumn;
    @FXML private TableColumn<Patient, String> patientAmkaColumn;

    // Doctor tab controls
    @FXML private TextField doctorIdField;
    @FXML private TextField doctorNameField;
    @FXML private TextField doctorSurnameField;
    @FXML private TextField doctorPhoneField;
    @FXML private TextField doctorEmailField;
    @FXML private DatePicker doctorBirthDatePicker;
    @FXML private ComboBox<Specialty> doctorSpecialtyCombo;
    @FXML private TextField doctorLicenseField;
    @FXML private TextField doctorOfficeField;
    @FXML private TableView<Doctor> doctorsTable;
    @FXML private TableColumn<Doctor, String> doctorIdColumn;
    @FXML private TableColumn<Doctor, String> doctorNameColumn;
    @FXML private TableColumn<Doctor, String> doctorSurnameColumn;
    @FXML private TableColumn<Doctor, String> doctorSpecialtyColumn;
    @FXML private TableColumn<Doctor, String> doctorPhoneColumn;

    // Appointment tab controls
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
    @FXML private TableColumn<Appointment, Boolean> appointmentStatusColumn;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeTables();
        loadData();
    }

    @FXML
    public void initialize() {
        // Initialize doctor specialty combo
        doctorSpecialtyCombo.setItems(FXCollections.observableArrayList(Specialty.values()));

        // Initialize date pickers
        patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        doctorBirthDatePicker.setValue(LocalDate.now().minusYears(35));
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
    }

    private void initializeTables() {
        // Patient table
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patientSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patientPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        patientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        patientAmkaColumn.setCellValueFactory(new PropertyValueFactory<>("amka"));

        // Doctor table
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        doctorSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        doctorSpecialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        doctorPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Appointment table
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
        appointmentDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        appointmentDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
        appointmentReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        appointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
    }

    private void loadData() {
        // Load patients
        patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));

        // Load doctors
        doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));

        // Load appointments
        appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));

        // Load combos
        appointmentPatientCombo.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        appointmentDoctorCombo.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
    }

    // Patient handlers
    @FXML
    private void handleAddPatient() {
        try {
            if (patientIdField.getText().isEmpty() || patientNameField.getText().isEmpty()) {
                statusLabel.setText("Σφάλμα: Συμπληρώστε ID και Όνομα");
                return;
            }

            Patient patient = new Patient(
                    patientIdField.getText(),
                    patientNameField.getText(),
                    patientSurnameField.getText(),
                    patientPhoneField.getText(),
                    patientEmailField.getText(),
                    patientBirthDatePicker.getValue(),
                    patientAmkaField.getText(),
                    patientHistoryArea.getText()
            );

            dataManager.addPatient(patient);
            loadData();
            clearPatientForm();
            statusLabel.setText("Ασθενής προστέθηκε επιτυχώς!");
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearPatientForm() {
        clearPatientForm();
    }

    private void clearPatientForm() {
        patientIdField.clear();
        patientNameField.clear();
        patientSurnameField.clear();
        patientPhoneField.clear();
        patientEmailField.clear();
        patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        patientAmkaField.clear();
        patientHistoryArea.clear();
    }

    // Doctor handlers
    @FXML
    private void handleAddDoctor() {
        try {
            if (doctorIdField.getText().isEmpty() || doctorNameField.getText().isEmpty()) {
                statusLabel.setText("Σφάλμα: Συμπληρώστε ID και Όνομα");
                return;
            }

            if (doctorSpecialtyCombo.getValue() == null) {
                statusLabel.setText("Σφάλμα: Επιλέξτε ειδικότητα");
                return;
            }

            Doctor doctor = new Doctor(
                    doctorIdField.getText(),
                    doctorNameField.getText(),
                    doctorSurnameField.getText(),
                    doctorPhoneField.getText(),
                    doctorEmailField.getText(),
                    doctorBirthDatePicker.getValue(),
                    doctorSpecialtyCombo.getValue(),
                    doctorLicenseField.getText(),
                    doctorOfficeField.getText()
            );

            dataManager.addDoctor(doctor);
            loadData();
            clearDoctorForm();
            statusLabel.setText("Ιατρός προστέθηκε επιτυχώς!");
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearDoctorForm() {
        clearDoctorForm();
    }

    private void clearDoctorForm() {
        doctorIdField.clear();
        doctorNameField.clear();
        doctorSurnameField.clear();
        doctorPhoneField.clear();
        doctorEmailField.clear();
        doctorBirthDatePicker.setValue(LocalDate.now().minusYears(35));
        doctorSpecialtyCombo.setValue(null);
        doctorLicenseField.clear();
        doctorOfficeField.clear();
    }

    // Appointment handlers
    @FXML
    private void handleAddAppointment() {
        try {
            if (appointmentPatientCombo.getValue() == null) {
                statusLabel.setText("Σφάλμα: Επιλέξτε ασθενή");
                return;
            }
            if (appointmentDoctorCombo.getValue() == null) {
                statusLabel.setText("Σφάλμα: Επιλέξτε ιατρό");
                return;
            }

            String appointmentId = UUID.randomUUID().toString().substring(0, 8);

            // Parse date and time
            LocalDate date = appointmentDatePicker.getValue();
            String[] timeParts = appointmentTimeField.getText().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            LocalDateTime dateTime = date.atTime(hour, minute);

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
                clearAppointmentForm();
                statusLabel.setText("Ραντεβού κλείστηκε επιτυχώς!");
            } else {
                statusLabel.setText("Σφάλμα: Υπάρχει ήδη ραντεβού αυτή την ώρα για τον συγκεκριμένο ιατρό!");
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearAppointmentForm() {
        clearAppointmentForm();
    }

    @FXML
    private void handleSearchAppointments() {
        String searchText = searchField.getText().toLowerCase();
        List<Appointment> allAppointments = dataManager.getAllAppointments();

        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        for (Appointment a : allAppointments) {
            if (a.getPatient().getFullName().toLowerCase().contains(searchText) ||
                    a.getDoctor().getFullName().toLowerCase().contains(searchText) ||
                    (a.getReason() != null && a.getReason().toLowerCase().contains(searchText))) {
                filtered.add(a);
            }
        }

        appointmentsTable.setItems(filtered);
        statusLabel.setText("Βρέθηκαν " + filtered.size() + " ραντεβού");
    }

    @FXML
    private void handleTodayAppointments() {
        List<Appointment> todayAppointments = dataManager.getAppointmentsForToday();
        appointmentsTable.setItems(FXCollections.observableArrayList(todayAppointments));
        statusLabel.setText("Ραντεβού σήμερα: " + todayAppointments.size());
    }

    @FXML
    private void handleShowAllAppointments() {
        appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
        statusLabel.setText("Εμφανίζονται όλα τα ραντεβού");
    }

    private void clearAppointmentForm() {
        appointmentPatientCombo.setValue(null);
        appointmentDoctorCombo.setValue(null);
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
        appointmentReasonField.clear();
        appointmentNotesArea.clear();
    }

    // Menu handlers
    @FXML
    private void handleSave() {
        dataManager.saveAllData();
        statusLabel.setText("Τα δεδομένα αποθηκεύτηκαν!");
    }

    @FXML
    private void handleLoad() {
        dataManager.loadAllData();
        loadData();
        statusLabel.setText("Τα δεδομένα φορτώθηκαν!");
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Σχετικά");
        //alert.setHeaderHeaderText("Σύστημα Διαχείρισης Ιατρείου");
        alert.setContentText("Έκδοση 1.0\n\nΜια εφαρμογή για διαχείριση ασθενών, ιατρών και ραντεβού.\n\nΑναπτύχθηκε για εκπαιδευτικούς σκοπούς.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}