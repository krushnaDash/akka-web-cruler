package com.krushna
package webcrul

import akka.actor.{Actor, ActorLogging, Props}
import com.krushna.util.Constant
import com.krushna.util.Constant.Job

/**
 * This actor is designed to run only oen web curl request at time and remaining request will be added to queue.
 */
class Receptionist extends Actor with ActorLogging {

  var reqNo: Int = 0

  override def receive: Receive = waiting


  val waiting: Receive = {
    case Constant.Check(url,depth)=>
      context.become(runNext(Vector(Job(sender,url, depth))))
  }

  def running(queue: Vector[Job]): Receive = {
    case Constant.Check(url,depth)=> enqueueJob(queue,Job(sender,url, depth))
    case Constant.Result(url, links) =>
      val job=queue.head
      job.actorRef !Constant.Result(url, links)
      context.stop(sender)
      context.become(runNext(queue.tail))
  }

  def runNext(queue: Vector[Job]): Receive = {
    reqNo += 1
    if (queue.isEmpty)
      waiting
    else {
      val controller = context.actorOf(Props(new ControllerActor(queue.head.url)), s"c-$reqNo")
      controller ! Constant.Check(queue.head.url, queue.head.depth)
      running(queue)
    }
  }
  def enqueueJob(queue: Vector[Job], job:Job) ={
    if(queue.size > 2) {
      println("Queue is full >>>>>>" )
      sender ! Constant.FAILED
      running(queue) // send failed and keep the running state
    }
    else {
     context.become(running(queue:+job)) // make the state of the actor becoming running by adding the new job
    }
  }
}
