package com.learnmicro.bookservice.query.projection;

import com.learnmicro.bookservice.command.data.Book;
import com.learnmicro.bookservice.command.data.BookRepository;
import com.learnmicro.bookservice.query.model.BookResponseModel;
import com.learnmicro.bookservice.query.queries.GetAllBookQuery;
import com.learnmicro.commonservice.model.BookResponseCommonModel;
import com.learnmicro.commonservice.queries.GetBookDetailQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookProjection {

    @Autowired
    private BookRepository bookRepository;

    @QueryHandler
    public List<BookResponseModel> handle(GetAllBookQuery query) {
        List<Book> list = bookRepository.findAll();
        List<BookResponseModel> bookResponseModels = new ArrayList<>();
        list.forEach(book -> {
            BookResponseModel model = new BookResponseModel();
            BeanUtils.copyProperties(book, model);
            bookResponseModels.add(model);
        });

        //using stream api instead
//        List<BookResponseModel> result = list.stream().map(book -> {
//            BookResponseModel model = new BookResponseModel();
//            BeanUtils.copyProperties(book, model);
//            return model;
//        }).toList();
        return bookResponseModels;
    }

    @QueryHandler
    public BookResponseCommonModel handle(GetBookDetailQuery query) throws Exception {
        BookResponseCommonModel bookResponseModel = new BookResponseCommonModel();
        Book book = bookRepository.findById(query.getId()).orElseThrow(() -> new Exception("Book not found with id " + query.getId()));
        BeanUtils.copyProperties(book, bookResponseModel);
        return bookResponseModel;
    }
}
