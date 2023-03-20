package com.krushna
package cluster

import akka.actor.{ActorSystem, Props}

object ClusterMainApp extends  App{
 val system= ActorSystem("clusterEx")

  val mainCluster=system.actorOf(Props[ClusterMain],"mainCluster")
  println(s"main Cluster started >> ${mainCluster.path}")

}
