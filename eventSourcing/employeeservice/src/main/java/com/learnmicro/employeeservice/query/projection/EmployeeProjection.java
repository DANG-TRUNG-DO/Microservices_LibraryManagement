package com.learnmicro.employeeservice.query.projection;

import com.learnmicro.employeeservice.command.data.Employee;
import com.learnmicro.employeeservice.command.data.EmployeeRepository;
import com.learnmicro.employeeservice.query.model.EmployeeResponseModel;
import com.learnmicro.employeeservice.query.queries.GetAllEmployeeQuery;
import com.learnmicro.employeeservice.query.queries.GetDetailEmployeeQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmployeeProjection {

    @Autowired
    private EmployeeRepository employeeRepository;

    @QueryHandler
    public List<EmployeeResponseModel> handle(GetAllEmployeeQuery query) {
        List<Employee> employees = employeeRepository.findAllByIsDisciplined(query.getIsDisciplined());
        return employees.stream().map(employee -> {
            EmployeeResponseModel employeeResponseModel = new EmployeeResponseModel();
            BeanUtils.copyProperties(employee, employeeResponseModel);
            return employeeResponseModel;
        }).toList();
    }

    @QueryHandler
    public EmployeeResponseModel handle(GetDetailEmployeeQuery query) throws Exception {
        Employee employee = employeeRepository.findById(query.getId()).orElseThrow(() -> new Exception("Employee not found"));
        EmployeeResponseModel employeeResponseModel = new EmployeeResponseModel();
        BeanUtils.copyProperties(employee, employeeResponseModel);
        return employeeResponseModel;
    }
}
