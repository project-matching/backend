package com.matching.project.oauth;

import com.matching.project.dto.enumerate.OAuth;
import com.matching.project.dto.enumerate.Role;
import com.matching.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private char sex;
    private String email;
    private String password;
    private OAuth oauthCategory;

    public static OAuthAttributes of(String registrationId, String nameAttributeKey, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return ofGoogle(nameAttributeKey, attributes);
        } else if (registrationId.equals("github")){
            return ofGithub(nameAttributeKey, attributes);
        } else {
            return null;
        }
    }

    private static OAuthAttributes ofGoogle(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .sex('S')
                .email((String) attributes.get("email"))
                .password("NONE")
                .oauthCategory(OAuth.GOOGLE)
                .attributes(attributes)
                .nameAttributeKey(nameAttributeKey)
                .build();
    }

    private static OAuthAttributes ofGithub(String nameAttributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("login"))
                .sex('S')
                .email(String.valueOf(attributes.get("id")))
                .password("NONE")
                .oauthCategory(OAuth.GITHUB)
                .attributes(attributes)
                .nameAttributeKey(nameAttributeKey)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .sex(sex)
                .email(email)
                .password(password)
                .oauthCategory(oauthCategory)
                .permission(Role.ROLE_USER)
                .build();
    }
}
