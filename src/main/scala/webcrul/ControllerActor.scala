package com.krushna
package webcrul

import util.Constant

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, ReceiveTimeout, SupervisorStrategy, Terminated}
import com.ning.http.client.AsyncHttpClient

import scala.concurrent.duration.{DurationInt, _}
import scala.util.Failure

/**
 * THis is just a example and it's not used show how to use
 */
class ControllerActor(url: String) extends Actor with ActorLogging {
  val client = new AsyncHttpClient()
  var visitedLinks = Set.empty[String]
  // we can use the context.children to get all the children
  context.setReceiveTimeout(200.second)

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minutes) {
    case _: Exception => SupervisorStrategy.restart
  }

  override def receive: Receive = {
    case Constant.Check(link, depth) =>
      //log.debug("{} checking with depth {}", link, depth)
      if (!visitedLinks.contains(link) && depth > 0) {
        val linkActor = context.actorOf(Props(new GetLinksActor(client, link, depth - 1)));
        linkActor ! Constant.GET_LINKS
        context.watch(linkActor)
      }
      visitedLinks += link

    case Terminated(_) =>
      if (context.children.isEmpty) {
        //log.debug(s"All links for  ${visitedLinks.mkString("\n")}")
        context.parent ! Constant.Result(url, visitedLinks)
      }
    case Failure(exception) =>
      println("ignore the link >>")
      context.stop(sender)

    case ReceiveTimeout =>
      context.children.foreach(_ ! Constant.ABORT)
  }

  override def postStop(): Unit = {
    client.close()
    super.postStop()
  }
}
