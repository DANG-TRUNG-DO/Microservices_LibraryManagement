package com.learnmicro.borrowingservice.command.saga;

import com.learnmicro.borrowingservice.command.command.DeleteBorrowingCommand;
import com.learnmicro.borrowingservice.command.event.BorrowingDeletedEvent;
import com.learnmicro.commonservice.command.RollBackStatusBookCommand;
import com.learnmicro.commonservice.event.BookRollBackStatusEvent;
import com.learnmicro.commonservice.model.EmployeeResponseCommonModel;
import com.learnmicro.commonservice.queries.GetDetailEmployeeQuery;
import com.learnmicro.borrowingservice.command.event.BorrowingCreatedEvent;
import com.learnmicro.commonservice.command.UpdateStatusBookCommand;
import com.learnmicro.commonservice.event.BookUpdateStatusEvent;
import com.learnmicro.commonservice.model.BookResponseCommonModel;
import lombok.extern.slf4j.Slf4j;
import com.learnmicro.commonservice.queries.GetBookDetailQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class BorrowingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
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
                UpdateStatusBookCommand command = new UpdateStatusBookCommand(event.getBookId(),false, event.getEmployeeId(), event.getId());
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
        try {
            GetDetailEmployeeQuery query = new GetDetailEmployeeQuery(event.getEmployeeId());
            EmployeeResponseCommonModel employeeModel = queryGateway.query(query,ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)).join();
            if(employeeModel.getIsDisciplined()){
                throw new Exception("The employee is disciplined");
            }else{
                log.info("Borrwing successfully in Saga for bookId: " + event.getBookId());
                SagaLifecycle.end();
            }

        }catch (Exception ex){
            rollBackBookStatus(event.getBookId(), event.getEmployeeId(), event.getBorrowingId());
            log.error(ex.getMessage());
        }
        SagaLifecycle.end();
    }

    private void rollBackBorrowingRecord(String id) {
        DeleteBorrowingCommand command = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(command);
        SagaLifecycle.end();
    }

    private void rollBackBookStatus(String bookId, String employeeId,  String borrowingId) {
        SagaLifecycle.associateWith("bookId", bookId);
        RollBackStatusBookCommand command = new RollBackStatusBookCommand(bookId, true, employeeId, borrowingId);
        commandGateway.sendAndWait(command);
    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BookRollBackStatusEvent event) {
        log.info("RollBackStatusBookCommand for bookId: " + event.getBookId());
        rollBackBorrowingRecord(event.getBorrowingId());
    }

    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingDeletedEvent event) {
        log.info("BorrowingDeletedEvent for bookId: " + event.getId());
        SagaLifecycle.end();
    }
}
