package com.profid.profid.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_student_name", columnList = "name"),
        @Index(name = "idx_student_email", columnList = "email", unique = true)
})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer age;

    private String gender;

    private String phoneNumber;

    private String address;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects;

    public Student() {
    }

    public Student(Long id, String name, String email, Integer age, String gender, String phoneNumber, String address, List<Subject> subjects) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.subjects = subjects;
    }

    public Student(String name, String mail, int age, String gender, String number, String address, List<Subject> subjects) {
        this.name = name;
        this.email = mail;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = number;
        this.address = address;
        this.subjects = subjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
