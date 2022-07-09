package com.krushna
import akka.actor.{ActorSystem, Props}
import akka.testkit.TestProbe
import com.krushna.webcrul.GetLinksActor
import com.krushna.util.Constant
import com.ning.http.client.AsyncHttpClient

import scala.concurrent.duration._

class GetLinksActorTest extends munit.FunSuite {
  test("Get Links Actor Test") {
    val client = new AsyncHttpClient()
    implicit val linksSystem=ActorSystem("GetLinksActorTestSystem")
    val linksActor = linksSystem.actorOf(Props(new GetLinksActor(client,Constant.EX_LINK,1)), "GetLinksActor")
    val p= TestProbe("TestProbe")
    p.send(linksActor,Constant.GET_LINKS)
    p.expectMsg(10.second, Constant.Check("https://github.com/", 1))
    linksSystem.terminate()
  }
}
