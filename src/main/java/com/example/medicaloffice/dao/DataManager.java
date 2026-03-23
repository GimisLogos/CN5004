package com.example.medicaloffice.dao;

import com.example.medicaloffice.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class DataManager {
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    private static final String DATA_DIR = "data/";
    private static final String DOCTORS_FILE = DATA_DIR + "doctors.dat";
    private static final String PATIENTS_FILE = DATA_DIR + "patients.dat";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.dat";

    public DataManager() {
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new ArrayList<>();

        // Create data directory if it doesn't exist
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }

        loadAllData();
    }

    // Load all data from files
    @SuppressWarnings("unchecked")
    public void loadAllData() {
        doctors = (List<Doctor>) loadFromFile(DOCTORS_FILE);
        patients = (List<Patient>) loadFromFile(PATIENTS_FILE);
        appointments = (List<Appointment>) loadFromFile(APPOINTMENTS_FILE);

        if (doctors == null) doctors = new ArrayList<>();
        if (patients == null) patients = new ArrayList<>();
        if (appointments == null) appointments = new ArrayList<>();
    }

    // Save all data to files
    public void saveAllData() {
        saveToFile(doctors, DOCTORS_FILE);
        saveToFile(patients, PATIENTS_FILE);
        saveToFile(appointments, APPOINTMENTS_FILE);
    }

    // Generic save method
    private void saveToFile(Object data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving to " + filename + ": " + e.getMessage());
        }
    }

    // Generic load method
    private Object loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from " + filename + ": " + e.getMessage());
            return null;
        }
    }

    // Doctor CRUD
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        saveAllData();
    }

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    public Doctor findDoctorById(String id) {
        return doctors.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Doctor> findDoctorsBySpecialty(Specialty specialty) {
        return doctors.stream()
                .filter(d -> d.getSpecialty() == specialty)
                .toList();
    }

    // Patient CRUD
    public void addPatient(Patient patient) {
        patients.add(patient);
        saveAllData();
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient findPatientById(String id) {
        return patients.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Patient findPatientByAmka(String amka) {
        return patients.stream()
                .filter(p -> p.getAmka().equals(amka))
                .findFirst()
                .orElse(null);
    }

    // Appointment CRUD
    public boolean addAppointment(Appointment appointment) {
        if (isDoubleBooked(appointment)) {
            return false;
        }
        appointments.add(appointment);
        saveAllData();
        return true;
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointments.stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                .toList();
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointments.stream()
                .filter(a -> a.getPatient().getId().equals(patient.getId()))
                .toList();
    }

    public List<Appointment> getAppointmentsByDate(LocalDateTime date) {
        return appointments.stream()
                .filter(a -> a.getDateTime().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }

    public List<Appointment> getAppointmentsForToday() {
        return getAppointmentsByDate(LocalDateTime.now());
    }

    private boolean isDoubleBooked(Appointment appointment) {
        return appointments.stream()
                .anyMatch(a -> a.getDoctor().getId().equals(appointment.getDoctor().getId()) &&
                        a.getDateTime().equals(appointment.getDateTime()));
    }

    public void deleteAppointment(String appointmentId) {
        appointments.removeIf(a -> a.getAppointmentId().equals(appointmentId));
        saveAllData();
    }

    public void updateAppointment(Appointment appointment) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getAppointmentId().equals(appointment.getAppointmentId())) {
                appointments.set(i, appointment);
                saveAllData();
                break;
            }
        }
    }
}