package com.example.KeyCloak.controller;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "employee")
public class Employee {
    private ObjectId id;
    private String name;
    private int age;

    public Employee() {

    }
}
