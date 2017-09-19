package ru.otus.common;


import java.util.UUID;

public class Configuration {

    public static final int DEFAULT_PORT = 8090;
    public static final String DEFAULT_HOST = "localhost";

    public static final String NODE_HOST_KEY = "node.host";
    public static final String NODE_PORT_KEY = "node.port";
    public static final String NODE_UUID_KEY = "node.uuid";

    public static final String MASTER_HOST_KEY = "master.host";
    public static final String MASTER_PORT_KEY = "master.port";
    public static final String MASTER_UUID_KEY = "master.uuid";

    public static String host() {
        return System.getProperty(NODE_HOST_KEY, DEFAULT_HOST);
    }

    public static int port() {
        try {
            return Integer.parseInt(System.getProperty(NODE_PORT_KEY));
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
    }

    public static String uuid() {
        return System.getProperty(NODE_UUID_KEY, UUID.randomUUID().toString());
    }

    public static String masterHost() {
        return System.getProperty(MASTER_HOST_KEY, DEFAULT_HOST);
    }

    public static int masterPort() {
        try {
            return Integer.parseInt(System.getProperty(MASTER_PORT_KEY));
        } catch (Exception e) {
            return DEFAULT_PORT;
        }
    }

    public static String masterUuid() {
        return System.getProperty(MASTER_UUID_KEY, UUID.randomUUID().toString());
    }

    private Configuration(){
    }
}
