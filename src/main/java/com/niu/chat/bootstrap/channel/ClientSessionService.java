package com.niu.chat.bootstrap.channel;

import com.niu.chat.bootstrap.bean.SessionMessage;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Auth justinniu
 * @Date 2018/11/30
 * @Desc
 */
@Component
public class ClientSessionService {

    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<SessionMessage>> queusSession = new ConcurrentHashMap<>();

    public void saveSessionMsg(String deviceId, SessionMessage sessionMessage) {
        ConcurrentLinkedQueue<SessionMessage> sessionMessages = queusSession.getOrDefault(deviceId, new ConcurrentLinkedQueue<>());
        boolean flag;
        do {
            flag = sessionMessages.add(sessionMessage);
        } while (!flag);
        queusSession.put(deviceId, sessionMessages);
    }

    public ConcurrentLinkedQueue<SessionMessage> getByteBuf(String id) {
        return Optional.ofNullable(id).map(
                i -> queusSession.get(i)
        ).orElse(null);
    }
 }
