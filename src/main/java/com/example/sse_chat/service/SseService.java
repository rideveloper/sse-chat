package com.example.sse_chat.service;

import com.example.sse_chat.dto.Message;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

@Service
public class SseService {

    private String NO_MESSAGE = "No message yet!";
    private String MESSAGE_FROM_ADMIN  = "Welcome to fast chat!";
    List<String> users = new ArrayList<>();
    Map<String, List<Message>> messageForMap = new HashMap<>();
    Map<String, List<Message>> messageToMap = new HashMap<>();

    public void addUser(String name) {
        if (!users.contains(name)) {

            //add users to list of registered users if name doesn't exist
            users.add(name);

            List<Message> messagesFor = messageForMap.get(name);
            if (messagesFor == null) {
                List<Message> messageList = new ArrayList<>();
                messageList.add(Message.builder()
                        .from("ADMIN")
                        .to(name)
                        .message(MESSAGE_FROM_ADMIN).build());

                messageForMap.put(name, messageList);
            }

            List<Message> messagesTo = messageToMap.get(name);
            if (Objects.isNull(messagesTo)) {
                List<Message> messageList = new ArrayList<>();
                messageList.add(Message.builder()
                        .from("ADMIN")
                        .to(name)
                        .message(NO_MESSAGE).build());

                messageToMap.put(name, messageList);
            }
        }
    }

    public Flux<ServerSentEvent<List<Message>>> getUserMessages(String name) {
        List<Message> messages = messageForMap.get(name);
        return Flux
                .fromStream(messages.stream())
                .map(sequence -> ServerSentEvent.<List<Message>>builder()
                        .id(String.valueOf(sequence))
                        .event("all-user-message")
                        .data(messages).build());
    }

}
