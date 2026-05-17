package com.learnmicro.bookservice.command.controller;

import com.learnmicro.bookservice.command.command.CreateBookCommand;
import com.learnmicro.bookservice.command.command.DeleteBookCommand;
import com.learnmicro.bookservice.command.command.UpdateBookCommand;
import com.learnmicro.bookservice.command.model.BookRequestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookCommandController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addBook(@RequestBody BookRequestModel model) {
        CreateBookCommand createBookCommand = new CreateBookCommand(UUID.randomUUID().toString(), model.getName(), model.getAuthor(), true);
        return commandGateway.sendAndWait(createBookCommand);
    }

    @PutMapping("/{bookId}")
    public String updateBook(@RequestBody BookRequestModel model, @PathVariable String bookId) {
        System.out.println("== Book Command Controller ==");
        System.out.println("ID: " + bookId);
        System.out.println("Name: " + model.getName());
        System.out.println("Author: " + model.getAuthor());
        UpdateBookCommand updateBookCommand = new UpdateBookCommand(bookId, model.getName(), model.getAuthor(), model.getIsReady());
        return commandGateway.sendAndWait(updateBookCommand);
    }

    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable String bookId) {
        DeleteBookCommand deleteBookCommand = new DeleteBookCommand(bookId);
        return commandGateway.sendAndWait(deleteBookCommand);
    }
}
