package com.learnmicro.bookservice.command.aggregate;

import com.learnmicro.bookservice.command.command.CreateBookCommand;
import com.learnmicro.bookservice.command.command.DeleteBookCommand;
import com.learnmicro.bookservice.command.command.UpdateBookCommand;
import com.learnmicro.bookservice.command.event.BookCreatedEvent;
import com.learnmicro.bookservice.command.event.BookDeletedEvent;
import com.learnmicro.bookservice.command.event.BookUpdatedEvent;
import com.learnmicro.commonservice.command.RollBackStatusBookCommand;
import com.learnmicro.commonservice.command.UpdateStatusBookCommand;
import com.learnmicro.commonservice.event.BookRollBackStatusEvent;
import com.learnmicro.commonservice.event.BookUpdateStatusEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@Aggregate
public class BookAggregate {

    @AggregateIdentifier
    private String id;
    private String name;
    private String author;
    private Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateBookCommand command) {
        BookCreatedEvent bookCreatedEvent = new BookCreatedEvent();
        //copy all attributes from command to event
        BeanUtils.copyProperties(command, bookCreatedEvent);

        //publish event
        AggregateLifecycle.apply(bookCreatedEvent);
    }

    @CommandHandler
    public void handle(UpdateBookCommand command) {
        BookUpdatedEvent bookUpdatedEvent = new BookUpdatedEvent();
        BeanUtils.copyProperties(command, bookUpdatedEvent);
        AggregateLifecycle.apply(bookUpdatedEvent);
        System.out.println("== Command Handler ==");
        System.out.println("Book ID: " + command.getId());
        System.out.println("Book Name: " + command.getName());
        System.out.println("Book Author: " + command.getAuthor());
    }

    @CommandHandler
    public void handle(DeleteBookCommand command) {
        BookDeletedEvent bookDeletedEvent = new BookDeletedEvent();
        BeanUtils.copyProperties(command, bookDeletedEvent);
        AggregateLifecycle.apply(bookDeletedEvent);
    }

    @CommandHandler
    public void handle(UpdateStatusBookCommand command) {
        BookUpdateStatusEvent event = new BookUpdateStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(RollBackStatusBookCommand command) {
        BookRollBackStatusEvent event = new BookRollBackStatusEvent();
        BeanUtils.copyProperties(command, event);
        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(BookRollBackStatusEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookUpdateStatusEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }


    //listen event by event sourcing handler
    @EventSourcingHandler
    public void on(BookCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @EventSourcingHandler
    public void on(BookUpdatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
        System.out.println("== Event Sourcing Handler ==");
        System.out.println("ID: " +  this.id);
        System.out.println("Name: " +  this.name);
        System.out.println("Author: " +  this.author);
    }

    @EventSourcingHandler
    public void on(BookDeletedEvent event) {
        this.id = event.getId();
    }
}
