package com.learnmicro.employeeservice.command.event;

import com.learnmicro.employeeservice.command.data.Employee;
import com.learnmicro.employeeservice.command.data.EmployeeRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
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
    public void on(EmployeeUpdatedEvent event) {
        Optional<Employee> oldEmployee = employeeRepository.findById(event.getId());
        oldEmployee.ifPresent(employee -> {
            employee.setLastName(event.getLastName());
            employee.setFirstName(event.getFirstName());
            employee.setKin(event.getKin());
            employee.setIsDisciplined(event.getIsDisciplined());
            employeeRepository.save(employee);
        });
    }

    @EventHandler
    @DisallowReplay
    public void on(EmployeeDeletedEvent event) {
        try {
            employeeRepository.deleteById(event.getId());
        } catch (Exception ex) {
            log.error("Delete employee failed", ex);
        }
    }
}
