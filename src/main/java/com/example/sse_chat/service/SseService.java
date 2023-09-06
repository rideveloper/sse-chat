package com.example.sse_chat.service;

import com.example.sse_chat.dto.Message;
import com.example.sse_chat.dto.User;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SseService {

    private String NO_MESSAGE = "No message yet!";
    private String MESSAGE_FROM_ADMIN  = "Welcome to fast chat!";

    Map<String, User> users = new HashMap<>();
    Map<String, List<Message>> inbox = new HashMap<>();
    Map<String, List<Message>> outbox = new HashMap<>();

    public void addUser(String username) {
        if (users.get(username) == null) {
            User user = new User(username);
            users.put(user.getUsername(), user);

            List<Message> inboxList = new ArrayList<>();
            inboxList.add(Message.builder()
                    .from("ADMIN")
                    .to(user.getUsername())
                    .message(MESSAGE_FROM_ADMIN).build());

            inbox.put(user.getUsername(), inboxList);

            List<Message> outboxList = new ArrayList<>();
            outboxList.add(Message.builder()
                    .from("ADMIN")
                    .to(user.getUsername())
                    .message(NO_MESSAGE).build());

            outbox.put(user.getUsername(), outboxList);
        }
    }

    public String sendUserMessage(Message message) {
        User sender = users.get(message.getFrom());
        User receiver = users.get(message.getTo());
        if (sender != null && receiver != null) {
            //add message to user outbox
            List<Message> outboxMessages = outbox.get(sender.getUsername());
            if (sender.isNewUser()) {
                outboxMessages.removeIf(msg -> msg.getMessage().equals(NO_MESSAGE));
            }

            outboxMessages.add(Message.builder()
                    .from(sender.getUsername())
                    .to(receiver.getUsername())
                    .message(message.getMessage()).build());

            outbox.put(sender.getUsername(), outboxMessages);

            //add message to destination user inbox
            List<Message> inboxMessages = inbox.get(receiver.getUsername());
            inboxMessages.add(Message.builder()
                    .from(sender.getUsername())
                    .to(receiver.getUsername())
                    .message(message.getMessage()).build());

            inbox.put(receiver.getUsername(), inboxMessages);
        }

        return "Message sent successfully";
    }

    public Flux<ServerSentEvent<Message>> getUserInbox(String username) {
        AtomicInteger increment = new AtomicInteger();
        List<Message> messages = inbox.get(username);
        return Flux
                .fromStream(messages.stream())
                .delayElements(Duration.ofSeconds(5))
                .map(message -> ServerSentEvent.<Message>builder()
                        .id(String.valueOf(increment.getAndIncrement()))
                        .event(message.getFrom())
                        .data(message).build());
    }

}
