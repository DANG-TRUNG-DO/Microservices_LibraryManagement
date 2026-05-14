package com.learnmicro.bookservice.command.event;

import com.learnmicro.bookservice.command.data.Book;
import com.learnmicro.bookservice.command.data.BookRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BookEventsHandler {

    @Autowired
    private BookRepository bookRepository;

    @EventHandler
    public void on(BookCreatedEvent event) {
        Book book = new Book();
        BeanUtils.copyProperties(event, book);
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookUpdatedEvent event) {
        Optional<Book> book = bookRepository.findById(event.getId());
        if (book.isPresent()) {
            Book bookUpdated = new Book();
            bookUpdated.setId(book.get().getId());
            bookUpdated.setAuthor(book.get().getAuthor());
            bookUpdated.setName(book.get().getName());
            bookUpdated.setIsReady(book.get().getIsReady());
            bookRepository.save(bookUpdated);
        }
    }

    @EventHandler
    public void on(BookDeletedEvent event) {
        Optional<Book> book = bookRepository.findById(event.getId());
        book.ifPresent(value -> bookRepository.delete(value));
    }
}
