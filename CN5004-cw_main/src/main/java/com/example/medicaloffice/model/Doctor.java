package com.example.medicaloffice.model;

import java.time.LocalDate;

public class Doctor extends Person {
    private static final long serialVersionUID = 1L;

    private Specialty specialty;
    private String licenseNumber;
    private String office;

    public Doctor(String id, String name, String surname, String phone,
                  String email, LocalDate birthDate, Specialty specialty,
                  String licenseNumber, String office) {
        super(id, name, surname, phone, email, birthDate);
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.office = office;
    }

    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getOffice() { return office; }
    public void setOffice(String office) { this.office = office; }

    @Override
    public String toString() {
        return super.toString() + " - " + specialty.getGreekName();
    }
}