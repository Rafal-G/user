/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.rafal.user.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;

import javax.inject.Inject;

import com.rafal.user.api.UserService;
import com.rafal.user.impl.UserCommand.*;

/**
 * Implementation of the UserService.
 */
public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public UserServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(UserEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, String> getUser(String id) {
        return request -> {
            // Look up the getUser world entity for the given ID.
            PersistentEntityRef<UserCommand> ref = persistentEntityRegistry.refFor(UserEntity.class, id);
            // Ask the entity the User command.
            return ref.ask(new User(id));
        };
    }

    @Override
    public ServiceCall<com.rafal.user.api.User, Done> changeUser(String id) {
        return request -> {
            // Look up the getUser world entity for the given ID.
            PersistentEntityRef<UserCommand> ref = persistentEntityRegistry.refFor(UserEntity.class, id);
            // Tell the entity to use the greeting message specified.
            return ref.ask(new EditUser(request.age));
        };

    }

    @Override
    public Topic<com.rafal.user.api.UserEvent> helloEvents() {
        // We want to publish all the shards of the getUser event
        return TopicProducer.taggedStreamWithOffset(UserEvent.TAG.allTags(), (tag, offset) ->

                // Load the event stream for the passed in shard tag
                persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

                    // Now we want to convert from the persisted event to the published event.
                    // Although these two events are currently identical, in future they may
                    // change and need to evolve separately, by separating them now we save
                    // a lot of potential trouble in future.
                    com.rafal.user.api.UserEvent eventToPublish;

                    if (eventAndOffset.first() instanceof UserEvent.UserChanged) {
                        UserEvent.UserChanged userChanged = (UserEvent.UserChanged) eventAndOffset.first();
                        eventToPublish = new com.rafal.user.api.UserEvent.UserChanged(
                                userChanged.getName(), userChanged.getAge()
                        );
                    } else {
                        throw new IllegalArgumentException("Unknown event: " + eventAndOffset.first());
                    }

                    // We return a pair of the translated event, and its offset, so that
                    // Lagom can track which offsets have been published.
                    return Pair.create(eventToPublish, eventAndOffset.second());
                })
        );
    }
}
