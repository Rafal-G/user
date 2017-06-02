package com.rafal.user.api;

import javax.annotation.Nullable;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Value
@JsonDeserialize
public final class User {

  public final String name;
  public final Integer age;

  @JsonCreator
  public User(String name, Integer age) {
    this.name = Preconditions.checkNotNull(name, "name");
    this.age = Preconditions.checkNotNull(age, "age");
  }
}
