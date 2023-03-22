package asn4

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.annotation.unused
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
 * A sentinel to stop sorting
 */
val END: Int = 0

object problem1 extends App {
  val system = ActorSystem("main")
  val client = system.actorOf(Props[SorterClient]())

  val unsorted = List(1, 5, 2, 4, 3)
  val future = client.ask(SorterClient.Request(unsorted))(3 seconds)

  future.onComplete {
    case Success(sorted) => assert(unsorted.sorted == sorted); system.terminate()
    case Failure(exception) => exception.printStackTrace(); system.terminate()
  }
}

/**
 * Client actor for sending messages to the filter actors
 */
object SorterClient {
  final case class Request(unsorted: List[Int])
}

class SorterClient extends Actor {
  import SorterClient.*

  var requestQueue: List[(ActorRef, List[Int])] = List()
  val sorter: ActorRef = context.actorOf(Props[Sorter](), "sorter")

  var sorted: List[Int] = List()

  def receive: Receive = {
    case Request(unsorted) if unsorted == Nil =>
      sender() ! Nil
    case Request(unsorted) =>
      requestQueue = requestQueue.appended(sender(), unsorted)
      unsorted.appended(END).foreach(sorter ! Sorter.Sort(_))
    case Sorter.Result(n) =>
      sorted = sorted.appended(n)

      if (sorted.length == requestQueue.head._2.length) {
        requestQueue.head._1 ! sorted
        requestQueue = requestQueue.tail
        sorted = List()
      }
  }
}

/**
 * A filter actor for sorting
 */
object Sorter {
  final case class Sort(n: Int)
  final case class Result(n: Int)
}

class Sorter extends Actor {
  import Sorter.*

  var min: Option[Int] = None

  def receive: Receive = {
    case Sort(n) if n == END =>
      sender() ! Result(min.get)

      if (context.children.nonEmpty) {
        context.children.head forward Sort(END)
      }
    case Sort(n) if min.isEmpty =>
      min = Some(n)
    case Sort(n) =>
      val child = if (context.children.nonEmpty) context.children.head else context.actorOf(Props[Sorter]())

      if (n < min.get) {
        child ! Sort(min.get)
        min = Some(n)
      } else {
        child ! Sort(n)
      }
  }
}
