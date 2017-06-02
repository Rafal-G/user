/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.userstream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.rafal.user.api.UserService;
import com.rafal.userstream.api.UserStreamService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the UserStreamService.
 */
public class UserStreamServiceImpl implements UserStreamService {

  private final UserService userService;
  private final UserStreamRepository repository;

  @Inject
  public UserStreamServiceImpl(UserService userService, UserStreamRepository repository) {
    this.userService = userService;
    this.repository = repository;
  }

  @Override
  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> directStream() {
    return hellos -> completedFuture(
        hellos.mapAsync(8, name -> userService.getUser(name).invoke()));
  }

  @Override
  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> autonomousStream() {
    return hellos -> completedFuture(
        hellos.mapAsync(8, name -> repository.getMessage(name).thenApply( message ->
            String.format("%s, %s!", message.orElse("Hello"), name)
        ))
    );
  }
}
