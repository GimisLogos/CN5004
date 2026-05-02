package com.example.medicaloffice.controller;

import com.example.medicaloffice.dao.DataManager;
import com.example.medicaloffice.model.Appointment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarController {

    private DataManager dataManager;

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<String> dayAppointmentsList;

    private YearMonth currentYearMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        refreshCalendar();
    }

    @FXML
    public void initialize() {
        // Θα ανανεωθεί όταν περάσει το dataManager
    }

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

        if (dataManager == null) {
            return;
        }

        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.getChildren().clear();

        // Day headers
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

        showDayAppointments(selectedDate);
    }

    private VBox createDayCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.setStyle("-fx-padding: 8; -fx-border-color: #dee2e6; -fx-border-width: 1; -fx-background-color: white; -fx-cursor: hand;");
        cell.setPrefWidth(100);
        cell.setPrefHeight(80);

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-font-weight: bold;");

        long appointmentCount = 0;
        if (dataManager != null) {
            appointmentCount = dataManager.getAllAppointments().stream()
                    .filter(a -> a.getDateTime().toLocalDate().equals(date))
                    .count();
        }

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
        if (dayAppointmentsList == null) return;
        dayAppointmentsList.getItems().clear();

        if (dataManager == null) return;

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
    }
}