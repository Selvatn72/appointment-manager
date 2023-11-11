package com.aptmgt.customer.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

@Configuration
public class MailConfig {
    private String host;
    private int port;
    private String userName;
    private String password;

    @Value("${spring.SMTP.mail.host}")
    public void setHost(String host) {
        this.host = host;
    }

    @Value("${spring.SMTP.mail.port}")
    public void setPort(int port) {
        this.port = port;
    }

    @Value("${spring.SMTP.mail.username}")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Value("${spring.SMTP.mail.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("templates/mail");
        return bean;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        return mailSender;
    }
}