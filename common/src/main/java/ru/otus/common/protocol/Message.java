package ru.otus.common.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Value
public final class Message implements Serializable {
    private final Type type;
    @Getter(AccessLevel.NONE)
    private final Map<Key, Serializable> data = new HashMap<>();

    public Map<Key, Serializable> getData() {
        return Collections.unmodifiableMap(data);
    }

    public Message put(Key key, Serializable value) {
        data.put(key, value);
        return this;
    }

    public Serializable get(Key key) {
        return data.get(key);
    }

    public Serializable getOrDefault(Key key, Serializable s) {
        return data.getOrDefault(key, s);
    }

}
