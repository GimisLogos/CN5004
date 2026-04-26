package com.example.medicaloffice.dao;

import com.example.medicaloffice.model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    private static final String DATA_DIR = "data/";
    private static final String DOCTORS_FILE = DATA_DIR + "doctors.csv";
    private static final String PATIENTS_FILE = DATA_DIR + "patients.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.csv";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    // Διαγραφή ασθενούς
    public void deletePatient(String patientId) {
        patients.removeIf(p -> p.getId().equals(patientId));
        saveAllData();
    }

    // Διαγραφή ιατρού
    public void deleteDoctor(String doctorId) {
        doctors.removeIf(d -> d.getId().equals(doctorId));
        saveAllData();
    }

    // ==================== ΦΟΡΤΩΣΗ ΑΠΟ CSV ====================

    public void loadAllData() {
        loadDoctorsFromCSV();
        loadPatientsFromCSV();
        loadAppointmentsFromCSV();
    }

    private void loadDoctorsFromCSV() {
        doctors.clear();
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    Doctor doctor = new Doctor(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            LocalDate.parse(parts[5], DATE_FORMATTER),
                            Specialty.valueOf(parts[6]),
                            parts[7],
                            parts[8]
                    );
                    doctors.add(doctor);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading doctors from CSV: " + e.getMessage());
        }
    }

    private void loadPatientsFromCSV() {
        patients.clear();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    Patient patient = new Patient(
                            parts[0],
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            LocalDate.parse(parts[5], DATE_FORMATTER),
                            parts[6],
                            parts[7]
                    );
                    patients.add(patient);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading patients from CSV: " + e.getMessage());
        }
    }

    private void loadAppointmentsFromCSV() {
        appointments.clear();
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Patient patient = findPatientById(parts[1]);
                    Doctor doctor = findDoctorById(parts[2]);

                    if (patient != null && doctor != null) {
                        Appointment appointment = new Appointment(
                                parts[0],
                                patient,
                                doctor,
                                LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER),
                                parts[4],
                                parts[5]
                        );
                        appointment.setCompleted(Boolean.parseBoolean(parts[6]));
                        appointments.add(appointment);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments from CSV: " + e.getMessage());
        }
    }

    // ==================== ΑΠΟΘΗΚΕΥΣΗ ΣΕ CSV ====================

    public void saveAllData() {
        saveDoctorsToCSV();
        savePatientsToCSV();
        saveAppointmentsToCSV();
    }

    private void saveDoctorsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTORS_FILE))) {
            writer.println("ID,Ονομα,Επίθετο,Τηλέφωνο,Email,ΗμΓέννησης,Ειδικότητα,ΑριθμόςΆδειας,Ιατρείο");

            for (Doctor d : doctors) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        d.getId(),
                        d.getName(),
                        d.getSurname(),
                        d.getPhone(),
                        d.getEmail(),
                        d.getBirthDate().format(DATE_FORMATTER),
                        d.getSpecialty().name(),
                        d.getLicenseNumber(),
                        d.getOffice()
                );
            }
            System.out.println("Doctors saved to CSV: " + doctors.size());
        } catch (IOException e) {
            System.err.println("Error saving doctors to CSV: " + e.getMessage());
        }
    }

    private void savePatientsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            writer.println("ID,Ονομα,Επίθετο,Τηλέφωνο,Email,ΗμΓέννησης,ΑΜΚΑ,ΙατρικόΙστορικό");

            for (Patient p : patients) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        p.getId(),
                        p.getName(),
                        p.getSurname(),
                        p.getPhone(),
                        p.getEmail(),
                        p.getBirthDate().format(DATE_FORMATTER),
                        p.getAmka(),
                        p.getMedicalHistory().replace(",", " ")
                );
            }
            System.out.println("Patients saved to CSV: " + patients.size());
        } catch (IOException e) {
            System.err.println("Error saving patients to CSV: " + e.getMessage());
        }
    }

    private void saveAppointmentsToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            writer.println("AppointmentID,PatientID,DoctorID,ΗμερομηνίαΏρα,Λόγος,Σημειώσεις,Ολοκληρώθηκε");

            for (Appointment a : appointments) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s%n",
                        a.getAppointmentId(),
                        a.getPatient().getId(),
                        a.getDoctor().getId(),
                        a.getDateTime().format(DATE_TIME_FORMATTER),
                        a.getReason().replace(",", " "),
                        a.getNotes().replace(",", " "),
                        a.isCompleted()
                );
            }
            System.out.println("Appointments saved to CSV: " + appointments.size());
        } catch (IOException e) {
            System.err.println("Error saving appointments to CSV: " + e.getMessage());
        }
    }

    // ==================== CRUD OPERATIONS ====================

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

    public Appointment getNextAppointment() {
        LocalDateTime now = LocalDateTime.now();
        Appointment next = null;

        for (Appointment a : appointments) {
            if (a.getDateTime().isAfter(now)) {
                if (next == null || a.getDateTime().isBefore(next.getDateTime())) {
                    next = a;
                }
            }
        }
        return next;
    }

    public String getPatientDoctorRatio() {
        int patientCount = patients.size();
        int doctorCount = doctors.size();
        if (doctorCount == 0) {
            return patientCount + ":0";
        }
        return patientCount + ":" + doctorCount;
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