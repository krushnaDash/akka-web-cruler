package com.krushna

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestProbe}
import com.krushna.util.Constant
import com.krushna.util.Constant.Result
import com.krushna.webcrul.ControllerActor

import scala.concurrent.duration._
import scala.reflect.ClassTag

class ControllerActorTest extends munit.FunSuite {
  test("Get Links depths of 2") {
    implicit val controllerActorSystem = ActorSystem("ConrollerActorTestSystem")
    //val controller = controllerActorSystem.actorOf(Props[ControllerActor], "GetLinksActor")
    val p = TestProbe("TestProbe")
    val controller = TestActorRef(Props[ControllerActor], p.ref, "GetLinksActor")
    controller ! Constant.Check(Constant.EX_LINK, 1)
    p.awaitAssert(p.msgAvailable, 60.seconds)
    controllerActorSystem.terminate()
  }
}
