package com.krushna
package util

import com.ning.http.client.AsyncHttpClient

object Constant {
  case class DONE()

  case class DONE_FOR_LINK(link: String)

  case class FAILED()

  case class ABORT()

  val EX_LINK = "https://github.com/krushnaDash/akka-web-cruler"

  case class GET_LINKS()

  case class Check(link: String, depth: Int)

  case class Result(url: String, link: Set[String])
}
