package com.krushna
package cluster

import akka.actor.{ActorSystem, Props}

object WorkerMainApp extends  App{
  val system= ActorSystem("clusterWorkerEx")
  val workerCluster=system.actorOf(Props[ClusterWorker],"workerCluster")
  println(s"woker Cluster started >> ${workerCluster.path}")

}
