package com.example.KeyCloak.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.util.List;



@RestController
@RequestMapping("/api")
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/anonymous", method = RequestMethod.GET)
    public ResponseEntity<String> getAnonymous() {
        return ResponseEntity.ok("Hello Anonymous");
    }



    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addEmployee(@RequestHeader String Authorization , @RequestBody Employee employee,HttpServletResponse response) throws JSONException, JsonProcessingException {
        return employeeService.save(employee,response,Authorization);

    }



    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public List<Employee> getEmployees(@RequestHeader String Authorization) {
        return employeeService.getAllEmployees();
    }



    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteStudent(@RequestHeader String Authorization , @PathVariable ObjectId id, HttpServletResponse response) throws JSONException {
        return employeeService.deleteById(id,response);
    }




    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public String updateEmployees(@RequestHeader String Authorization, @PathVariable ObjectId id, @RequestBody Employee employee, HttpServletResponse response) throws JSONException, JsonProcessingException {
        return employeeService.updateEmployee(id, employee, response);
    }

}