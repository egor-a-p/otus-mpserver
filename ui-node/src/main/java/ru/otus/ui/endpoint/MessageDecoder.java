package ru.otus.ui.endpoint;

import com.google.gson.Gson;
import ru.otus.common.protocol.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * author: egor, created: 04.09.17.
 */
public class MessageDecoder implements Decoder.Text<Message> {

    private Gson gson;

    @Override
    public Message decode(String s) throws DecodeException {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        try {

            return gson.fromJson(s, Message.class) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        gson = new Gson();
    }

    @Override
    public void destroy() {
        gson = null;
    }
}
