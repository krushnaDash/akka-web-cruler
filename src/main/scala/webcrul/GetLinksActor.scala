package com.krushna
package webcrul

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.krushna.util.{AsyncWebClient, Constant}
import com.ning.http.client.AsyncHttpClient

import scala.util.{Failure, Success}

/**
 * This class jobs is get links from a given URL and call back to parent
 */
class GetLinksActor(client: AsyncHttpClient, url: String, depth: Int) extends Actor with ActorLogging {
  implicit val exec = context.dispatcher


  override def receive: Receive = {
    /**
     * Call bacak to sender for each links found with depth
     */
    case Constant.GET_LINKS =>
      val futureUrls = AsyncWebClient.get(url, client)
      val requester = sender
      futureUrls.onComplete {
        case Success(value) =>
          checkLinksSendMsg(requester, AsyncWebClient.findLinks(value))
          stop()
        case Failure(exception) => sender ! Failure(exception)
      }
    case Constant.ABORT => stop()
  }

  def checkLinksSendMsg(requester: ActorRef, links: Iterator[String]) {
    for (link <- links) {
      if (!link.isEmpty) {
        //log.debug(s"send message check with link ${link.trim} and dept with $depth")
        requester ! Constant.Check(link.trim, depth)
      }
    }
  }

  def stop(): Unit = {
    //context.parent ! Constant.DONE_FOR_LINK(url)
    context.stop(self)
  }
}

