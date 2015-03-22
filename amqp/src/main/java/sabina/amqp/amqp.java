/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.amqp;

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.logging.Logger.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.logging.Logger;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@FunctionalInterface interface RpcHandler extends Function<Request, Response> {}
@FunctionalInterface interface Handler extends java.util.function.Consumer<Request> {}

/**
 * Takes care of connections. Creates producers and consumers
 */
class Client implements Closeable {
    private static volatile int count;

    private static final Logger log = getLogger (AmqpClient.class.getName ());
    private static final int CORES = getRuntime ().availableProcessors ();
    private static final ThreadFactory THREAD_FACTORY = r -> new Thread (r, "amqp-" + count++);

    private final ConnectionFactory connectionFactory = new ConnectionFactory ();
    private final ExecutorService executor;
    private Connection connection;

    public Client (String uri, int threads) {
        try {
            connectionFactory.setUri (uri);

            log.info (format ("Created AMQP Client to:\n\t%s\n\t%s\n\t%s\n\t%s\n\t%s\n\t%s",
                uri,
                connectionFactory.getHost (),
                connectionFactory.getPort (),
                connectionFactory.getVirtualHost (),
                connectionFactory.getUsername (),
                connectionFactory.getPassword ()
            ));

            executor = threads < 0?
                newCachedThreadPool (THREAD_FACTORY) :
                newFixedThreadPool (threads == 0? CORES : threads, THREAD_FACTORY);
        }
        catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException ("Error initializing AMQP connection factory", e);
        }
    }

    public void open () {
        try {
            if (connection != null)
                close ();

            connection = connectionFactory.newConnection (executor);
            log.info ("Connection to AMQP opened");
        }
        catch (IOException e) {
            throw new RuntimeException ("Error opening AMQP connection", e);
        }
    }

    /**
     * TODO Handle close properly
     */
    @Override public void close () {
        if (connection == null)
            return;

        try {
            if (connection.isOpen ())
                connection.close ();
            log.info ("AMQP client closed");
        }
        catch (IOException e) {
            log.severe ("Error closing AMQP connection" + e.getMessage ());
        }
        connection = null;
    }
}

class Producer {
    void publish (String q, Request r) {

    }
}

class Consumer {
    void consume (String q, Handler r) {

    }
}

class Response {
}

class Request {
}
