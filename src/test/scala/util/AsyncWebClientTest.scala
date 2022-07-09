package com.krushna
package util

import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class AsyncWebClientTest extends munit.FunSuite {
  val link = "https://github.com/krushnaDash/akka-web-cruler"
  test("get body from links") {
    val client = new AsyncHttpClient()
    val system = ActorSystem("Test")
    implicit val exec = system.dispatcher
    val eventuallyResult = AsyncWebClient.get(link, client)
    val res = Await.ready(eventuallyResult, 100.seconds)
    assert(res.isCompleted)
    client.close()
  }
  test("get links from body") {
    val links = AsyncWebClient.findLinks(s"<html><body><a href='$link'>Hello</a></body></html>")
    links.foreach(println)
    assertEquals(links.size, 0)
  }
}
