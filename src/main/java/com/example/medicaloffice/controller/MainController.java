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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainController {

    private DataManager dataManager;

    // Patient tab controls
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

    // Calendar controls
    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<String> dayAppointmentsList;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate = LocalDate.now();

    // ==================== AUTO-ID METHODS ====================

    private String generatePatientId() {
        int maxId = 0;
        for (Patient p : dataManager.getAllPatients()) {
            String id = p.getId();
            if (id.startsWith("PAT")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {}
            }
        }
        return String.format("PAT%03d", maxId + 1);
    }

    private String generateDoctorId() {
        int maxId = 0;
        for (Doctor d : dataManager.getAllDoctors()) {
            String id = d.getId();
            if (id.startsWith("DOC")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {}
            }
        }
        return String.format("DOC%03d", maxId + 1);
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

    private void updateAutoIds() {
        if (patientIdField != null && dataManager != null) {
            patientIdField.setText(generatePatientId());
        }
        if (doctorIdField != null && dataManager != null) {
            doctorIdField.setText(generateDoctorId());
        }
    }

    // ==================== EXPORT METHODS ====================

    @FXML
    private void exportPatientsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ασθενών");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                try (FileOutputStream fos = new FileOutputStream(file);
                     OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                     PrintWriter writer = new PrintWriter(osw)) {

                    writer.write('\ufeff');
                    writer.println("ID,Ονομα,Επίθετο,Τηλέφωνο,Email,ΗμΓέννησης,ΑΜΚΑ,ΙατρικόΙστορικό");
                    for (Patient p : dataManager.getAllPatients()) {
                        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                                p.getId(), p.getName(), p.getSurname(), p.getPhone(),
                                p.getEmail(), p.getBirthDate(), p.getAmka(),
                                p.getMedicalHistory().replace(",", " "));
                    }
                    statusLabel.setText("Εξαγωγή ασθενών σε " + file.getName());
                }
            } catch (Exception e) {
                statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportDoctorsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ιατρών");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                try (FileOutputStream fos = new FileOutputStream(file);
                     OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                     PrintWriter writer = new PrintWriter(osw)) {

                    writer.write('\ufeff');
                    writer.println("ID,Όνομα,Επίθετο,Τηλέφωνο,Email,ΗμΓέννησης,Ειδικότητα,ΑριθμόςΆδειας,Ιατρείο");
                    for (Doctor d : dataManager.getAllDoctors()) {
                        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                                d.getId(), d.getName(), d.getSurname(), d.getPhone(),
                                d.getEmail(), d.getBirthDate(), d.getSpecialty().getGreekName(),
                                d.getLicenseNumber(), d.getOffice());
                    }
                    statusLabel.setText("Εξαγωγή ιατρών σε " + file.getName());
                }
            } catch (Exception e) {
                statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportAppointmentsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ραντεβού");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
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
                    statusLabel.setText("Εξαγωγή ραντεβού σε " + file.getName());
                }
            } catch (Exception e) {
                statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }

    // ==================== CALENDAR METHODS ====================

    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        refreshCalendar();
    }

    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        refreshCalendar();
    }

    @FXML
    private void today() {
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        refreshCalendar();
        showDayAppointments(selectedDate);
    }

    private void refreshCalendar() {
        if (currentYearMonth == null) {
            currentYearMonth = YearMonth.now();
        }

        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.getChildren().clear();

        String[] dayNames = {"Δευ", "Τρι", "Τετ", "Πεμ", "Παρ", "Σαβ", "Κυρ"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;");
            dayLabel.setPrefWidth(100);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;

        int row = 1;
        int col = firstDayOfWeek;

        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            VBox dayCell = createDayCell(date);
            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.setStyle("-fx-padding: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-background-color: white; -fx-cursor: hand;");
        cell.setPrefWidth(100);
        cell.setPrefHeight(80);

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-font-weight: bold;");

        long appointmentCount = dataManager.getAllAppointments().stream()
                .filter(a -> a.getDateTime().toLocalDate().equals(date))
                .count();

        Label countLabel = new Label(appointmentCount + " ραντεβού");
        countLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6c757d;");

        if (date.equals(selectedDate)) {
            cell.setStyle("-fx-padding: 8; -fx-border-color: #2c3e50; -fx-border-width: 2; -fx-background-color: #e8f0fe; -fx-cursor: hand;");
        }

        if (date.equals(LocalDate.now())) {
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-underline: true;");
        }

        cell.getChildren().addAll(dayLabel, countLabel);

        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            refreshCalendar();
            showDayAppointments(date);
        });

        return cell;
    }

    private void showDayAppointments(LocalDate date) {
        dayAppointmentsList.getItems().clear();

        List<Appointment> appointments = dataManager.getAllAppointments().stream()
                .filter(a -> a.getDateTime().toLocalDate().equals(date))
                .sorted((a1, a2) -> a1.getDateTime().compareTo(a2.getDateTime()))
                .toList();

        for (Appointment a : appointments) {
            String display = a.getDateTime().toLocalTime() + " - " +
                    a.getPatient().getFullName() + " / " +
                    a.getDoctor().getFullName() + " (" + a.getReason() + ")";
            dayAppointmentsList.getItems().add(display);
        }

        if (appointments.isEmpty()) {
            dayAppointmentsList.getItems().add("Δεν υπάρχουν ραντεβού για αυτή την ημέρα");
        }

        statusLabel.setText("Ραντεβού για: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // ==================== INITIALIZATION ====================

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeTables();
        loadData();
        updateAutoIds();
        refreshCalendar();
    }

    @FXML
    public void initialize() {
        if (doctorSpecialtyCombo != null) {
            doctorSpecialtyCombo.setItems(FXCollections.observableArrayList(Specialty.values()));
        }

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

        if (patientIdField != null) {
            patientIdField.setEditable(false);
            patientIdField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #555;");
        }
        if (doctorIdField != null) {
            doctorIdField.setEditable(false);
            doctorIdField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #555;");
        }

        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
    }

    // ==================== TABLE INITIALIZATION ====================

    private void initializeTables() {
        if (patientIdColumn != null) {
            patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            patientSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            patientPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            patientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            patientAmkaColumn.setCellValueFactory(new PropertyValueFactory<>("amka"));
        }

        if (doctorIdColumn != null) {
            doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            doctorSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            doctorSpecialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
            doctorPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        }

        if (appointmentIdColumn != null) {
            appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            appointmentPatientColumn.setCellValueFactory(new PropertyValueFactory<>("patient"));
            appointmentDoctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
            appointmentDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDateTime"));
            appointmentReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        }

        addPatientActionButtons();
        addDoctorActionButtons();
        addAppointmentActionButtons();
        addDoubleClickListeners();
    }

    private void addDoubleClickListeners() {
        patientsTable.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openPatientEditDialog(row.getItem());
                }
            });
            return row;
        });

        doctorsTable.setRowFactory(tv -> {
            TableRow<Doctor> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openDoctorEditDialog(row.getItem());
                }
            });
            return row;
        });

        appointmentsTable.setRowFactory(tv -> {
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openAppointmentEditDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void addPatientActionButtons() {
        TableColumn<Patient, Void> patientEditCol = new TableColumn<>("Ενέργειες");
        patientEditCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #2c3e50; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #d9534f; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
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
        patientEditCol.setPrefWidth(210);
        patientsTable.getColumns().add(patientEditCol);
    }

    private void addDoctorActionButtons() {
        TableColumn<Doctor, Void> doctorEditCol = new TableColumn<>("Ενέργειες");
        doctorEditCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Επεξεργασία");
            private final Button deleteBtn = new Button("Διαγραφή");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                editBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #2c3e50; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
                deleteBtn.setStyle("-fx-background-color: #e2e6ea; -fx-text-fill: #d9534f; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 5 10;");
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
    }

    private void addAppointmentActionButtons() {
        TableColumn<Appointment, Void> appointmentEditCol = new TableColumn<>("Ενέργειες");
        appointmentEditCol.setCellFactory(col -> new TableCell<>() {
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
        appointmentEditCol.setPrefWidth(210);
        appointmentsTable.getColumns().add(appointmentEditCol);
    }

    // ==================== SEARCH METHODS ====================

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

        if (patientsTable != null) {
            patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        }
        if (doctorsTable != null) {
            doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        }
        if (appointmentsTable != null) {
            appointmentsTable.setItems(FXCollections.observableArrayList(dataManager.getAllAppointments()));
        }
        if (appointmentPatientCombo != null) {
            appointmentPatientCombo.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        }
        if (appointmentDoctorCombo != null) {
            appointmentDoctorCombo.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        }
        updateAutoIds();
        refreshCalendar();
    }

    // ==================== EDIT METHODS ====================

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
        statusLabel.setText("Επεξεργασία: " + p.getFullName() + " - Πατήστε Αποθήκευση");
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
        statusLabel.setText("Επεξεργασία: " + d.getFullName() + " - Πατήστε Αποθήκευση");
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
        statusLabel.setText("Επεξεργασία ραντεβού - Πατήστε Κλείσιμο");
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

    // ==================== ADD HANDLERS ====================

    @FXML
    private void handleAddPatient() {
        try {
            if (patientNameField.getText().isEmpty() || patientSurnameField.getText().isEmpty()) {
                statusLabel.setText("Σφάλμα: Συμπληρώστε όνομα και επίθετο");
                return;
            }

            String oldId = (String) patientIdField.getUserData();
            String patientId = (oldId != null && !oldId.isEmpty()) ? oldId : generatePatientId();

            Patient patient = new Patient(
                    patientId,
                    patientNameField.getText(),
                    patientSurnameField.getText(),
                    patientPhoneField.getText(),
                    patientEmailField.getText(),
                    patientBirthDatePicker.getValue(),
                    patientAmkaField.getText(),
                    patientHistoryArea.getText()
            );

            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deletePatient(oldId);
                patientIdField.setUserData(null);
                statusLabel.setText("Ασθενής ενημερώθηκε");
            } else {
                statusLabel.setText("Ασθενής προστέθηκε");
            }

            dataManager.addPatient(patient);
            loadData();
            clearPatientForm();

        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDoctor() {
        try {
            if (doctorNameField.getText().isEmpty() || doctorSurnameField.getText().isEmpty()) {
                statusLabel.setText("Σφάλμα: Συμπληρώστε όνομα και επίθετο");
                return;
            }

            if (doctorSpecialtyCombo.getValue() == null) {
                statusLabel.setText("Σφάλμα: Επιλέξτε ειδικότητα");
                return;
            }

            String oldId = (String) doctorIdField.getUserData();
            String doctorId = (oldId != null && !oldId.isEmpty()) ? oldId : generateDoctorId();

            Doctor doctor = new Doctor(
                    doctorId,
                    doctorNameField.getText(),
                    doctorSurnameField.getText(),
                    doctorPhoneField.getText(),
                    doctorEmailField.getText(),
                    doctorBirthDatePicker.getValue(),
                    doctorSpecialtyCombo.getValue(),
                    doctorLicenseField.getText(),
                    doctorOfficeField.getText()
            );

            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deleteDoctor(oldId);
                doctorIdField.setUserData(null);
                statusLabel.setText("Ιατρός ενημερώθηκε");
            } else {
                statusLabel.setText("Ιατρός προστέθηκε");
            }

            dataManager.addDoctor(doctor);
            loadData();
            clearDoctorForm();

        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddAppointment() {
        try {
            if (appointmentPatientCombo.getValue() == null || appointmentDoctorCombo.getValue() == null) {
                statusLabel.setText("Σφάλμα: Επιλέξτε ασθενή και ιατρό");
                return;
            }

            String oldId = (String) appointmentPatientCombo.getUserData();
            String appointmentId = (oldId != null && !oldId.isEmpty()) ? oldId : generateAppointmentId();

            LocalDate date = appointmentDatePicker.getValue();
            String[] timeParts = appointmentTimeField.getText().split(":");
            LocalDateTime dateTime = date.atTime(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));

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
                statusLabel.setText("Ραντεβού ενημερώθηκε");
            } else {
                statusLabel.setText("Ραντεβού κλείστηκε");
            }

            if (dataManager.addAppointment(appointment)) {
                loadData();
                clearAppointmentForm();
            } else {
                statusLabel.setText("Σφάλμα: Υπάρχει ήδη ραντεβού");
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== CLEAR METHODS ====================

    @FXML
    private void handleClearPatientForm() {
        clearPatientForm();
    }

    @FXML
    private void handleClearDoctorForm() {
        clearDoctorForm();
    }

    @FXML
    private void handleClearAppointmentForm() {
        clearAppointmentForm();
    }

    private void clearPatientForm() {
        patientIdField.setText(generatePatientId());
        patientNameField.clear();
        patientSurnameField.clear();
        patientPhoneField.clear();
        patientEmailField.clear();
        patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        patientAmkaField.clear();
        patientHistoryArea.clear();
        patientIdField.setUserData(null);
    }

    private void clearDoctorForm() {
        doctorIdField.setText(generateDoctorId());
        doctorNameField.clear();
        doctorSurnameField.clear();
        doctorPhoneField.clear();
        doctorEmailField.clear();
        doctorBirthDatePicker.setValue(LocalDate.now().minusYears(35));
        doctorSpecialtyCombo.setValue(null);
        doctorLicenseField.clear();
        doctorOfficeField.clear();
        doctorIdField.setUserData(null);
    }

    private void clearAppointmentForm() {
        appointmentPatientCombo.setValue(null);
        appointmentDoctorCombo.setValue(null);
        appointmentDatePicker.setValue(LocalDate.now());
        appointmentTimeField.setText("10:00");
        appointmentReasonField.clear();
        appointmentNotesArea.clear();
        appointmentPatientCombo.setUserData(null);
    }

    // ==================== APPOINTMENT HANDLERS ====================

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

    // ==================== MENU HANDLERS ====================

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
        alert.setContentText("Έκδοση 1.0\n\n- Auto-ID (PATxxx, DOCxxx, APPxxx)\n- Export CSV\n- Double-click edit\n- Ημερολόγιο Ραντεβού\n- Επαγγελματικό UI");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}