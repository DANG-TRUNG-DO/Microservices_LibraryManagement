package com.learnmicro.bookservice.query.controller;

import com.learnmicro.bookservice.query.model.BookResponseModel;
import com.learnmicro.bookservice.query.queries.GetAllBookQuery;
import com.learnmicro.bookservice.query.queries.GetBookDetailQuery;
import lombok.Getter;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/books")
public class BookQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    public List<BookResponseModel> getALLBooks() {
        GetAllBookQuery query = new GetAllBookQuery();
        //asynchronous
//        CompletableFuture<List<BookResponseModel>> bookFuture = queryGateway.query(query, ResponseTypes.multipleInstancesOf(BookResponseModel.class));
//        bookFuture.thenAccept(books -> {
//
//        });

        //synchronous
        List<BookResponseModel> result = queryGateway.query(query, ResponseTypes.multipleInstancesOf(BookResponseModel.class)).join();
        return result;
    }

    @GetMapping("/{bookId}")
    public BookResponseModel getBookDetail(@PathVariable String bookId) {
        GetBookDetailQuery query = new GetBookDetailQuery(bookId);
        return queryGateway.query(query, ResponseTypes.instanceOf(BookResponseModel.class)).join();
    }
}
