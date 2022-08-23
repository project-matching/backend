package com.matching.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Comment extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long no;

    @Column(length = 255, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_no")
    private Project project;

    public void updateComment(String content) {
        this.content = content;
    }
    public void updateUser(User user){ this.user = user; }
}
