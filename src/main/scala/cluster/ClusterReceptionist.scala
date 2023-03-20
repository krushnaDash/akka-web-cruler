package com.krushna
package cluster

import akka.actor.{Actor, ActorRef, Address, Deploy, Props, ReceiveTimeout, SupervisorStrategy, Terminated}
import akka.cluster.ClusterEvent.{MemberRemoved, MemberUp}
import akka.cluster.{Cluster, ClusterEvent}
import akka.remote.RemoteScope
import com.krushna.util.Constant
import com.krushna.webcrul.ControllerActor

import scala.concurrent.duration.DurationInt

class ClusterReceptionist extends Actor {
  val cluster = Cluster(context.system)
  cluster.subscribe(self, classOf[ClusterEvent.MemberRemoved])
  cluster.subscribe(self, classOf[ClusterEvent.MemberUp])

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }


  override def receive: Receive = awaitingMember

  val awaitingMember: Receive = {
    // this is the first event it will recive in response of cluster.subscribe
    case current: ClusterEvent.CurrentClusterState =>
      val addresses = current.members.toVector.map(_.address) // to extract all the cluster addresses
      val notMe = addresses.filter(_ != cluster.selfAddress)
      if (notMe.nonEmpty) context.become(active(notMe))
    case MemberUp(member) if member.address != cluster.selfAddress =>
      context.become(active((Vector(member.address))))
    case util.Constant.Check(url, depth) => sender ! Constant.FAILED // whilte waiting on cluster to avaialbe if any request come send failed signal
  }

  def active(addresses: Vector[Address]): Receive = {
    case MemberUp(member) if member.address != cluster.selfAddress =>
      context.become(active(addresses :+ member.address))
    case MemberRemoved(member, _) =>
      val next = addresses.filterNot(_ == member.address)
      if (next.isEmpty) context.become(awaitingMember)
      else
        context.become(active(next))
    case Constant.Check(url, depth) if context.children.size < addresses.size => // to run one request per cluster
      val client = sender
      //val address=pick(addresses)
      val address = addresses(0) // choose a random address here
      context.actorOf(Props(new Customer(client,url, address)), "Test")
    case Constant.Check(url, depth) => // all cluster are busy
      sender ! Constant.FAILED
  }


}

class Customer(client: ActorRef, url: String, address: Address) extends Actor {
  implicit val s = context.parent // this will be send as sender to to any message to any actor, as this will take precedence

  override def receive: Receive = ({
    case ReceiveTimeout =>
      context.unwatch(controller)
      client ! Constant.FAILED // controller Actor timeout
    case Terminated(_) =>
      client ! Constant.FAILED // controller dead

    case Constant.Result(url, links) =>
      context.unwatch(controller)
      client ! Constant.Result(url, links)

  }: Receive).andThen(_ => context.stop(self))


  override val supervisorStrategy = SupervisorStrategy.stoppingStrategy
  val props = Props(new ControllerActor(url)).withDeploy(Deploy(scope = RemoteScope(address)))
  val controller = context.actorOf(props, "controller")
  context.watch(controller)

  context.setReceiveTimeout(200.seconds)
  controller ! Constant.Check(url, 2)
}