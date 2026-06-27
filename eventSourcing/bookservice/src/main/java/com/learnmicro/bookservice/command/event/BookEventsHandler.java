package com.learnmicro.bookservice.command.event;

import com.learnmicro.bookservice.command.data.Book;
import com.learnmicro.bookservice.command.data.BookRepository;
import com.learnmicro.commonservice.event.BookUpdateStatusEvent;
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
            bookUpdated.setId(event.getId());
            bookUpdated.setAuthor(event.getAuthor());
            bookUpdated.setName(event.getName());
            bookUpdated.setIsReady(event.getIsReady());
            bookRepository.save(bookUpdated);
            System.out.println("== Event Handler ==");
            System.out.println("ID: " + bookUpdated.getId());
            System.out.println("Name: " +  bookUpdated.getName() + " author: " + bookUpdated.getAuthor());
        }
    }

    @EventHandler
    public void on(BookUpdateStatusEvent event) {
        Optional<Book> oldBook = bookRepository.findById(event.getBookId());
        oldBook.ifPresent(book -> {
            book.setIsReady(event.getIsReady());
            bookRepository.save(book);
        });
    }

    @EventHandler
    public void on(BookDeletedEvent event) {
        Optional<Book> book = bookRepository.findById(event.getId());
        book.ifPresent(value -> bookRepository.delete(value));
    }
}
