package asn4

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import java.time.Instant
import scala.annotation.unused
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

/**
 * A sentinel to stop sorting
 */
val END: Int = 0

object problem1 extends App {
  // usage example
  val system = ActorSystem("main")
  val client = system.actorOf(Props[SorterClient]())

  val unsorted = List(1, 5, 2, 4, 3)
  client.ask(SorterClient.Request(unsorted))(3 seconds).onComplete {
    case Success(sorted) => assert(unsorted.sorted == sorted)
    case Failure(exception) => exception.printStackTrace()
  }

  system.terminate()
}

/**
 * Client actor for sending messages to the filter actors
 */
object SorterClient {
  /**
   * A wrapper to send requests to this actor
   * @param unsorted The list of integers to be sorted
   */
  final case class Request(unsorted: List[Int])
}

/**
 * Client actor for sending messages to the filter actors
 */
class SorterClient extends Actor {
  import SorterClient.*

  var requestTable: Map[String, (ActorRef, ActorRef, List[Int], List[Int])] = Map()

  def receive: Receive = {
    case Request(Nil) =>
      sender() ! Nil
    case Request(unsorted) =>
      val key = Instant.now().toString
      val sorter = context.actorOf(Props[Sorter](), key)
      requestTable += key -> (sender(), sorter, unsorted, Nil)

      unsorted.appended(END).foreach(sorter ! Sorter.Sort(_))
    case Sorter.Result(n: Int) =>
      val key = sender().path.elements
        .find(s => Try(Instant.parse(s)).isSuccess)
        .map(Instant.parse)
        .get.toString
      
      var t = requestTable(key)
      t = (t._1, t._2, t._3, t._4.appended(n))
      if (t._4.length == t._3.length) {
        t._1 ! t._4
        context.stop(t._2)
        requestTable -= key
      } else {
        requestTable += key -> t
      }
    case _ => throw new IllegalArgumentException("Invalid type of message")
  }
}

/**
 * Filter actor for sorting
 */
object Sorter {
  final case class Sort(n: Int)
  final case class Result(n: Int)
}

/**
 * A filter actor for sorting
 */
class Sorter extends Actor {
  import Sorter.*

  var min: Option[Int] = None

  def receive: Receive = {
    case Sort(END) =>
      sender() ! Result(min.get)

      if (context.children.nonEmpty) {
        context.children.head forward Sort(END)
      }
    case Sort(n) if min.isEmpty =>
      min = Some(n)
    case Sort(n) =>
      val child = context.children.headOption.getOrElse(context.actorOf(Props[Sorter]()))

      if (n < min.get) {
        child ! Sort(min.get)
        min = Some(n)
      } else {
        child ! Sort(n)
      }
    case _ => throw new IllegalArgumentException("Invalid type of message")
  }
}
