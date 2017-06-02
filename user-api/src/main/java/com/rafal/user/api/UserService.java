/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;
import static com.lightbend.lagom.javadsl.api.Service.topic;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;

/**
 * The user service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the UserService.
 */
public interface UserService extends Service {

  ServiceCall<NotUsed, String> getUser(String id);

  ServiceCall<User, Done> changeUser(String id);

  /**
   * This gets published to Kafka.
   */
  Topic<UserEvent> helloEvents();

  @Override
  default Descriptor descriptor() {
    // @formatter:off
    return named("user").withCalls(
        pathCall("/api/user/:id",  this::getUser),
        pathCall("/api/user/:id", this::changeUser)
      ).publishing(
        topic("getUser-events", this::helloEvents)
          // Kafka partitions messages, messages within the same partition will
          // be delivered in order, to ensure that all messages for the same user
          // go to the same partition (and hence are delivered in order with respect
          // to that user), we configure a partition key strategy that extracts the
          // name as the partition key.
          .withProperty(KafkaProperties.partitionKeyStrategy(), UserEvent::getName)
      ).withAutoAcl(true);
    // @formatter:on
  }
}
