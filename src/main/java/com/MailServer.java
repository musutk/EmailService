package com;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailServer {

    private int port;
    private String host;
    private String smtpUsername;
    private String smtpAuthString;
    private List<Mail> failedEmails = new ArrayList<>();

    public void addToFailedEmailList(Mail failedMailObject) {
        failedEmails.add(failedMailObject);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public void setSmtpAuthString(String smtpAuthString) {
        this.smtpAuthString = smtpAuthString;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Session configureSession() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return Session.getDefaultInstance(props);
    }

    public Transport generateSessionTransport(Session session) {
        try {
            return session.getTransport();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("Cannot prepare transport for");
    }

    public void transportConnection(Transport transport) {
        try {
            transport.connect(host, smtpUsername, smtpAuthString);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new NullPointerException(" Cannot connect to " + host);
        }
    }

    public void closeTransportConnection(Transport transport) {
        try {
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new NullPointerException(" Cannot close connection to " + host);
        }
    }


}
