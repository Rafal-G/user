/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * This interface defines all the events that the UserEntity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

  /**
   * Tags are used for getting and publishing streams of events. Each event
   * will have this tag, and in this case, we are partitioning the tags into
   * 4 shards, which means we can have 4 concurrent processors/publishers of
   * events.
   */
  AggregateEventShards<UserEvent> TAG = AggregateEventTag.sharded(UserEvent.class, 4);

  /**
   * An event that represents a change in greeting message.
   */
  @SuppressWarnings("serial")
  @Value
  @JsonDeserialize
  final class UserChanged implements UserEvent {
	public final String name;
    public final Integer age;

    @JsonCreator
    public UserChanged(String name, Integer age) {
      this.name = Preconditions.checkNotNull(name, "name");
      this.age = Preconditions.checkNotNull(age, "age");
    }
  }

  @Override
  default AggregateEventTagger<UserEvent> aggregateTag() {
    return TAG;
  }
}
