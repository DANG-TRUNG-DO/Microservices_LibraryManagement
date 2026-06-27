package com.learnmicro.employeeservice.query.projection;

import com.learnmicro.commonservice.model.EmployeeResponseCommonModel;
import com.learnmicro.employeeservice.command.data.Employee;
import com.learnmicro.employeeservice.command.data.EmployeeRepository;
import com.learnmicro.employeeservice.query.model.EmployeeResponseModel;
import com.learnmicro.employeeservice.query.queries.GetAllEmployeeQuery;
import com.learnmicro.commonservice.queries.GetDetailEmployeeQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public EmployeeResponseCommonModel handle(GetDetailEmployeeQuery query) throws Exception {
        Employee employee = employeeRepository.findById(query.getId()).orElseThrow(() -> new Exception("Employee not found"));
        EmployeeResponseCommonModel employeeResponseModel = new EmployeeResponseCommonModel();
        BeanUtils.copyProperties(employee, employeeResponseModel);
        return employeeResponseModel;
    }
}
