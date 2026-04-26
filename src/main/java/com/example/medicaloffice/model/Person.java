package com.example.medicaloffice.model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String surname;
    protected String phone;
    protected String email;
    protected LocalDate birthDate;

    public Person(String id, String name, String surname, String phone,
                  String email, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getFullName() {
        return name + " " + surname;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + id + ")";
    }
}