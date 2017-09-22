package com.seanshubin.builder.domain

import java.util.concurrent._

import akka.typed.{ActorContext, ActorSystem, Behavior, Signal}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, Promise}

object PrototypeApp extends App {
  /*
  state initial
  action search
  state searching
  event found
  action process
  state processing
  event processed
  action check
  event processed
  action check
  event done
  action terminate
   */

  var errorCount = 0

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val futureRunner = new FutureRunner {
    override def runInFuture[T](block: => T): Future[T] = {
      Future {
        try {
          block
        } catch {
          case ex: Throwable =>
            ex.printStackTrace()
            throw ex
        }
      }

    }
  }

  def doSearch(): Future[Seq[String]] = futureRunner.runInFuture(Seq("found-1", "found-2"))

  def process(name: String): Future[Int] = futureRunner.runInFuture(0)

  sealed trait State {
    def isDone: Boolean = false

    def update(name: String, status: Int): State = ???

    def results: Map[String, Int] = ???
  }

  object State {

    case object Initial extends State

    case object Searching extends State

    case class Processing(status: Map[String, Option[Int]]) extends State {
      override def isDone: Boolean = {
        def isNameDone(name: String, result: Option[Int]): Boolean = {
          result.isDefined
        }

        status.forall((isNameDone _).tupled)
      }
    }

  }

  sealed trait Event

  object Event {

    case object Ready extends Event

    case class Found(names: Seq[String]) extends Event

    case class Processed(name: String, result: Int) extends Event

  }

  trait EventHandler {
    def handle(event: Event): Unit
  }

  class EventHandlerImpl extends EventHandler {
    override def handle(event: Event): Unit = {
      event match {
        case Event.Ready =>
          ???
        case Event.Found(names) =>
          ???
        case Event.Processed(name, result) =>
          ???
      }
    }
  }

  sealed trait Action {
    def execute(eventHandler: EventHandler): Unit
  }

  object Action {

    case object Search extends Action {
      override def execute(eventHandler: EventHandler): Unit = {
        futureRunner.runInFuture {
          eventHandler.handle(Event.Found(Seq("found-1", "found-2")))
        }
      }
    }

    case class Process(name: String) extends Action {
      override def execute(eventHandler: EventHandler): Unit = {
        futureRunner.runInFuture {
          eventHandler.handle(Event.Processed(name, 0))
        }
      }
    }

    case class Update(name: String, result: Int) extends Action {
      override def execute(eventHandler: EventHandler): Unit = {
        futureRunner.runInFuture {
          println(s"$name - $result")
        }
      }
    }

    case class Report(results: Map[String, Int]) extends Action {
      override def execute(eventHandler: EventHandler): Unit = {
        def displayResult(name: String, result: Int) = {
          println(s"  $name $result")
        }

        println("final results")
        results.foreach((displayResult _).tupled)
      }
    }

  }

  def createInitialProcessingStatus(names: Seq[String]): Map[String, Option[Int]] = {
    names.map(name => name -> None).toMap
  }

  class MyBehavior(eventHandler: EventHandler, done: Promise[Unit]) extends Behavior[Event] {
    private var state: State = State.Initial

    override def management(ctx: ActorContext[Event], msg: Signal): Behavior[Event] = {
      println(s"management($msg)")
      this
    }

    override def message(ctx: ActorContext[Event], msg: Event): Behavior[Event] = {
      println(s"message($msg)")
      msg match {
        case Event.Ready =>
          state = State.Searching
          Action.Search.execute(eventHandler)
        case Event.Found(names) =>
          val initialProcessingStatus = createInitialProcessingStatus(names)
          state = State.Processing(initialProcessingStatus)
          names.foreach(Action.Process(_).execute(eventHandler))
        case Event.Processed(name, result) =>
          state = state.update(name, result)
          Action.Update(name, result)
      }
      if (state.isDone) {
        Action.Report(state.results)
        done.success(())
      }
      this
    }
  }

  val done: Promise[Unit] = Promise()
  val eventHandler: EventHandler = new EventHandlerImpl
  val behavior: Behavior[Event] = new MyBehavior(eventHandler, done)
  val actorSystem: ActorSystem[Event] = ActorSystem("behavior", behavior)
  val duration: Duration = Duration(5, TimeUnit.SECONDS)

  actorSystem ! Event.Ready
  Await.ready(done.future, duration)
  actorSystem.terminate()
  println(errorCount)
}
