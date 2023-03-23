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
  val client = system.actorOf(Props[Sorter]())

  val unsorted = List(1, 5, 2, 4, 3)
  client.ask(Sorter.Request(unsorted))(3 seconds).onComplete {
    case Success(sorted) => assert(unsorted.sorted == sorted)
    case Failure(exception) => exception.printStackTrace()
  }

  system.terminate()
}

object Sorter {
  /**
   * A wrapper to send requests to this actor
   * @param unsorted The list of integers to be sorted
   */
  final case class Request(unsorted: List[Int])
}

/**
 * Actor for sorting a list of integers
 */
class Sorter extends Actor {
  import Sorter.*

  var requestTable: Map[ActorRef, (ActorRef, List[Int], List[Int])] = Map()

  def receive: Receive = {
    case Request(Nil) =>
      sender() ! Nil
    case Request(unsorted) =>
      val sorter = context.actorOf(Props[SorterNode](), Instant.now().toString)
      requestTable += sorter -> (sender(), unsorted, Nil)

      unsorted.appended(END).foreach(sorter ! SorterNode.Sort(_))
    case SorterNode.Result(n: Int) =>
      val sorter = sender()
      var (client, unsorted, sorted) = requestTable(sorter)
      sorted = sorted appended n

      if (sorted.length == unsorted.length) {
        client ! sorted
        context stop sorter
        requestTable -= sorter
      } else {
        requestTable += sorter -> (client, unsorted, sorted)
      }
  }
}

object SorterNode {
  final case class Sort(n: Int)
  final case class Result(n: Int)
}

/**
 * Node actor for sorting
 */
class SorterNode extends Actor {
  import SorterNode.*

  var min: Option[Int] = None

  def receive: Receive = {
    case Sort(END) =>
      sender() ! Result(min.get)

      if (context.children.nonEmpty) {
        if (Try(Instant.parse(self.path.name)).isSuccess) {
          context.children.head ! Sort(END)
        } else {
          context.children.head forward Sort(END)
        }
      }
    case Sort(n) if min.isEmpty =>
      min = Some(n)
    case Sort(n) =>
      val child = context.children.headOption.getOrElse(context.actorOf(Props[SorterNode]()))

      if (n < min.get) {
        child ! Sort(min.get)
        min = Some(n)
      } else {
        child ! Sort(n)
      }
    case Result(n) =>
      context.actorSelection(self.path.parent) ! Result(n)
  }
}
