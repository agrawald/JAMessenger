package com.chat.client;

import com.chat.common.data.Message;

/**
 * Created by e7006722 on 17/11/2014.
 */
public interface ClientSvc {
    void sendMessage(Message message);
    String start();
}
