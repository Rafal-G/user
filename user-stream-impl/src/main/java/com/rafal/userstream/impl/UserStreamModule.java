/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.userstream.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.rafal.user.api.UserService;
import com.rafal.userstream.api.UserStreamService;

/**
 * The module that binds the UserStreamService so that it can be served.
 */
public class UserStreamModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    // Bind the UserStreamService service
    bindService(UserStreamService.class, UserStreamServiceImpl.class);
    // Bind the UserService client
    bindClient(UserService.class);
    // Bind the subscriber eagerly to ensure it starts up
    bind(UserStreamSubscriber.class).asEagerSingleton();
  }
}
