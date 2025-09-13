package com.koa.koalamailman.domain.reminder.service;

import com.koa.koalamailman.domain.mandalart.repository.entity.MandalartEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final MailService sendGridMailService;
    private final ReminderService reminderService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    // 매일 오전 6시 실행
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void checkAndScheduleTodayMails() {
        log.info("[스케줄러] 🔔오전 6시 스케줄러 실행");
        reschedulePastOrScheduleToday();
    }

    // 앱 시작 시: 오늘 06:00 이후라면 한 번 스캔해서 예약해둔다
    @PostConstruct
    public void scheduleMailsOnStartup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtSix = LocalDate.now().atTime(6, 0);

        if (now.isBefore(todayAtSix)) {
            log.info("[스케줄러] 🕕애플리케이션 시작: 오전 6시 이전은 메일 예약 스케줄링 X");
            return;
        }

        log.info("[스케줄러] 🚀애플리케이션 시작: 메일 예약 스케줄링 시작");
        reschedulePastOrScheduleToday();
    }

    private void reschedulePastOrScheduleToday() {

        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);

        List<MandalartEntity> targetMandalarts = reminderService.findMandalartsByScheduleTimeBefore(endOfToday.plusSeconds(1));
        log.info("[스케줄러] 📧오늘 또는 지난 메일 예약 대상 수: {}", targetMandalarts.size());

        for (MandalartEntity mandalart : targetMandalarts) {
            scheduleMailAt(mandalart);
        }
    }

    private void scheduleMailAt(MandalartEntity mandalart) {
        LocalDateTime scheduledTime = mandalart.getReminderOption().getRemindScheduledAt();
        long delayMs = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();
        if (delayMs < 0) delayMs = 0;

        scheduler.schedule(() -> {
            try {
                sendGridMailService.sendRemindMail(mandalart);
                log.info("[스케줄러] 메일 전송 완료 - userId: {}", mandalart.getUserId());
            } catch (Exception e) {
                log.error("[스케줄러] 메일 전송 중 예외 발생 - userId: {}, 이유: {}", mandalart.getUserId(), e.getMessage());
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }
}
