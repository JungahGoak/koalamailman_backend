package com.koa.koalamailman.domain.reminder.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MandalartEmailMessage {
    String from;
    String to;
    String subject;
    String username;
    String[][] grid;
    String tip;
    String logoUrl; // 로고 이미지
    String heroUrl; // 배너 이미지
    String ctaUrl;
}
