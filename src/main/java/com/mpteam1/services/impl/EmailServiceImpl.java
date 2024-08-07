package com.mpteam1.services.impl;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.mpteam1.entities.enums.EEmailStatus;
import com.mpteam1.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/13/2024, Sat
 **/


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.api.public}")
    private String api;
    @Value("${spring.mail.api.secret}")
    private String secret;
    @Value("${spring.mail.client.hostname}")
    private String hostname;
    @Value("${spring.mail.client.port}")
    private String port;
    @Value("${spring.mail.client.from}")
    private String from;

    @Override
    public void sendHtmlMessage(EEmailStatus eEmailStatus, String to, String subject, String content) {
        MailjetClient client;
        MailjetRequest request;
        client = new MailjetClient(ClientOptions.builder()
                .apiKey(api)
                .apiSecretKey(secret)
                .build());
        if (Objects.requireNonNull(eEmailStatus) == EEmailStatus.RESTORE) {
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", from)
                                            .put("Name", "Mock Team 1"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", to)
                                                    .put("Name", to)))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, "<html><body>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Dear User,</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>This is a automatic message from Mock Project - Team 1</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Here is your <strong>link to reset password: " + hostname + ":" + port + "/reset-password?token=" + content + "</strong></p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Regards,</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Team 1</p>"
                                            + "</body></html>")));
        } else {
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", from)
                                            .put("Name", "Mock Team 1"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", to)
                                                    .put("Name", to)))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, "<html><body>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Dear User,</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>This is a automatic message from Mock Project - Team 1</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Your account was registered successfully:</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif;'>Here is your email: <strong>" + to + "</strong> to login to system</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Regards,</p>"
                                            + "<p style='font-family: Arial, Helvetica, sans-serif; color: #333;'>Team 1</p>"
                                            + "</body></html>")));
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.execute(() -> {
            try {
                client.post(request);
            } catch (MailjetException e) {
                log.error(e.getMessage());
            }
        });
    }
}
