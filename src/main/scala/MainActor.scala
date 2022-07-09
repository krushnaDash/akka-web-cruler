package com.krushna

import akka.actor.{Actor, ActorSystem, Props}
import com.krushna.MainApp.mainActorSystem
import com.krushna.webcrul.ControllerActor
import com.krushna.util.Constant

class MainActor extends Actor{

  val controller=context.actorOf(Props[ControllerActor])
  controller ! Constant.Check(Constant.EX_LINK,2)

  override def receive: Receive = {
    case Constant.Result(url,values)=>
      println(s"got links for $url ${values.mkString("\n")}")
      context.stop(controller)
  }
}

object MainApp extends App{
  val mainActorSystem=ActorSystem("MainApp")
  mainActorSystem.actorOf(Props[MainActor],"MainActor")
}
