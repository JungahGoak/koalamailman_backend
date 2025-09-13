package com.koa.koalamailman.domain.reminder.service;

import com.koa.koalamailman.domain.mandalart.repository.entity.GoalEntity;
import com.koa.koalamailman.domain.mandalart.repository.entity.MandalartEntity;
import com.koa.koalamailman.domain.mandalart.service.GoalService;
import com.koa.koalamailman.domain.reminder.client.SesMailClient;
import com.koa.koalamailman.domain.reminder.dto.EmailMessage;
import com.koa.koalamailman.domain.reminder.dto.MandalartEmailMessage;
import com.koa.koalamailman.domain.mandalart.repository.entity.GoalLevel;
import com.koa.koalamailman.domain.user.repository.User;
import com.koa.koalamailman.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final SesMailClient mailClient;
    private final UserService userService;
    private final GoalService goalService;

    @Value("${mail.from}")
    private String from;
    @Value("${mail.cta-uri}")
    private String ctaUrl;

    public void send(EmailMessage message) {
        mailClient.send(message);
    }
    public void sendRemindMail(MandalartEntity mandalart) {
        User user = userService.findUserById(mandalart.getUserId());
        List<GoalEntity> goals = goalService.getCoreAndMainGoalsFromMandalart(mandalart);

        MandalartEmailMessage mandalartEmailMessage = MandalartEmailMessage.builder()
                .to(user.getEmail())
                .username(user.getNickname())
                .from(from)
                .ctaUrl(ctaUrl)
                .grid(toGrid(goals))
                .build();

        sendMandalartTemplate(mandalartEmailMessage);
    }

    public void sendMandalartTemplate(MandalartEmailMessage message) {
        mailClient.sendMandalart(message);
    }

    private String[][] toGrid(List<GoalEntity> goals) {
        int size = 3; // 3x3
        String[][] grid = new String[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = "";
            }
        }

        for (GoalEntity goal : goals) {
            if (goal.getLevel() == GoalLevel.CORE) {
                grid[1][1] = goal.getContent();
            } else if (goal.getLevel() == GoalLevel.MAIN) {
                int pos = goal.getPosition();
                int row = pos / size;
                int col = pos % size;

                if (row == 1 && col == 1) {
                    continue; // CORE
                }

                grid[row][col] = goal.getContent();
            }
        }
        return grid;
    }
}
