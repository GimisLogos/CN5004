/*
 * ΑΡΜΟΔΙΟΤΗΤΕΣ:
 * 1. CRUD λειτουργίες για ιατρούς
 * 2. Αναζήτηση (ID, όνομα, επίθετο, ειδικότητα)
 * 3. Εξαγωγή σε CSV
 * 4. Validation όλων των πεδίων (όλα είναι υποχρεωτικά)
 * 5. Auto-ID generation (DOC001, DOC002...)
 *
 * ΚΥΡΙΕΣ ΜΕΘΟΔΟΙ & ΓΡΑΜΜΕΣ:
 *
 * handleAddDoctor()          - Προσθήκη ή ενημέρωση ιατρού (γραμμή ~302)
 * validateDoctorFields()     - Έλεγχος υποχρεωτικών πεδίων (γραμμή ~232)
 * searchDoctors()            - Αναζήτηση ιατρών (γραμμή ~164)
 * resetDoctorSearch()        - Επαναφορά αναζήτησης (γραμμή ~190)
 * exportDoctorsToCSV()       - Εξαγωγή σε CSV (γραμμή ~387)
 * generateDoctorId()         - Auto-ID: DOC001, DOC002... (γραμμή ~144)
 * deleteDoctor()             - Διαγραφή ιατρού και ραντεβού του (γραμμή ~352)
 * openDoctorEditDialog()     - Φόρτωση στοιχείων για επεξεργασία (γραμμή ~338)
 */
package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Appointment;
import com.example.medicaloffice.model.Doctor;
import com.example.medicaloffice.model.Specialty;
import javafx.collections.FXCollections;
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
import java.util.List;

public class DoctorController {

    private DataManager dataManager;
    private Label statusLabel;

    @FXML private TextField doctorSearchField;
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

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeTable();
        loadData();
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
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
    public void initialize() {
        doctorSpecialtyCombo.setItems(FXCollections.observableArrayList(Specialty.values()));
        doctorBirthDatePicker.setValue(LocalDate.now().minusYears(35));
        doctorIdField.setEditable(false);
    }

    private void initializeTable() {
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        doctorSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        doctorSpecialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        doctorPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        addActionButtons();
    }

    private void addActionButtons() {
        TableColumn<Doctor, Void> actionCol = new TableColumn<>("Ενέργειες");
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
        actionCol.setPrefWidth(210);
        doctorsTable.getColumns().add(actionCol);
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

    private void loadData() {
        doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        doctorIdField.setText(generateDoctorId());
    }

    @FXML
    private void searchDoctors() {
        String text = doctorSearchField.getText().toLowerCase().trim();

        if (text.isEmpty()) {
            doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
            if (statusLabel != null) statusLabel.setText("Εμφανίζονται όλοι οι ιατροί");
            return;
        }

        List<Doctor> filtered = dataManager.getAllDoctors().stream()
                .filter(d -> (d.getId() != null && d.getId().toLowerCase().contains(text)) ||
                        (d.getName() != null && d.getName().toLowerCase().contains(text)) ||
                        (d.getSurname() != null && d.getSurname().toLowerCase().contains(text)) ||
                        (d.getSpecialty().getGreekName().toLowerCase().contains(text)))
                .toList();

        doctorsTable.setItems(FXCollections.observableArrayList(filtered));

        if (filtered.isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Δεν βρέθηκαν ιατροί");
        } else {
            if (statusLabel != null) statusLabel.setText("Βρέθηκαν " + filtered.size() + " ιατροί");
        }
    }

    @FXML
    private void resetDoctorSearch() {
        doctorSearchField.clear();
        doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        doctorsTable.refresh();
        if (statusLabel != null) statusLabel.setText("Εμφανίζονται όλοι οι ιατροί (" + dataManager.getAllDoctors().size() + ")");
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("[\\p{L}Α-Ωα-ω\\s-]+");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(69\\d{8}|2\\d{7})$");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidBirthDate(LocalDate date) {
        if (date == null) return false;
        LocalDate today = LocalDate.now();
        return date.isBefore(today) && date.isAfter(today.minusYears(120));
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

    private boolean validateDoctorFields() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (!isValidName(doctorNameField.getText())) {
            setFieldError(doctorNameField, true, "Όνομα: Μόνο γράμματα, μη κενό");
            errors.append("- Όνομα\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorNameField);
        }

        if (!isValidName(doctorSurnameField.getText())) {
            setFieldError(doctorSurnameField, true, "Επίθετο: Μόνο γράμματα, μη κενό");
            errors.append("- Επίθετο\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorSurnameField);
        }

        if (!isValidPhone(doctorPhoneField.getText())) {
            setFieldError(doctorPhoneField, true, "Τηλέφωνο: 10 ψηφία");
            errors.append("- Τηλέφωνο\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorPhoneField);
        }

        if (!isValidEmail(doctorEmailField.getText())) {
            setFieldError(doctorEmailField, true, "Email: μη έγκυρη μορφή");
            errors.append("- Email\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorEmailField);
        }

        if (!isValidBirthDate(doctorBirthDatePicker.getValue())) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: Μη έγκυρη ημερομηνία γέννησης");
            isValid = false;
        }

        if (doctorSpecialtyCombo.getValue() == null) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: Επιλέξτε ειδικότητα");
            isValid = false;
        }

        if (doctorLicenseField.getText() == null || doctorLicenseField.getText().trim().isEmpty()) {
            setFieldError(doctorLicenseField, true, "Αριθμός άδειας είναι υποχρεωτικός");
            errors.append("- Αριθμός Άδειας\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorLicenseField);
        }

        if (doctorOfficeField.getText() == null || doctorOfficeField.getText().trim().isEmpty()) {
            setFieldError(doctorOfficeField, true, "Ιατρείο είναι υποχρεωτικό");
            errors.append("- Ιατρείο\n");
            isValid = false;
        } else {
            resetFieldStyle(doctorOfficeField);
        }

        if (!isValid && statusLabel != null) {
            statusLabel.setText("Σφάλμα: Συμπληρώστε σωστά τα πεδία:\n" + errors.toString());
        }

        return isValid;
    }

    @FXML
    private void handleAddDoctor() {
        if (!validateDoctorFields()) return;

        try {
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
                if (statusLabel != null) statusLabel.setText("Ιατρός ενημερώθηκε");
            } else {
                if (statusLabel != null) statusLabel.setText("Ιατρός προστέθηκε");
            }

            dataManager.addDoctor(doctor);
            loadData();
            clearForm();

        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
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
        if (statusLabel != null) statusLabel.setText("Επεξεργασία: " + d.getFullName());
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
            loadData();
            if (statusLabel != null) statusLabel.setText("Διαγράφηκε: " + d.getFullName());
        }
    }

    @FXML
    private void handleClearDoctorForm() {
        clearForm();
    }

    private void clearForm() {
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

    @FXML
    private void exportDoctorsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ιατρών");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
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
                if (statusLabel != null) statusLabel.setText("Εξαγωγή ιατρών σε " + file.getName());
            } catch (Exception e) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }
}