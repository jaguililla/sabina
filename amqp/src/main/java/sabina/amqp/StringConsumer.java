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

import static java.lang.String.*;
import static java.lang.System.*;

import java.util.function.Function;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Consumer that takes a string message, processes it with a handler and returns the resulting
 * string.
 * <p/>
 * If an exception is caught, its toString result is returned.
 * <p/>
 * TODO Document... sanity stuff
 *
 * @author jam
 */
public class StringConsumer extends DefaultConsumer {
    private static final Logger LOGGER = Logger.getLogger (StringConsumer.class.getName ());

    private final Function<String, String> handler;
    private final AmqpClient client;

    public StringConsumer (AmqpClient amqpClient, Channel channel, Function<String, String> handler) {
        super(channel);
        if (channel == null || handler == null)
            throw new IllegalArgumentException();
        this.handler = handler;
        this.client = amqpClient;
    }

    @Override public void handleDelivery (
          final String consumerTag,
          final Envelope envelope,
          final BasicProperties properties,
          final byte[] body) {

        final String encoding = properties.getContentEncoding();
        final String correlationId = properties.getCorrelationId();
        final String request = Io.encode (body, encoding);

        LOGGER.fine (format ("Handling AMQP message ENCODING: %s CORRELATION_ID: %s BODY:\n%s",
              encoding, correlationId, request));

        try {
            long time = currentTimeMillis();
            final String response = handler.apply(request);
            LOGGER.fine(">>>> " + (currentTimeMillis() - time));
            client.publish(response, properties.getReplyTo(), encoding, correlationId);
        }
        catch (Exception ex) {
            LOGGER.warning("Error processing message. Returning error response" + ex.getMessage ());
            try {
                client.publish(ex.getMessage(), properties.getReplyTo(), encoding, correlationId);
            }
            catch (Exception e) {
                LOGGER.severe("Unable to send reply" + e.getMessage ());
            }
        }
    }
//    public void publish (
//        String requestQueue, String message, String correlationId, String replyQueueName)

//    void publishToQueue (
//          final String message, final String replyToQueue, final String encoding, final String correlationId) {
}
