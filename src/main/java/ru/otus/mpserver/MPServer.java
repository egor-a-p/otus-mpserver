package ru.otus.mpserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class MPServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8090);

        // Configure webapp provided as external WAR
        WebAppContext webapp = new WebAppContext();
        webapp.setWar(Thread.currentThread()
                .getContextClassLoader()
                .getResource("libs/root.war").getPath());
        server.setHandler(webapp);

        // Start the server
        server.start();
        server.join();
    }
}
