/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.userstream.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.rafal.user.api.UserEvent;
import com.rafal.user.api.UserService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * This subscribes to the UserService event stream.
 */
public class UserStreamSubscriber {

  @Inject
  public UserStreamSubscriber(UserService userService, UserStreamRepository repository) {
    // Create a subscriber
    userService.helloEvents().subscribe()
      // And subscribe to it with at least once processing semantics.
      .atLeastOnce(
        // Create a flow that emits a Done for each message it processes
        Flow.<UserEvent>create().mapAsync(1, event -> {

          if (event instanceof UserEvent.UserChanged) {
            UserEvent.UserChanged userChanged = (UserEvent.UserChanged) event;
            // Update the user
            return repository.updateAge(userChanged.getName(), userChanged.getName());

          } else {
            // Ignore all other events
            return CompletableFuture.completedFuture(Done.getInstance());
          }
        })
      );

  }
}
