package com.bankapp.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;

@Service
public class MailService implements IMailService {

    private final Logger logger = Logger.getLogger(MailService.class);

    @Value("${com.bankapp.email.mail_aki_key}")
    private String apiKey;

    @Override
    public void sendEmail(String recipientAddress, String subject, String textBody) {

        try {
            SendGrid sendgrid = new SendGrid(apiKey);

            Email email = new Email();
            email.addTo(recipientAddress);
            email.setFrom("bank.myasu@gmail.com");
            email.setSubject(subject);
            email.setHtml(textBody);

            SendGrid.Response response = sendgrid.send(email);

            String logMessageFormat = "[Action=%s][Recipient=%s, Subject=%s, Message=%s]";
            String logMessage = String.format(logMessageFormat, "sendEmail", recipientAddress, subject, response.getMessage());
            logger.info(logMessage);

        } catch (SendGridException e) {
            String logMessageFormat = "[Action=%s][Recipient=%s, Subject=%s, ErrorMessage=%s]";
            String logMessage = String.format(logMessageFormat, "sendEmail", recipientAddress, subject, e.getMessage());
            logger.error(logMessage);

            throw new RuntimeException("Sorry, seems that our email service is down. Please contact administrator.");
        }
    }

}
