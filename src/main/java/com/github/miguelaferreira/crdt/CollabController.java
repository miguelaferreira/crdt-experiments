package com.github.miguelaferreira.crdt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerWebSocket("/collab/{username}")
public class CollabController {

    private static final Logger log = LoggerFactory.getLogger(CollabController.class);

    private Sheet sheet = new Sheet();

    @Inject
    @Named(TaskExecutors.IO)
    ExecutorService ioExecutor;
    private WebSocketBroadcaster broadcaster;


    public CollabController(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen
    public Publisher<String> onOpen(String username, WebSocketSession session) {
        String msg = "[" + username + "] Joined!";
        broadcaster.broadcastSync(msg, isValid(session));

        return session.send(sheet.toString());
    }

    @OnMessage
    public void onMessage(String username, String message, WebSocketSession session) {
//        String msg = "[" + username + "] " + message;
//        broadcaster.broadcastSync(msg, isValid(session));
        log.debug("Message from {}: {}", username, message);
        final Cell cellUpdate = new Message(message).getCell();
        log.debug("Parsed cellUpdate: {}", cellUpdate);
        sheet.update(cellUpdate);
        broadcaster.broadcastSync(sheet.toString());
    }

    @OnClose
    public void onClose(String username, WebSocketSession session) {
        String msg = "[" + username + "] Disconnected!";
        broadcaster.broadcastSync(msg, isValid(session));
    }

    private Predicate<WebSocketSession> isValid(WebSocketSession session) {
        return s -> s != session;
    }
}
