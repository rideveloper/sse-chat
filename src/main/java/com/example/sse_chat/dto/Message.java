package com.example.sse_chat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Message {
    private String from;
    private String to;
    private String message;
}
