package com.example.sse_chat.controller;

import com.example.sse_chat.dto.Message;
import com.example.sse_chat.service.SseService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/sse-server")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @PostMapping("/add-user")
    public String addUser(@RequestParam("name") String name) {
        sseService.addUser(name);
        return name + " created successfully!";
    }

    @GetMapping("/user-messages")
    public Flux<ServerSentEvent<List<Message>>> getUserMessages(@RequestParam("name") String name) {
        return sseService.getUserMessages(name);
    }
}
