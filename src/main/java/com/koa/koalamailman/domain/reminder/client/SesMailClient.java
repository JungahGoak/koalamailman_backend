package com.koa.koalamailman.domain.reminder.client;

import com.koa.koalamailman.domain.reminder.dto.EmailMessage;
import com.koa.koalamailman.domain.reminder.dto.MandalartEmailMessage;
import com.koa.koalamailman.domain.reminder.util.MailContentBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component
public class SesMailClient implements MailClient {

    private final SesClient sesClient;
    private final String charset;

    public SesMailClient(
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secret,
            @Value("${aws.region:ap-northeast-2}") String region,
            @Value("${mail.charset:UTF-8}") String charset
    ) {
        this.sesClient = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secret))
                )
                .build();
        this.charset = charset;
    }

    @Override
    public String getProvider() {
        return "ses";
    }

    @Override
    public void send(EmailMessage msg) {

        Body body;
        if (msg.getHtml() != null) {
            body = Body.builder()
                    .html(Content.builder().data(msg.getHtml()).charset(charset).build())
                    .text(msg.getText() != null ? Content.builder().data(msg.getText()).charset(charset).build() : null)
                    .build();
        } else {
            body = Body.builder()
                    .text(Content.builder().data(msg.getText()).charset(charset).build())
                    .build();
        }

        Message message = Message.builder()
                .subject(Content.builder().data(msg.getSubject()).charset(charset).build())
                .body(body)
                .build();

        SendEmailRequest req = SendEmailRequest.builder()
                .source(msg.getFrom())
                .destination(Destination.builder().toAddresses(msg.getTo()).build())
                .message(message)
                .build();

        sesClient.sendEmail(req);
    }

    public void sendMandalart(MandalartEmailMessage msg) {
        try {
            // 1. 템플릿 HTML 생성
            String html = MailContentBuilder.build(
                    msg.getUsername(),
                    msg.getGrid(),
                    msg.getTip(),
                    msg.getLogoUrl(),
                    msg.getHeroUrl(),
                    msg.getCtaUrl()
            );

            // 2. HTML 본문을 SES Message로 구성
            Body body = Body.builder()
                    .html(Content.builder().data(html).charset(charset).build())
                    .build();

            Message message = Message.builder()
                    .subject(Content.builder().data(msg.getSubject()).charset(charset).build())
                    .body(body)
                    .build();

            SendEmailRequest req = SendEmailRequest.builder()
                    .source(msg.getFrom())
                    .destination(Destination.builder().toAddresses(msg.getTo()).build())
                    .message(message)
                    .build();

            sesClient.sendEmail(req);

        } catch (Exception e) {
            throw new RuntimeException("SES Mandalart 메일 전송 실패", e);
        }
    }
}
