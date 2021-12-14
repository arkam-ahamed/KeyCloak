package com.example.KeyCloak.dao;


import com.example.KeyCloak.controller.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, UUID> {
    boolean existsById(ObjectId id);

    Employee findById(ObjectId id);

    Employee deleteById(ObjectId id);
}
