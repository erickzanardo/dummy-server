package org.dummy.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class DummyServer {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("No path was specified. Exiting");
        } else {
            String serverPath = args[0];

            Server server = new Server(8080);

            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[] { "index.html" });
            resource_handler.setResourceBase(serverPath);

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
            server.setHandler(handlers);

            server.start();
            server.join();
        }
    }
}
