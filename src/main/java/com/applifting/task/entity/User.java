package com.applifting.task.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String accessToken;

    public User(String username, String email, String accessToken) {
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
    }
}
