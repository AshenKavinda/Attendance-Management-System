package com.attendance.model;

import java.sql.Date;

public class Student {

    private int    id;
    private String firstName;
    private String lastName;
    private Date   dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;

    public Student() {}

    public Student(int id, String firstName, String lastName, Date dateOfBirth,
                   String gender, String email, String phone, String address) {
        this.id          = id;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender      = gender;
        this.email       = email;
        this.phone       = phone;
        this.address     = address;
    }

    public int    getId()            { return id; }
    public void   setId(int id)      { this.id = id; }

    public String getFirstName()               { return firstName; }
    public void   setFirstName(String v)       { this.firstName = v; }

    public String getLastName()                { return lastName; }
    public void   setLastName(String v)        { this.lastName = v; }

    public String getFullName()                { return firstName + " " + lastName; }

    public Date   getDateOfBirth()             { return dateOfBirth; }
    public void   setDateOfBirth(Date v)       { this.dateOfBirth = v; }

    public String getGender()                  { return gender; }
    public void   setGender(String v)          { this.gender = v; }

    public String getEmail()                   { return email; }
    public void   setEmail(String v)           { this.email = v; }

    public String getPhone()                   { return phone; }
    public void   setPhone(String v)           { this.phone = v; }

    public String getAddress()                 { return address; }
    public void   setAddress(String v)         { this.address = v; }

    @Override
    public String toString() { return firstName + " " + lastName; }
}
