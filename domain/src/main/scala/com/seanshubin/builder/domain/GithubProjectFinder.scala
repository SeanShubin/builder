package com.seanshubin.builder.domain

import com.seanshubin.http.values.domain.{RequestValue, Sender}

class GithubProjectFinder(userName: String,
                          sender: Sender,
                          jsonMarshaller: JsonMarshaller,
                          projectsFoundInGitlab: Seq[String] => Unit) extends ProjectFinder {
  override def findProjects(): Unit = {
    val request = RequestValue(s"https://api.github.com/users/$userName/repos?per_page=100", "GET", Seq(), Seq())
    val response = sender.send(request)
    val untypedJson = jsonMarshaller.fromJson(response.text, classOf[AnyRef])

    def getName(untyped: AnyRef): String = {
      val map = untyped.asInstanceOf[Map[String, String]]
      val name = map("name")
      name
    }

    val jsonSequence = untypedJson.asInstanceOf[Seq[AnyRef]]
    val names = jsonSequence.map(getName)
    projectsFoundInGitlab(names)
  }
}
