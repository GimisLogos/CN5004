package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Doctor;
import com.example.medicaloffice.model.Specialty;
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
        if (statusLabel != null) {
            statusLabel.setText("Doctors loaded: " + dataManager.getAllDoctors().size());
        }
    }

    @FXML
    private void searchDoctors() {
        String text = doctorSearchField.getText().toLowerCase();
        if (text.isEmpty()) {
            doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
            return;
        }

        List<Doctor> filtered = dataManager.getAllDoctors().stream()
                .filter(d -> d.getId().toLowerCase().contains(text) ||
                        d.getName().toLowerCase().contains(text) ||
                        d.getSurname().toLowerCase().contains(text) ||
                        d.getSpecialty().getGreekName().toLowerCase().contains(text))
                .toList();
        doctorsTable.setItems(FXCollections.observableArrayList(filtered));
        if (statusLabel != null) {
            statusLabel.setText("Βρέθηκαν " + filtered.size() + " ιατροί");
        }
    }

    @FXML
    private void resetDoctorSearch() {
        doctorSearchField.clear();
        doctorsTable.setItems(FXCollections.observableArrayList(dataManager.getAllDoctors()));
        if (statusLabel != null) {
            statusLabel.setText("Εμφανίζονται όλοι οι ιατροί");
        }
    }

    @FXML
    private void handleAddDoctor() {
        try {
            if (doctorNameField.getText().isEmpty() || doctorSurnameField.getText().isEmpty()) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Συμπληρώστε όνομα και επίθετο");
                return;
            }

            if (doctorSpecialtyCombo.getValue() == null) {
                if (statusLabel != null) statusLabel.setText("Σφάλμα: Επιλέξτε ειδικότητα");
                return;
            }

            Doctor doctor = new Doctor(
                    generateDoctorId(),
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
            clearForm();
            if (statusLabel != null) statusLabel.setText("Ιατρός προστέθηκε");

        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Σφάλμα: " + e.getMessage());
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