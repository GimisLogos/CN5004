package com.example.medicaloffice.model;

import java.time.LocalDate;

public class Patient extends Person {
    private static final long serialVersionUID = 1L;

    private String amka;
    private String medicalHistory;

    public Patient(String id, String name, String surname, String phone,
                   String email, LocalDate birthDate, String amka,
                   String medicalHistory) {
        super(id, name, surname, phone, email, birthDate);
        this.amka = amka;
        this.medicalHistory = medicalHistory;
    }


    public String getAmka() { return amka; }
    public void setAmka(String amka) { this.amka = amka; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    @Override
    public String toString() {
        return super.toString() + " (ΑΜΚΑ: " + amka + ")";
    }
}