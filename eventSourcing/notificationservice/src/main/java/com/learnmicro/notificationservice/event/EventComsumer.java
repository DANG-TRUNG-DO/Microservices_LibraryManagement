package com.learnmicro.notificationservice.event;

import com.learnmicro.commonservice.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventComsumer {

    @Autowired
    private EmailService emailService;


    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            include = {RetriableException.class, RuntimeException.class}
    )
    @KafkaListener(topics = "test", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info(message);
        //processing messages
    }

    @DltHandler
    void processDltMessage(@Payload String message) {
        log.info("DLT received message: " + message);
    }

    @KafkaListener(topics = "testEmail", containerFactory = "kafkaListenerContainerFactory")
    public void testEmail(String message) {
        log.info("Received email: " + message);

        String template = "<div>\n" +
                "   <h1> Welcome, %s!</h1>\n" +
                "   <p>Thank you for joining us. We're excieted to have you on board.</p>\n" +
                "   <p>Your username is: <strong>%s</strong></p>\n" +
                "</div>";
        String filledTemplate = String.format(template,"Trung Do", message);
        emailService.sendEmail(message, "This is test email", filledTemplate, true, null);
    }

    @KafkaListener(topics = "emailTemplate", containerFactory = "kafkaListenerContainerFactory")
    public void emailTemplate(String message) {
        log.info("Received email template: " + message);
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("name", "Trung Do");
        emailService.sendEmailWithTemplate(message, "This is email with template", "emailTemplate.ftl",placeholders,null);
    }
}
