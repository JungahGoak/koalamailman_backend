package com.koa.koalamailman.domain.reminder.service;

import com.koa.koalamailman.domain.mandalart.repository.entity.CoreGoal;
import com.koa.koalamailman.domain.user.repository.User;
import com.koa.koalamailman.domain.reminder.provider.ReminderTimeProvider;
import com.koa.koalamailman.domain.mandalart.repository.CoreGoalRepository;
import com.koa.koalamailman.domain.user.repository.UserRepository;
import com.koa.koalamailman.domain.reminder.util.MailContentBuilder;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    private final UserRepository userRepository;
    private final CoreGoalRepository coreGoalRepository;
    private final ReminderTimeProvider reminderTimeProvider;
    private final MailContentBuilder mailContentBuilder;

    @Transactional
    public void sendMail(Long targetId) {

        User user = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + targetId));

        // contentText를 Mandalart 내용으로
        CoreGoal coreGoal = coreGoalRepository.findCoreGoalWithMainGoalsByUserId(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Core Goal이 존재하지 않는 사용자 ID: " + targetId));

        try {
            String title = mailContentBuilder.buildTitle();
            String html = mailContentBuilder.buildFullHtml(coreGoal);
            sendHTMLMail(user.getEmail(), title, html);
            coreGoal.updateNextScheduledTime(reminderTimeProvider.generateRandomTime(coreGoal.getReminderInterval()));
        } catch (IOException e) {
            log.error("[메일 전송] 전송 실패 - userId: {}, email: {}, 이유: {}", targetId, user.getEmail(), e.getMessage());

            // 메일 전송 실패 시 nextScheduledTime 내일로 다시 설정
            coreGoal.updateNextScheduledTime(reminderTimeProvider.generateRandomTimeForTomorrow());

            log.info("[메일 전송] 메일 재전송을 위한 nextScheduledTime 갱신 완료");
        }
    }

    public void sendHTMLMail(String to, String subject, String htmlContent) throws IOException {
        Email from = new Email("reminder@ringdong.kr");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        SendGrid sg = new SendGrid(apiKey);
        Response response = sg.api(request);

        if (response.getStatusCode() != 202) {
            throw new IOException("[메일 전송] SendGrid 응답 실패 - 상태 코드: " + response.getStatusCode());
        }

        log.info("[메일 전송] 전송 성공 - to: {}, subject: {}", to, subject);
    }
}