package com.krushna
package cluster

import akka.actor.{Actor, Props, ReceiveTimeout}
import akka.cluster.{Cluster, ClusterEvent}

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}

class ClusterMain extends Actor {
  val cluster = Cluster(context.system)
  cluster.subscribe(self, classOf[ClusterEvent.MemberUp])
  cluster.subscribe(self, classOf[ClusterEvent.MemberRemoved])
  cluster.join(cluster.selfAddress)

  val receptionist = context.actorOf(Props[ClusterReceptionist], "receptionist")
  context.watch(receptionist)

  def getLater(d: FiniteDuration, url: String) = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(d, receptionist, util.Constant.Check(url, 1))
  }

  getLater(Duration.Zero, util.Constant.EX_LINK)

  override def receive: Receive = {
    case ClusterEvent.MemberUp(member) =>
      if (member.address != cluster.selfAddress) {
        // some one joined
        getLater(100.seconds, util.Constant.EX_LINK)
        getLater(110.seconds, "https://en.wikipedia.org/wiki/Wiki")
        getLater(150.seconds, "https://en.wikipedia.org/wiki/Main_Page")
        getLater(200.seconds, "https://en.wikipedia.org/wiki/Wikipedia:Contact_us")
        context.setReceiveTimeout(600.second)
      }
    case util.Constant.Result(url, links) =>
      println(s"print for the links $url ${links.mkString("\n")}")
    case util.Constant.FAILED =>
      println("failed >>")

    case ReceiveTimeout =>
     cluster.leave(cluster.selfAddress)

    case ClusterEvent.MemberRemoved(m,_) =>
      context.stop(self)
  }
  // this will start single cluster node on port 25520
}