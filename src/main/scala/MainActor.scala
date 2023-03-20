package com.krushna


import akka.actor.{Actor, ActorSystem, Identify, Props, ReceiveTimeout}
import com.krushna.util.Constant
import com.krushna.webcrul.{ControllerActor, Receptionist}

import scala.concurrent.duration.DurationInt

class MainActor extends Actor{

  val receptionist=context.actorOf(Props[Receptionist])
  receptionist ! Constant.Check(Constant.EX_LINK,1)
  receptionist ! Constant.Check("https://en.wikipedia.org/wiki/Wiki",1)
  receptionist ! Constant.Check("https://en.wikipedia.org/wiki/Main_Page",1)
  receptionist ! Constant.Check("https://en.wikipedia.org/wiki/Wikipedia:Contact_us",1)


  context.setReceiveTimeout(900.seconds)

  override def receive: Receive = {
    case Constant.Result(url,values)=>
      println(s"got links for $url -> ${values.mkString("\n")}")
    case Constant.FAILED =>
      println("So many request need to wait >>>>")
    case ReceiveTimeout =>
     context.stop(self)
    case e:Exception =>
    println("Execpetion"+e)
  }
}

object MainApp extends App{
  val mainActorSystem=ActorSystem("MainApp")
  mainActorSystem.actorOf(Props[MainActor],"MainActor")
}
