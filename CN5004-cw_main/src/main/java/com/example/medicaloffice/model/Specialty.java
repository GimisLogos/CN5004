package com.example.medicaloffice.model;

public enum Specialty {
    CARDIOLOGIST("Καρδιολόγος"),
    DERMATOLOGIST("Δερματολόγος"),
    ORTHOPEDIC("Ορθοπεδικός"),
    PEDIATRICIAN("Παιδίατρος"),
    GYNECOLOGIST("Γυναικολόγος"),
    NEUROLOGIST("Νευρολόγος"),
    OPHTHALMOLOGIST("Οφθαλμίατρος"),
    GENERAL_PRACTITIONER("Γενικός Ιατρός");

    private final String greekName;

    Specialty(String greekName) {
        this.greekName = greekName;
    }

    public String getGreekName() {
        return greekName;
    }

    @Override
    public String toString() {
        return greekName;
    }
}