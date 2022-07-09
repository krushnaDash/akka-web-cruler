package com.krushna
package util

import akka.event.slf4j.Logger
import com.ning.http.client.AsyncHttpClient
import org.jsoup.Jsoup
import scala.jdk.CollectionConverters._

import java.util.concurrent.Executor
import scala.concurrent.{Future, Promise}

object AsyncWebClient {
  val logger = Logger(getClass.getName)

  def get(url: String, client: AsyncHttpClient)(implicit exec: Executor): Future[String] = {
    logger.info(s"Hitting to URL -> $url")
    val f = client.prepareGet(url).execute()
    val p = Promise[String]()
    f.addListener(new Runnable {
      override def run(): Unit = {
        val res = f.get();
        if (res.getStatusCode < 400)
          p.success(res.getResponseBody())
        else
          p.failure(new RuntimeException(res.getStatusCode + "Error"))
      }
    }, exec)
    p.future
  }

  def findLinks(body: String): Iterator[String] = {
    val document = Jsoup.parse(body)
    val links = document.select("a[href]")
    for (link <- links.iterator().asScala)
      yield link.absUrl("href")
  }
}

