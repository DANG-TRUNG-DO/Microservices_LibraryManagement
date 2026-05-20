package com.learnmicro.employeeservice.command.data;


import com.learnmicro.employeeservice.query.model.EmployeeResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findAllByIsDisciplined(Boolean isDisciplined);
}
