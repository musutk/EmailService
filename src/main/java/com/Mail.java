package com;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@ToString
public class Mail {

    @ToString.Exclude
    private final MailServer msc;
    @ToString.Exclude
    private final Session session;
    @ToString.Exclude
    private final MimeMessage msg;

    private String sender;
    private String senderName;

    private String subject;
    private String htmlBody;

    private List<InternetAddress> recipients = new ArrayList<>(); //rec type hashmap

    public Mail(MailServer msc) {
        this.msc = msc;
        this.session = msc.configureSession();
        this.msg = new MimeMessage(session);
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSender(String sender, String senderName) {
        this.sender = sender;
        this.senderName = senderName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public void setRecipients(List<InternetAddress> recipients) {
        this.recipients = recipients;
    }

    public void addRecipient(String recipientAddress, Message.RecipientType recipientType) {
        log.log(Level.INFO, "Attempting to attach recipient");
        try {
            this.recipients.add(new InternetAddress(recipientAddress));
            log.log(Level.INFO, "Success in attaching recipient");
        } catch (AddressException e) {
            var message = "Could not add" + recipientAddress;
            log.log(Level.ERROR, message);
            throw new NullPointerException(message);
        }
    }

    private void attachRecipients() {
        log.log(Level.INFO, "Attempting to attach recipients");
        InternetAddress[] arrayRec = this.recipients.toArray(InternetAddress[]::new);
        try {
            this.msg.setRecipients(Message.RecipientType.TO, arrayRec);
            log.log(Level.INFO, "Success in attaching recipients");
        } catch (MessagingException e) {
            var message = "Error occurred when attaching recipients";
            log.log(Level.ERROR, message);
            throw new NullPointerException(message);
        }
    }

    private void attachSender() {
        log.log(Level.INFO, "Attempting to attach sender");
        try {
            this.msg.setFrom(new InternetAddress(this.sender, this.senderName));
            log.log(Level.INFO, "Success in attaching sender");
        } catch (MessagingException | UnsupportedEncodingException e) {
            var message = "could not add sender: " + this.sender;
            log.log(Level.ERROR, message);
            throw new NullPointerException(message);
        }

    }

    private void attachSubject() {
        log.log(Level.INFO, "Attempting to attach subject message");
        try {
            this.msg.setSubject(subject);
            log.log(Level.INFO, "Success in attaching subject");
        } catch (MessagingException e) {
            var message = "could not attach subject: " + subject;
            log.log(Level.ERROR, message);
            throw new NullPointerException(message);
        }
    }

    private void attachContent() {
        log.log(Level.INFO, "Attempting to attach content");
        try {
            this.msg.setContent(htmlBody, "text/html");
            log.log(Level.INFO, "Success in attaching content");
        } catch (MessagingException e) {
            log.log(Level.ERROR, "Error Email content was not sent");
            log.log(Level.ERROR, e.getMessage());
        }
    }

    private void finaliseMimeMessage() {
        log.log(Level.INFO, "Building Email");
        attachSubject();
        attachSender();
        attachRecipients();
        attachContent();
        log.log(Level.INFO, "Email Building finished");
    }

    private void sendMessage(Transport transport) {
        log.log(Level.INFO, "Attempting to send message");
        try {
            transport.sendMessage(msg, msg.getAllRecipients());
            log.log(Level.INFO, "Email sent successfully " + this);
        } catch (MessagingException e) {
            msc.addToFailedEmailList(this);
            log.log(Level.ERROR, "Error Email was not sent");
            log.log(Level.ERROR, e.getMessage());
        }
    }

    public void sendEmail() {
        finaliseMimeMessage();
        var transport = msc.generateSessionTransport(session);
        msc.transportConnection(transport);
        sendMessage(transport);
        msc.closeTransportConnection(transport);
    }

}