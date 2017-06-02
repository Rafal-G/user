/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;
import com.rafal.user.impl.UserCommand.EditUser;
import com.rafal.user.impl.UserEvent.UserChanged;

/**
 * This is an event sourced entity. It has a state, {@link UserState}, which
 * stores what the greeting should be (eg, "User").
 * <p>
 * Event sourced entities are interacted with by sending them commands. This
 * entity supports two commands, a {@link EditUser} command, which is
 * used to change the greeting, and a {@link UserCommand.User} command, which is a read
 * only command which returns a greeting to the name specified by the command.
 * <p>
 * Commands get translated to events, and it's the events that get persisted by
 * the entity. Each event will have an event handler registered for it, and an
 * event handler simply applies an event to the current state. This will be done
 * when the event is first created, and it will also be done when the entity is
 * loaded from the database - each event will be replayed to recreate the state
 * of the entity.
 * <p>
 * This entity defines one event, the {@link UserChanged} event,
 * which is emitted when a {@link EditUser} command is received.
 */
public class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

  /**
   * An entity can define different behaviours for different states, but it will
   * always start with an initial behaviour. This entity only has one behaviour.
   */
  @Override
  public Behavior initialBehavior(Optional<UserState> snapshotState) {

    /*
     * Behaviour is defined using a behaviour builder. The behaviour builder
     * starts with a state, if this entity supports snapshotting (an
     * optimisation that allows the state itself to be persisted to combine many
     * events into one), then the passed in snapshotState may have a value that
     * can be used.
     */
    BehaviorBuilder b = newBehaviorBuilder(
        snapshotState.orElse(new UserState(999, LocalDateTime.now().toString())));

    /*
     * Command handler for the EditUser command.
     */
    b.setCommandHandler(EditUser.class, (cmd, ctx) ->
    // In response to this command, we want to first persist it as a
    // UserChanged event
    ctx.thenPersist(new UserChanged(entityId(), cmd.age),
        // Then once the event is successfully persisted, we respond with done.
        evt -> ctx.reply(Done.getInstance())));

    /*
     * Event handler for the UserChanged event.
     */
    b.setEventHandler(UserChanged.class,
        evt -> new UserState(evt.age, LocalDateTime.now().toString()));

    b.setReadOnlyCommandHandler(UserCommand.User.class,
        (cmd, ctx) -> ctx.reply("User: " + cmd.name + " is " + state().age + " old!"));

    return b.build();
  }

}
