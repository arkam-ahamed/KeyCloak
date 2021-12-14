package com.example.KeyCloak.controller;


import com.example.KeyCloak.dao.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Service
public class EmployeeService {
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeService() {

    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }


    public String save(Employee employee,HttpServletResponse response,String Authorization) throws JsonProcessingException, JSONException {
        JSONObject responseJson = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        if (employee.getName() == null || ("").equals(employee.getAge())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        employee = employeeRepository.save(employee);
        JSONObject studentJSON = new JSONObject(mapper.writeValueAsString(employee));
        studentJSON.put("id", employee.getId().toHexString());
        responseJson.put("saveEmployeeResponse", studentJSON);

        return responseJson.toString();

    }

    public String deleteById(ObjectId id, HttpServletResponse response) throws JSONException {
        JSONObject responseJson = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        boolean exists = employeeRepository.existsById(id);

        if (!exists) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            responseJson.put("deleted employee with id ", id);
            employeeRepository.deleteById(id);



        }
        return responseJson.toString();
    }


    @Transactional
    public String updateEmployee(ObjectId id, Employee employee, HttpServletResponse response) throws JsonProcessingException, JSONException {
        JSONObject responseJson = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        Employee employee1 = employeeRepository.findById(id);


        boolean exists = employeeRepository.existsById(id);

        if (!exists) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        if(employee.getName()!=null) {
            employee1.setName(employee.getName());
            employee1.setAge(employee.getAge());
            employeeRepository.save(employee1);



            JSONObject studentJSON = new JSONObject(mapper.writeValueAsString(employee1));
            studentJSON.put("id", employee1.getId().toHexString());
            responseJson.put("getUpdatedEmployeeResponse", studentJSON);
        }




        return responseJson.toString();

    }


}
