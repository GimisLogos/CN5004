package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Appointment;
import com.example.medicaloffice.model.Patient;
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

public class PatientController {

    private DataManager dataManager;
    private Label statusLabel;

    @FXML private TextField patientSearchField;
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
        patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        patientIdField.setEditable(false);
    }

    private void initializeTable() {
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patientSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patientPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        patientEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        patientAmkaColumn.setCellValueFactory(new PropertyValueFactory<>("amka"));

        addActionButtons();
    }

    private void addActionButtons() {
        TableColumn<Patient, Void> actionCol = new TableColumn<>("Ενέργειες");
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
        actionCol.setPrefWidth(210);
        patientsTable.getColumns().add(actionCol);
    }

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

    private void loadData() {
        patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        patientIdField.setText(generatePatientId());
    }

    @FXML
    private void searchPatients() {
        String text = patientSearchField.getText().toLowerCase();
        if (text.isEmpty()) {
            patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
            return;
        }

        List<Patient> filtered = dataManager.getAllPatients().stream()
                .filter(p -> p.getId().toLowerCase().contains(text) ||
                        p.getName().toLowerCase().contains(text) ||
                        p.getSurname().toLowerCase().contains(text) ||
                        p.getAmka().toLowerCase().contains(text))
                .toList();
        patientsTable.setItems(FXCollections.observableArrayList(filtered));
        if (statusLabel != null) statusLabel.setText("Βρέθηκαν " + filtered.size() + " ασθενείς");
    }

    @FXML
    private void resetPatientSearch() {
        patientSearchField.clear();
        patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        if (statusLabel != null) statusLabel.setText("Εμφανίζονται όλοι οι ασθενείς");
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

    private boolean isValidAmka(String amka) {
        return amka != null && amka.matches("\\d{11}");
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

    private boolean validatePatientFields() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (!isValidName(patientNameField.getText())) {
            setFieldError(patientNameField, true, "Όνομα: Μόνο γράμματα, μη κενό");
            errors.append("- Όνομα\n");
            isValid = false;
        } else {
            resetFieldStyle(patientNameField);
        }

        if (!isValidName(patientSurnameField.getText())) {
            setFieldError(patientSurnameField, true, "Επίθετο: Μόνο γράμματα, μη κενό");
            errors.append("- Επίθετο\n");
            isValid = false;
        } else {
            resetFieldStyle(patientSurnameField);
        }

        if (!isValidPhone(patientPhoneField.getText())) {
            setFieldError(patientPhoneField, true, "Τηλέφωνο: 10 ψηφία");
            errors.append("- Τηλέφωνο\n");
            isValid = false;
        } else {
            resetFieldStyle(patientPhoneField);
        }

        if (!isValidEmail(patientEmailField.getText())) {
            setFieldError(patientEmailField, true, "Email: μη έγκυρη μορφή");
            errors.append("- Email\n");
            isValid = false;
        } else {
            resetFieldStyle(patientEmailField);
        }

        if (!isValidBirthDate(patientBirthDatePicker.getValue())) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: Μη έγκυρη ημερομηνία γέννησης");
            isValid = false;
        }

        if (!isValidAmka(patientAmkaField.getText())) {
            setFieldError(patientAmkaField, true, "ΑΜΚΑ: 11 ψηφία");
            errors.append("- ΑΜΚΑ\n");
            isValid = false;
        } else {
            resetFieldStyle(patientAmkaField);
        }

        if (!isValid && statusLabel != null) {
            statusLabel.setText("Σφάλμα: Συμπληρώστε σωστά τα πεδία:\n" + errors.toString());
        }

        return isValid;
    }

    @FXML
    private void handleAddPatient() {
        if (!validatePatientFields()) return;

        try {
            String oldId = (String) patientIdField.getUserData();
            String patientId = (oldId != null && !oldId.isEmpty()) ? oldId : generatePatientId();

            String medicalHistory = patientHistoryArea.getText();
            if (medicalHistory == null || medicalHistory.trim().isEmpty()) {
                medicalHistory = "Χωρίς ιστορικό";
            }

            Patient patient = new Patient(
                    patientId,
                    patientNameField.getText(),
                    patientSurnameField.getText(),
                    patientPhoneField.getText(),
                    patientEmailField.getText(),
                    patientBirthDatePicker.getValue(),
                    patientAmkaField.getText(),
                    medicalHistory
            );

            if (oldId != null && !oldId.isEmpty()) {
                dataManager.deletePatient(oldId);
                patientIdField.setUserData(null);
                if (statusLabel != null) statusLabel.setText("Ασθενής ενημερώθηκε");
            } else {
                if (statusLabel != null) statusLabel.setText("Ασθενής προστέθηκε");
            }

            dataManager.addPatient(patient);
            loadData();
            clearForm();

        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
        }
    }

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
        if (statusLabel != null) statusLabel.setText("Επεξεργασία: " + p.getFullName());
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
            loadData();
            if (statusLabel != null) statusLabel.setText("Διαγράφηκε: " + p.getFullName());
        }
    }

    @FXML
    private void handleClearPatientForm() {
        clearForm();
    }

    private void clearForm() {
        patientNameField.clear();
        patientSurnameField.clear();
        patientPhoneField.clear();
        patientEmailField.clear();
        patientBirthDatePicker.setValue(LocalDate.now().minusYears(20));
        patientAmkaField.clear();
        patientHistoryArea.clear();
        patientIdField.setUserData(null);
    }

    @FXML
    private void exportPatientsToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Αποθήκευση Λίστας Ασθενών");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                 PrintWriter writer = new PrintWriter(osw)) {

                writer.write('\ufeff');
                writer.println("ID,Ονομα,Επίθετο,Τηλέφωνο,Email,ΗμΓέννησης,ΑΜΚΑ,ΙατρικόΙστορικό");
                for (Patient p : dataManager.getAllPatients()) {
                    String history = p.getMedicalHistory();
                    if (history == null || history.isEmpty()) history = "Χωρίς ιστορικό";
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            p.getId(), p.getName(), p.getSurname(), p.getPhone(),
                            p.getEmail(), p.getBirthDate(), p.getAmka(),
                            history.replace(",", " "));
                }
                if (statusLabel != null) statusLabel.setText("Εξαγωγή ασθενών σε " + file.getName());
            } catch (Exception e) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
            }
        }
    }
}