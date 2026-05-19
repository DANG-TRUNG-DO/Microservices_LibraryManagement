package com.learnmicro.employeeservice.command.controller;

import com.learnmicro.employeeservice.command.command.CreateEmployeeCommand;
import com.learnmicro.employeeservice.command.command.DeleteEmployeeCommand;
import com.learnmicro.employeeservice.command.command.UpdateEmployeeCommand;
import com.learnmicro.employeeservice.command.data.Employee;
import com.learnmicro.employeeservice.command.model.CreateEmployeeModel;
import com.learnmicro.employeeservice.command.model.UpdateEmployeeModel;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addEmployee(@Valid @RequestBody CreateEmployeeModel model) {
        CreateEmployeeCommand command =
                new CreateEmployeeCommand(UUID.randomUUID().toString(), model.getFirstName(), model.getLastName(), model.getKin(), false);
        return commandGateway.sendAndWait(command);
    }

    @PutMapping("/{employeeID}")
    public String updateEmployee(@Valid @RequestBody UpdateEmployeeModel model, @PathVariable("employeeID") String employeeID) {
        UpdateEmployeeCommand command =
                new UpdateEmployeeCommand(employeeID, model.getFirstName(), model.getLastName(), model.getKin(), model.getIsDisciplined());
        return commandGateway.sendAndWait(command);
    }

    @DeleteMapping("/{employeeID}")
    public String deleteEmployee(@PathVariable("employeeID") String employeeID) {
        DeleteEmployeeCommand command =
                new DeleteEmployeeCommand(employeeID);
        return commandGateway.sendAndWait(command);
    }

}
