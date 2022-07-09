package com.krushna
package webcrul

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout}

import scala.concurrent.duration._
import com.krushna.util.Constant
import com.ning.http.client.AsyncHttpClient

class ControllerActor extends Actor with ActorLogging {
  val client = new AsyncHttpClient()
  var visitedLinks = Set.empty[String]
  var childActor = Set.empty[ActorRef]
  // this timeout is reset by every received message
  context.setReceiveTimeout(12.second)

  override def receive: Receive = {
    case Constant.Check(link, depth) =>
      log.debug("{} checking with depth {}", link, depth)
      if (!visitedLinks.contains(link) && depth > 0) {
        val linkActor=context.actorOf(Props(new GetLinksActor(client, link, depth - 1)));
        childActor += linkActor
        linkActor ! Constant.GET_LINKS
      }
      visitedLinks += link

    case Constant.DONE_FOR_LINK(link) =>
      childActor -= sender
      context.stop(sender)
      if (childActor.isEmpty) {
        log.info(s"All links for $link is ${visitedLinks.mkString("\n")}")
        context.parent ! Constant.Result(link, visitedLinks)
      }

    case ReceiveTimeout =>
      childActor.foreach(_ ! Constant.ABORT)
  }
}
