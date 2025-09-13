package com.koa.koalamailman.domain.reminder.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmailMessage {
    private final String from;
    private final List<String> to;
    private final String subject;
    private final String text;
    private final String html;
}
