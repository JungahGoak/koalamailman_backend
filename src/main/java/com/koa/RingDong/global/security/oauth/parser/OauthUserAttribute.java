package com.koa.RingDong.global.security.oauth.parser;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OauthUserAttribute {
    private final OAuth2User oAuth2User;
    private final OauthAttributeParser parser;

    public OauthUserAttribute(OAuth2User oAuth2User, OauthAttributeParser parser) {
        this.oAuth2User = oAuth2User;
        this.parser = parser;
    }

    public Map<String, Object> toOauthUserInfo() {
        return parser.parse(oAuth2User);
    }
}

