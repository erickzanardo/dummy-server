package org.dummy.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class DummyServer {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("No path was specified. Exiting");
        } else {
            String serverPath = args[0];

            int port = 8080;
            String servicesFolder = null;

            if (args.length > 1) {
                String param = args[1];

                try {
                    // If the second parameter is a number, then it's the server
                    // port
                    port = Integer.parseInt(param);

                    if (args.length > 2) {
                        servicesFolder = args[2];
                    }
                } catch (NumberFormatException e) {
                    // Otherwiser it's the services fodler
                    servicesFolder = param;
                }
            }

            Server server = new Server(port);

            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[] { "index.html" });
            resource_handler.setResourceBase(serverPath);

            HandlerList handlers = new HandlerList();
            if (servicesFolder != null) {
                handlers.addHandler(new DummyServicesHandler(servicesFolder));
            }
            handlers.addHandler(resource_handler);

            server.setHandler(handlers);

            server.start();
            server.join();
        }
    }
}
