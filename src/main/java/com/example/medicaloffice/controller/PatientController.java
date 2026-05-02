package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Patient;
import javafx.collections.FXCollections;
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
        if (statusLabel != null) {
            statusLabel.setText("Patients loaded: " + dataManager.getAllPatients().size());
        }
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
        if (statusLabel != null) {
            statusLabel.setText("Βρέθηκαν " + filtered.size() + " ασθενείς");
        }
    }

    @FXML
    private void resetPatientSearch() {
        patientSearchField.clear();
        patientsTable.setItems(FXCollections.observableArrayList(dataManager.getAllPatients()));
        if (statusLabel != null) {
            statusLabel.setText("Εμφανίζονται όλοι οι ασθενείς");
        }
    }

    @FXML
    private void handleAddPatient() {
        try {
            if (patientNameField.getText().isEmpty() || patientSurnameField.getText().isEmpty()) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Συμπληρώστε όνομα και επίθετο");
                return;
            }

            String medicalHistory = patientHistoryArea.getText();
            if (medicalHistory == null || medicalHistory.trim().isEmpty()) {
                medicalHistory = "Χωρίς ιστορικό";
            }

            Patient patient = new Patient(
                    generatePatientId(),
                    patientNameField.getText(),
                    patientSurnameField.getText(),
                    patientPhoneField.getText(),
                    patientEmailField.getText(),
                    patientBirthDatePicker.getValue(),
                    patientAmkaField.getText(),
                    medicalHistory
            );

            dataManager.addPatient(patient);
            loadData();
            clearForm();
            if (statusLabel != null) statusLabel.setText("Ασθενής προστέθηκε");

        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
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