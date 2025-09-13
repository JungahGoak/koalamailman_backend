package com.koa.koalamailman.domain.reminder.client;

import com.koa.koalamailman.domain.reminder.dto.EmailMessage;

public interface MailClient {
    String getProvider();
    void send(EmailMessage message);
}
