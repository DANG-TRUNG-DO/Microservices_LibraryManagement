package com.learnmicro.bookservice.command.aggregate;

import com.learnmicro.bookservice.command.command.CreateBookCommand;
import com.learnmicro.bookservice.command.event.BookCreatedEvent;
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


    //listen event by event sourcing handler
    @EventSourcingHandler
    public void on(BookCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();

    }
}
