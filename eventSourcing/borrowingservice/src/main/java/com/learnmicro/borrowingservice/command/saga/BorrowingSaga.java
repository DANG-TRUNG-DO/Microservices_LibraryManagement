package com.learnmicro.borrowingservice.command.saga;

import com.learnmicro.borrowingservice.command.command.DeleteBorrowingCommand;
import com.learnmicro.borrowingservice.command.event.BorrowingCreatedEvent;
import com.learnmicro.commonservice.command.UpdateStatusBookCommand;
import com.learnmicro.commonservice.event.BookUpdateStatusEvent;
import com.learnmicro.commonservice.model.BookResponseCommonModel;
import lombok.extern.slf4j.Slf4j;
import com.learnmicro.commonservice.queries.GetBookDetailQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class BorrowingSaga {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent event) {
        log.info("BorrowingSaga for bookId: " + event.getBookId() + " and employeeId: " + event.getEmployeeId());
        try {
            GetBookDetailQuery getBookDetailQuery =  new GetBookDetailQuery(event.getBookId());
            BookResponseCommonModel bookResponseCommonModel = queryGateway.query(getBookDetailQuery,
                    ResponseTypes.instanceOf(BookResponseCommonModel.class)).join();
            if (!bookResponseCommonModel.getIsReady()) {
                throw new Exception("Someone has borrowed the book");
            } else {
                SagaLifecycle.associateWith("bookId", event.getBookId());
                UpdateStatusBookCommand command = new UpdateStatusBookCommand();
                commandGateway.sendAndWait(command);
            }
        } catch (Exception e) {
            rollBackBorrowingRecord(event.getId());
            log.error("Error occurred while processing BorrowingCreatedEvent", e);
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BookUpdateStatusEvent event) {
        log.info("BookUpdateStatusEvent in Saga for bookId: " + event.getBookId());
        SagaLifecycle.end();
    }

    private void rollBackBorrowingRecord(String id) {
        DeleteBorrowingCommand command = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(command);
        SagaLifecycle.end();
    }
}
