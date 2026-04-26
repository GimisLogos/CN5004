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
import javafx.scene.layout.HBox;

public class MainController {

    private DataManager dataManager;

    // Patient tab controls
    // Search fields
    @FXML private TextField patientSearchField;
    @FXML private TextField doctorSearchField;
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
        if (doctorSpecialtyCombo != null) {
            doctorSpecialtyCombo.setItems(FXCollections.observableArrayList(Specialty.values()));
        }

        // Initialize date pickers
        if (patientBirthDatePicker != null) {
            patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        }
        if (doctorBirthDatePicker != null) {
            doctorBirthDatePicker.setValue(LocalDate.now().minusYears(35));
        }
        if (appointmentDatePicker != null) {
            appointmentDatePicker.setValue(LocalDate.now());
        }
        if (appointmentTimeField != null) {
            appointmentTimeField.setText("10:00");
        }
    }

    private void initializeTables() {
        // Patient table
        if (patientIdColumn != null) {
            patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            patientSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            patientPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            patientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            patientAmkaColumn.setCellValueFactory(new PropertyValueFactory<>("amka"));
        }

        // Doctor table
        if (doctorIdColumn != null) {
            doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            doctorSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            doctorSpecialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
            doctorPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        }

        // Appointment table
        if (appointmentIdColumn != null) {
            appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            appointmentPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
            appointmentDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
            appointmentDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
            appointmentReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        }

        // ========== ΠΡΟΣΘΗΚΗ ΚΟΥΜΠΙΩΝ ΕΠΕΞΕΡΓΑΣΙΑΣ ==========

        // Κουμπί επεξεργασίας για Ασθενείς
        TableColumn<Patient, Void> patientEditCol = new TableColumn<>("Ενέργειες");
        patientEditCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");

                // Δώσε στα κουμπιά σταθερό πλάτος
                editBtn.setPrefWidth(100);
                deleteBtn.setPrefWidth(90);

                editBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    openPatientEditDialog(p);
                });

                deleteBtn.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    deletePatient(p);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        patientEditCol.setPrefWidth(210);  // Αύξηση πλάτους στήλης
        patientsTable.getColumns().add(patientEditCol);

        // Κουμπί επεξεργασίας για Ιατρούς
        TableColumn<Doctor, Void> doctorEditCol = new TableColumn<>("Ενέργειες");
        doctorEditCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");

                editBtn.setPrefWidth(100);
                deleteBtn.setPrefWidth(90);

                editBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    openDoctorEditDialog(d);
                });

                deleteBtn.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    deleteDoctor(d);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        doctorEditCol.setPrefWidth(210);
        doctorsTable.getColumns().add(doctorEditCol);

        // Κουμπί επεξεργασίας για Ραντεβού
        TableColumn<Appointment, Void> appointmentEditCol = new TableColumn<>("Ενέργειες");
        appointmentEditCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");

                // Σταθερό πλάτος στα κουμπιά
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
        appointmentEditCol.setPrefWidth(210);  // Αύξηση πλάτους στήλης
        appointmentsTable.getColumns().add(appointmentEditCol);
    }

    // ==================== ΜΕΘΟΔΟΙ ΑΝΑΖΗΤΗΣΗΣ ====================

    @FXML
    private void searchPatients() {
        String text = patientSearchField.getText().toLowerCase();
        if (text.isEmpty()) {
            patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
            return;
        }

        List<Patient> filtered = new java.util.ArrayList<>();
        for (Patient p : dataManager.getAllPatients()) {
            if (p.getId().toLowerCase().contains(text) ||
                    p.getName().toLowerCase().contains(text) ||
                    p.getSurname().toLowerCase().contains(text) ||
                    p.getAmka().toLowerCase().contains(text)) {
                filtered.add(p);
            }
        }
        patientsTable.setItems(FXCollections.observableArrayList(filtered));
        statusLabel.setText("Βρέθηκαν " + filtered.size() + " ασθενείς");
    }

    @FXML
    private void resetPatientSearch() {
        patientSearchField.clear();
        patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        statusLabel.setText("Εμφανίζονται όλοι οι ασθενείς");
    }

    @FXML
    private void searchDoctors() {
        String text = doctorSearchField.getText().toLowerCase();
        if (text.isEmpty()) {
            doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
            return;
        }

        List<Doctor> filtered = new java.util.ArrayList<>();
        for (Doctor d : dataManager.getAllDoctors()) {
            if (d.getId().toLowerCase().contains(text) ||
                    d.getName().toLowerCase().contains(text) ||
                    d.getSurname().toLowerCase().contains(text) ||
                    d.getSpecialty().getGreekName().toLowerCase().contains(text)) {
                filtered.add(d);
            }
        }
        doctorsTable.setItems(FXCollections.observableArrayList(filtered));
        statusLabel.setText("Βρέθηκαν " + filtered.size() + " ιατροί");
    }

    @FXML
    private void resetDoctorSearch() {
        doctorSearchField.clear();
        doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        statusLabel.setText("Εμφανίζονται όλοι οι ιατροί");
    }

    private void loadData() {
        if (dataManager == null) {
            System.err.println("ERROR: dataManager is null!");
            return;
        }

        // Load patients
        if (patientsTable != null) {
            patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
            System.out.println("Loaded " + dataManager.getAllPatients().size() + " patients");
        }

        // Load doctors
        if (doctorsTable != null) {
            doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
            System.out.println("Loaded " + dataManager.getAllDoctors().size() + " doctors");
        }

        // Load appointments
        if (appointmentsTable != null) {
            appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
            System.out.println("Loaded " + dataManager.getAllAppointments().size() + " appointments");
        }

        // Load combos
        if (appointmentPatientCombo != null) {
            appointmentPatientCombo.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        }
        if (appointmentDoctorCombo != null) {
            appointmentDoctorCombo.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        }
    }

    // ==================== ΜΕΘΟΔΟΙ ΕΠΕΞΕΡΓΑΣΙΑΣ ====================

    private void openPatientEditDialog(Patient p) {
        patientIdField.setText(p.getId());
        patientNameField.setText(p.getName());
        patientSurnameField.setText(p.getSurname());
        patientPhoneField.setText(p.getPhone());
        patientEmailField.setText(p.getEmail());
        patientBirthDatePicker.setValue(p.getBirthDate());
        patientAmkaField.setText(p.getAmka());
        patientHistoryArea.setText(p.getMedicalHistory());

        patientIdField.setUserData(p.getId());
        statusLabel.setText("Επεξεργασία: " + p.getFullName() + " - Πατήστε Προσθήκη για αποθήκευση");
    }

    private void deletePatient(Patient p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Διαγραφή");
        alert.setHeaderText("Διαγραφή ασθενούς");
        alert.setContentText("Θα διαγραφεί ο ασθενής: " + p.getFullName() +
                "\nΘα διαγραφούν και όλα τα ραντεβού του!");

        if (alert.showAndWait().get() == ButtonType.OK) {
            for (Appointment a : dataManager.getAppointmentsByPatient(p)) {
                dataManager.deleteAppointment(a.getAppointmentId());
            }
            dataManager.deletePatient(p.getId());
            refreshAllTables();
            statusLabel.setText("Διαγράφηκε: " + p.getFullName());
        }
    }

    private void openDoctorEditDialog(Doctor d) {
        doctorIdField.setText(d.getId());
        doctorNameField.setText(d.getName());
        doctorSurnameField.setText(d.getSurname());
        doctorPhoneField.setText(d.getPhone());
        doctorEmailField.setText(d.getEmail());
        doctorBirthDatePicker.setValue(d.getBirthDate());
        doctorSpecialtyCombo.setValue(d.getSpecialty());
        doctorLicenseField.setText(d.getLicenseNumber());
        doctorOfficeField.setText(d.getOffice());

        doctorIdField.setUserData(d.getId());
        statusLabel.setText("Επεξεργασία: " + d.getFullName() + " - Πατήστε Προσθήκη για αποθήκευση");
    }

    private void deleteDoctor(Doctor d) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Διαγραφή");
        alert.setHeaderText("Διαγραφή ιατρού");
        alert.setContentText("Θα διαγραφεί ο ιατρός: " + d.getFullName() +
                "\nΘα διαγραφούν και όλα τα ραντεβού του!");

        if (alert.showAndWait().get() == ButtonType.OK) {
            for (Appointment a : dataManager.getAppointmentsByDoctor(d)) {
                dataManager.deleteAppointment(a.getAppointmentId());
            }
            dataManager.deleteDoctor(d.getId());
            refreshAllTables();
            statusLabel.setText("Διαγράφηκε: " + d.getFullName());
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
        statusLabel.setText("Επεξεργασία ραντεβού - Πατήστε Κλείσιμο Ραντεβού για αποθήκευση");
    }

    private void deleteAppointment(Appointment a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Διαγραφή");
        alert.setHeaderText("Διαγραφή ραντεβού");
        alert.setContentText("Θα διαγραφεί το ραντεβού του " + a.getPatient().getFullName());

        if (alert.showAndWait().get() == ButtonType.OK) {
            dataManager.deleteAppointment(a.getAppointmentId());
            refreshAllTables();
            statusLabel.setText("Ραντεβού διαγράφηκε");
        }
    }

    // ==================== ΒΟΗΘΗΤΙΚΕΣ ΜΕΘΟΔΟΙ ====================
    private void refreshAllTables() {
        loadData();
    }

    private void initializeCombos() {
        if (appointmentPatientCombo != null) {
            appointmentPatientCombo.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        }
        if (appointmentDoctorCombo != null) {
            appointmentDoctorCombo.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        }
    }

    // ==================== ΔΟΚΙΜΑΣΤΙΚΑ ΔΕΔΟΜΕΝΑ ====================
    @FXML
    private void handleTestData() {
        System.out.println("=== ΔΟΚΙΜΑΣΤΙΚΗ ΠΡΟΣΘΗΚΗ ΔΕΔΟΜΕΝΩΝ ===");

        if (dataManager == null) {
            System.err.println("ERROR: dataManager is null!");
            return;
        }

        Patient testPatient = new Patient(
                "P001",
                "Γιώργος",
                "Παπαδόπουλος",
                "6941234567",
                "giorgos@email.com",
                LocalDate.of(1980, 1, 1),
                "12345678901",
                "Χωρίς ιστορικό"
        );
        dataManager.addPatient(testPatient);
        System.out.println("Προστέθηκε ασθενής: " + testPatient.getFullName());

        Doctor testDoctor = new Doctor(
                "D001",
                "Μαρία",
                "Αντωνίου",
                "6947654321",
                "maria@email.com",
                LocalDate.of(1975, 5, 5),
                Specialty.CARDIOLOGIST,
                "LIC12345",
                "Αθήνα"
        );
        dataManager.addDoctor(testDoctor);
        System.out.println("Προστέθηκε ιατρός: " + testDoctor.getFullName());

        Appointment testAppointment = new Appointment(
                "APP001",
                testPatient,
                testDoctor,
                LocalDateTime.now().plusDays(1),
                "Τακτικός έλεγχος",
                "Πρώτη επίσκεψη"
        );
        dataManager.addAppointment(testAppointment);
        System.out.println("Προστέθηκε ραντεβού: " + testAppointment.getAppointmentId());

        refreshAllTables();
        initializeCombos();

        if (statusLabel != null) {
            statusLabel.setText("Προστέθηκαν δοκιμαστικά δεδομένα!");
        }
        System.out.println("Δοκιμαστικά δεδομένα προστέθηκαν!");
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

            String oldId = (String) patientIdField.getUserData();
            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deletePatient(oldId);
                patientIdField.setUserData(null);
                statusLabel.setText("Ασθενής ενημερώθηκε!");
            }

            dataManager.addPatient(patient);
            loadData();
            clearPatientForm();

            if (oldId == null) {
                statusLabel.setText("Ασθενής προστέθηκε επιτυχώς!");
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
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

            String oldId = (String) doctorIdField.getUserData();
            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deleteDoctor(oldId);
                doctorIdField.setUserData(null);
                statusLabel.setText("Ιατρός ενημερώθηκε!");
            }

            dataManager.addDoctor(doctor);
            loadData();
            clearDoctorForm();

            if (oldId == null) {
                statusLabel.setText("Ιατρός προστέθηκε επιτυχώς!");
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
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

            String oldId = (String) appointmentPatientCombo.getUserData();
            if (oldId != null && !oldId.isEmpty()) {
                appointmentId = oldId;
                appointmentPatientCombo.setUserData(null);
            }

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

            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deleteAppointment(oldId);
            }

            if (dataManager.addAppointment(appointment)) {
                loadData();
                clearAppointmentForm();
                statusLabel.setText(oldId != null ? "Ραντεβού ενημερώθηκε!" : "Ραντεβού κλείστηκε επιτυχώς!");
            } else {
                statusLabel.setText("Σφάλμα: Υπάρχει ήδη ραντεβού αυτή την ώρα!");
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
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
        alert.setHeaderText("Σύστημα Διαχείρισης Ιατρείου");
        alert.setContentText("Έκδοση 1.0\n\nΜια εφαρμογή για διαχείριση ασθενών, ιατρών και ραντεβού.\n\nΑναπτύχθηκε για εκπαιδευτικούς σκοπούς.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}