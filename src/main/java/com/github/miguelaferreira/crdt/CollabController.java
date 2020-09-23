package com.github.miguelaferreira.crdt;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerWebSocket("/collab/{username}")
public class CollabController {

    private static final Logger log = LoggerFactory.getLogger(CollabController.class);

    private Map<String, Sheet> sheetReplicas = new ConcurrentHashMap<>();
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

        sheetReplicas.put(username, sheet.clone());

        return session.send(sheet.toString());
    }

    @OnMessage
    public void onMessage(String username, String message, WebSocketSession session) {
        log.debug("Message from {}: {}", username, message);
        final Cell cellUpdate = new Message(message).getCell();
        log.debug("Parsed cellUpdate: {}", cellUpdate);
        final char column = cellUpdate.getColumn();
        final Optional<ColumnCrdt> maybeColumn = sheet.getColumn(column);
        if (maybeColumn.isPresent()) {
            final ColumnCrdt clientCrdt = sheet.getColumn(column).get();
            final long timestamp = System.currentTimeMillis();
            clientCrdt.add(timestamp, cellUpdate);
            sheet.update(column, clientCrdt);
            broadcaster.broadcastSync(sheet.toString());
        } else {
            log.warn("Discarding message: invalid column {}", column);
        }
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
