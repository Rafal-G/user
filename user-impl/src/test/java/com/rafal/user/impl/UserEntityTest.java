package com.rafal.user.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.rafal.user.impl.UserCommand.User;
import com.rafal.user.impl.UserCommand.EditUser;
import com.rafal.user.impl.UserEvent.UserChanged;

public class UserEntityTest {

  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create("UserEntityTest");
  }

  @AfterClass
  public static void teardown() {
    JavaTestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void testUserEntity() {
    PersistentEntityTestDriver<UserCommand, UserEvent, UserState> driver = new PersistentEntityTestDriver<>(system,
        new UserEntity(), "world-1");

    Outcome<UserEvent, UserState> outcome1 = driver.run(new UserCommand.User("Alice"));
    assertEquals("User, Alice!", outcome1.getReplies().get(0));
    assertEquals(Collections.emptyList(), outcome1.issues());

    Outcome<UserEvent, UserState> outcome2 = driver.run(new EditUser("Hi"),
        new User("Bob"));
    assertEquals(1, outcome2.events().size());
    assertEquals(new UserChanged("world-1", "Hi"), outcome2.events().get(0));
    assertEquals("Hi", outcome2.state().message);
    assertEquals(Done.getInstance(), outcome2.getReplies().get(0));
    assertEquals("Hi, Bob!", outcome2.getReplies().get(1));
    assertEquals(2, outcome2.getReplies().size());
    assertEquals(Collections.emptyList(), outcome2.issues());
  }

}
