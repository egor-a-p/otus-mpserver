package ru.otus.ui.endpoint;

import com.google.gson.Gson;
import ru.otus.common.protocol.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * author: egor, created: 04.09.17.
 */
public class MessageEncoder implements Encoder.Text<Message> {

    private Gson gson;

    @Override
    public String encode(Message object) throws EncodeException {
        return gson.toJson(object);
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
