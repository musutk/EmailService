package com;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import javax.mail.Message;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Log4j2
public class Email {

    public void sendEmail(String body, String emailTo, String subject) {
        configureMail(body, emailTo, subject).sendEmail();
    }

    private MailServer configureServer() {
        log.log(Level.INFO, "Attempting to send configure/prepare Email Server");
        var properties = loadProps();
        var msc = new MailServer();
        msc.setPort(Integer.parseInt(properties.getProperty("port")));
        msc.setHost(properties.getProperty("host"));
        msc.setSmtpUsername(properties.getProperty("smtpUsername"));
        msc.setSmtpAuthString(properties.getProperty("smtpAuthString"));
        log.log(Level.INFO, "Smtp configuration setup complete");
        return msc;
    }

    private Mail configureMail(String body, String emailTo, String subject) {
        log.log(Level.INFO, "Attempting to send configure/prepareEmail");
        var properties = loadProps();
        var mail = new Mail(configureServer());
        mail.setSender(properties.getProperty("emailFrom"), properties.getProperty("emailFromName"));
        mail.setSubject(subject);
        mail.addRecipient(emailTo, Message.RecipientType.BCC);
        mail.setHtmlBody(body);
        log.log(Level.INFO, "Email prepared" + mail);
        return mail;
    }

    private Properties loadProps() {
        var properties = new Properties();
        try (var fis = new FileInputStream((System.getProperty("smtp.properties.path")))) {
            properties.load(fis);
            return properties;
        } catch (IOException var6) {
            var message = "Error reading smtp properties file";
            log.log(Level.ERROR, message);
            throw new NullPointerException(message);
        }
    }

}