/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.impl;

import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

/**
 * The state for the {@link UserEntity} entity.
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class UserState implements CompressedJsonable {

  public final Integer age;
  public final String timestamp;

  @JsonCreator
  public UserState(Integer age, String timestamp) {
    this.age = Preconditions.checkNotNull(age, "message");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
  }
}
