/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import lombok.Value;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserEvent.UserChanged.class, name = "greeting-message-changed")
})
public interface UserEvent {

  String getName();
  Integer getAge();

  @Value
  final class UserChanged implements UserEvent {
    public final String name;
    public final Integer age;

    @JsonCreator
    public UserChanged(String name, Integer age) {
      this.name = Preconditions.checkNotNull(name, "name");
      this.age = Preconditions.checkNotNull(age, "message");
    }

    //TODO check if these are needed
    @Override
    public String getName() {
      return name;
    }

    @Override
    public Integer getAge() {
      return age;
    }
  }
}
