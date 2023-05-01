package com.gg.server.global.security.info.impl;


import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.security.info.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class FortyTwoOAuthUserInfo extends OAuthUserInfo {

    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;

    public FortyTwoOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getIntraId() {
        return attributes.get("login").toString();
    }

    public String getEmail() {
        return attributes.get("email").toString();
    }

    public String getImageUrl() {
        Map<String, Object> image = (Map<String, Object>) attributes.get("image");
        if (image == null) {
            return defaultImageUrl;
        }
        if (image.get("link") == null) {
            return defaultImageUrl;
        }
        return image.get("link").toString();
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.USER;
    }
}
