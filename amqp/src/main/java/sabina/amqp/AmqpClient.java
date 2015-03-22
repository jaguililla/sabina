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
import static java.util.UUID.*;
import static java.util.concurrent.Executors.*;
import static java.util.logging.Logger.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * TODO Check parameters, document... sanity stuff
 * <p>
 * TODO Free channels across code
 * <p>
 * TODO Support 'addresses' in open connection
 *
 * @author juanjoaguililla
 */
public class AmqpClient implements Closeable {
    private static volatile int count;

    private static final Logger log = getLogger (AmqpClient.class.getName ());
    private static final int CORES = getRuntime ().availableProcessors ();
    private static final ThreadFactory THREAD_FACTORY = r -> new Thread (r, "amqp-" + count++);

    public static AmqpClient openAmqpClient (String uri) {
        AmqpClient client = new AmqpClient (uri);
        client.open ();
        return client;
    }

    private final ExecutorService executor = newFixedThreadPool (CORES, THREAD_FACTORY);
    private final ConnectionFactory connectionFactory = new ConnectionFactory ();
    Connection connection;

    public AmqpClient (String uri) {
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

    public void consume (String queueName, Function<String, String> handler) throws IOException {
        Channel channel = connection.createChannel ();
        channel.queueDeclare (queueName, false, false, false, null);
        channel.basicQos (1);
        channel.basicConsume (queueName, true, new StringConsumer (this, channel, handler));
        log.info ("Consuming messages in " + queueName);
    }

    public void publish (
        String requestQueue, String message, String correlationId, String replyQueueName)
        throws Exception {

        Channel channel = null;
        try {
            channel = connection.createChannel ();
            publish (channel, requestQueue, message, correlationId, replyQueueName);
        }
        finally {
            if (channel != null)
                channel.close ();
        }
    }

    public void publish (
        Channel channel, String requestQueue, String message, String correlationId,
        String replyQueueName)
        throws Exception {

        BasicProperties props = new BasicProperties.Builder ()
            .correlationId (correlationId)
            .replyTo (replyQueueName)
            .build ();

        channel.basicPublish ("", requestQueue, props, message.getBytes ());
        log.fine (format (
            "Published TO: %s REPLYING TO: %s with CORRELATION ID: %s BODY:\n%s",
            requestQueue, replyQueueName, correlationId, message));
    }

    public String call (String requestQueue, String message) throws Exception {
        Channel channel = null;
        try {
            channel = connection.createChannel ();
            String correlationId = randomUUID ().toString ();
            String replyQueueName = channel.queueDeclare ().getQueue ();

            publish (channel, requestQueue, message, correlationId, replyQueueName);

            QueueingConsumer consumer = new QueueingConsumer (channel);
            channel.basicConsume (replyQueueName, true, consumer);

            while (true) {
                Delivery delivery = consumer.nextDelivery ();
                if (delivery.getProperties ().getCorrelationId ().equals (correlationId))
                    return new String (delivery.getBody ());
            }
        }
        finally {
            if (channel != null)
                channel.close ();
        }
    }
}
