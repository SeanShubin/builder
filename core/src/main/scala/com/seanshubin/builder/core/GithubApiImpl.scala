package com.seanshubin.builder.core

import com.seanshubin.http.values.client.google.HttpSender
import com.seanshubin.http.values.core.RequestValue

class GithubApiImpl(httpSender: HttpSender, jsonMarshaller: JsonMarshaller) extends GithubApi {
  override def getProjectsForUser(userName: String): Seq[String] = {
    val request = RequestValue(s"https://api.github.com/users/$userName/repos?per_page=100", "GET", Seq(), Seq())
    val response = httpSender.send(request)
    val untypedJson = jsonMarshaller.fromJson(response.text, classOf[AnyRef])
    def getName(untyped: AnyRef): String = {
      val map = untyped.asInstanceOf[Map[String, String]]
      val name = map("name")
      name
    }
    val jsonSequence = untypedJson.asInstanceOf[Seq[AnyRef]]
    val names = jsonSequence.map(getName)
    names
  }
}
