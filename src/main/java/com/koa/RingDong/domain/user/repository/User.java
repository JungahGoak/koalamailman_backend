package com.koa.RingDong.domain.user.repository;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"oauthId", "oauthProvider"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String oauthId; // 각 플랫폼의 고유 id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oauthProvider;  // KAKAO, NAVER

    @Column(nullable = false)
    private String nickname;

    @Column
    private String email;

    @Column
    private String gender;

    @Column
    private String ageGroup;

    @Column
    private String job;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Builder
    public User(String oauthId, OAuthProvider oauthProvider, String nickname, String email) {
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.nickname = nickname;
        this.email = email;
        this.gender = null;
        this.ageGroup = null;
        this.job = null;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfile(String ageGroup, String gender, String job) {
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.job = job;
    }
    public void updateGender(String gender) { this.gender = gender;}

    public void updateAgeGroup(String ageGroup) { this.ageGroup = ageGroup;}

    public void updateJob(String job) { this.job = job;}


}