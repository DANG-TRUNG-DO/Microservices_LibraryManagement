package com.learnmicro.employeeservice.query.controller;

import com.learnmicro.employeeservice.query.model.EmployeeResponseModel;
import com.learnmicro.employeeservice.query.queries.GetAllEmployeeQuery;
import com.learnmicro.employeeservice.query.queries.GetDetailEmployeeQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    public List<EmployeeResponseModel> getEmployees(@RequestParam(required = false, defaultValue = "false") Boolean isDisciplined) {
        List<EmployeeResponseModel> list = queryGateway
                .query(new GetAllEmployeeQuery(isDisciplined), ResponseTypes.multipleInstancesOf(EmployeeResponseModel.class)).join();
        return list;
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponseModel getEmployee(@PathVariable String employeeId) {
        return queryGateway.query(new GetDetailEmployeeQuery(employeeId), ResponseTypes.instanceOf(EmployeeResponseModel.class)).join();
    }
}
