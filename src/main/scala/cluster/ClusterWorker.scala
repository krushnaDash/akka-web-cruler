package com.krushna
package cluster

import akka.actor.Actor
import akka.cluster.{Cluster, ClusterEvent}

class ClusterWorker extends Actor {
  val cluster = Cluster(context.system)
  cluster.subscribe(self, classOf[ClusterEvent.MemberRemoved])
  val main = cluster.selfAddress.copy(port = Some(util.Constant.AKKA_DEFAULT_PORT)) // we can retrive the ClusterMain address from the cluster self address by replacing the port
  cluster.join(main) // this will join the ClusterMain

  override def receive: Receive = {
    case ClusterEvent.MemberRemoved(m, _) =>
      if (m.address == main) context.stop(self)
  }
}