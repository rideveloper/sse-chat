package com.example.sse_chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String username;
    private boolean isNewUser;

    public User(String username) {
        this.username = username;
        this.isNewUser = true;
    }
}
