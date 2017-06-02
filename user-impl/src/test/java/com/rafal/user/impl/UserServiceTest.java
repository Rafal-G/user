/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.rafal.user.api.User;
import com.rafal.user.api.UserService;

public class UserServiceTest {

  @Test
  public void shouldStorePersonalizedGreeting() throws Exception {
    withServer(defaultSetup().withCassandra(true), server -> {
      UserService service = server.client(UserService.class);

      String msg1 = service.getUser("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("User, Alice!", msg1); // default greeting

      service.changeUser("Alice").invoke(new User("Hi")).toCompletableFuture().get(5, SECONDS);
      String msg2 = service.getUser("Alice").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("Hi, Alice!", msg2);

      String msg3 = service.getUser("Bob").invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals("User, Bob!", msg3); // default greeting
    });
  }

}
