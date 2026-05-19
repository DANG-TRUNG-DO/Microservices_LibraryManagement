package com.learnmicro.employeeservice.command.event;

import com.learnmicro.employeeservice.command.data.Employee;
import com.learnmicro.employeeservice.command.data.EmployeeRepository;
import jakarta.ws.rs.NotFoundException;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmployeeEventHandler {

    @Autowired
    private EmployeeRepository  employeeRepository;

    @EventHandler
    public void on(EmployeeCreatedEvent event) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(event, employee);
        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeUpdatedEvent event) throws Exception {
        Optional<Employee> oldEmployee = employeeRepository.findById(event.getId());
        Employee employee = oldEmployee.orElseThrow(() -> new Exception("Employee not found"));
        employee.setLastName(event.getLastName());
        employee.setFirstName(event.getFirstName());
        employee.setKin(event.getKin());
        employee.setIsDisciplined(event.getIsDisciplined());
        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeDeletedEvent event) throws Exception {
        employeeRepository.findById(event.getId()).orElseThrow(() -> new Exception("Employee not found"));
        employeeRepository.deleteById(event.getId());
    }
}
