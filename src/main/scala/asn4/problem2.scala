package asn4

import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Identify, Props}
import asn4.CardCollector.MetaData
import akka.pattern.ask
import akka.util.Timeout

import java.time.Instant
import scala.annotation.unused
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import concurrent.duration.DurationInt
import concurrent.ExecutionContext.Implicits.global

object problem2 extends App {
  // usage example
  val system = ActorSystem("main")
  val shuffler = system.actorOf(Props[Shuffler[Int]](), "shuffler")

  val deck = Range(1, 53).toList
  shuffler.ask(Shuffler.Request(deck, 1, true))(3 seconds).onComplete {
    case Success(shuffled) => println(shuffled)
    case Failure(exception) => exception.printStackTrace()
  }

  system.terminate()
}

object Shuffler {
  /**
   * Wrapper to send request to this actor
   * @param deck A deck of cards with even number of elements to shuffle
   * @param n Number of times to shuffle
   * @param isOutShuffle Whether the shuffles should be out-shuffle or not
   * @tparam A The type of the elements in the deck
   */
  final case class Request[A](deck: List[A], n: Int, isOutShuffle: Boolean)
}

/**
 * Shuffle the given deck n times
 * @tparam A The type of the elements in the deck
 */
class Shuffler[A] extends Actor {
  import Shuffler._

  var requestTable: Map[String, (ActorRef, Int, Boolean, ActorRef, ActorRef)] = Map()

  def receive: Receive = {
    case Request(deck, n, isOutShuffle) =>
      val key = Instant.now().toString
      val splitter = context.actorOf(Props[Splitter]())
      val faroShuffler = context.actorOf(Props[FaroShuffler[A]](), key)

      requestTable += key -> (sender(), n, isOutShuffle, splitter, faroShuffler)

      splitter ! Splitter.Request(deck, key)
      faroShuffler ! FaroShuffler.ShuffleMethod(isOutShuffle)
    case CardCollector.Result(shuffled) =>
      val key = sender().path.elements
        .map(s => Try(Instant.parse(s)))
        .find(_.isSuccess)
        .get.get.toString

      val (client, n, isOutShuffle, splitter, faroShuffler) = requestTable(key)

      if (n <= 1) {
        client ! shuffled
        context stop splitter
        context stop faroShuffler
        requestTable -= key
      } else {
        requestTable += key -> (client, n - 1, isOutShuffle, splitter, faroShuffler)
        self.tell(Request(shuffled, n - 1, isOutShuffle), client)
      }
  }
}

object Splitter {
  /**
   * Wrapper to send request to this actor
   * @param deck A deck of cards with even number of elements to shuffle
   * @param name The name of the the paired faro shuffler
   * @tparam A The type of the elements in the deck
   */
  final case class Request[A](deck: List[A], name: String)
}

/**
 * Split the given deck into two equal sized decks
 */
class Splitter extends Actor {
  import Splitter._

  def receive: Receive = {
    case Request(deck, name) =>
      val faroShuffler = context.actorSelection(s"../$name")

      faroShuffler ! FaroShuffler.SplitDeck(deck.take(deck.length / 2))
      faroShuffler ! FaroShuffler.SplitDeck(deck.takeRight(deck.length / 2))
  }
}

object FaroShuffler {
  /**
   * Wrapper to send a split deck to this actor
   * @param deck A split deck of cards to shuffle
   * @tparam A The type of the elements in the deck
   */
  case class SplitDeck[A](deck: List[A])

  /**
   * Wrapper to send a shuffle method to this actor
   * @param isOutShuffle Whether the shuffles should be out-shuffle or not
   */
  final case class ShuffleMethod(isOutShuffle: Boolean)

  private case object Ready
}

/**
 * Perform a faro shuffle
 * @tparam A The type of the elements in the deck
 */
class FaroShuffler[A] extends Actor {
  import FaroShuffler._

  var decks: (Option[List[A]], Option[List[A]]) = (None, None)
  var isOutShuffle: Option[Boolean] = None

  def receive: Receive = {
    case SplitDeck(deck): SplitDeck[A] if decks._1.isEmpty =>
      decks = (Some(deck), None)
    case SplitDeck(deck): SplitDeck[A] =>
      decks = (decks._1, Some(deck))
      sendIfReady()
    case ShuffleMethod(isOutShuffle) =>
      this.isOutShuffle = Some(isOutShuffle)
      sendIfReady()
    case Ready =>
      val collector = context.actorOf(Props[CardCollector[A]]())
      val n = decks._1.get.length + decks._2.get.length
      collector ! CardCollector.MetaData(context.parent.path.name, n)

      decks = if (isOutShuffle.get) decks else decks.swap

      Range(0, n).foreach(_ =>
        collector ! CardCollector.Card(decks._1.get.head)
        decks = (Some(decks._1.get.tail), decks._2)
        decks = decks.swap
      )
  }

  private def sendIfReady(): Unit = {
    if (decks._1.isDefined && decks._2.isDefined && isOutShuffle.isDefined) {
      self forward Ready
    }
  }
}

object CardCollector {
  /**
   * Wrapper to send meta data to this actor
   * @param name The name of the shuffler to send the result
   * @param numCards Number of cards to receive
   */
  final case class MetaData(name: String, numCards: Int)

  /**
   * Wrapper to send a card to this actor
   * @param card The card to collect
   */
  final case class Card[A](card: A)

  final case class Result[A](collected: List[A])
}

/**
 * Collect individual cards to create a deck
 * @tparam A The type of the elements in the deck
 */
class CardCollector[A] extends Actor {
  import CardCollector._

  var metaData: Option[MetaData] = None
  var deck: List[A] = Nil

  def receive: Receive = {
    case MetaData(name, numCards) =>
      metaData = Some(MetaData(name, numCards))
    case Card(_) if metaData.isEmpty =>
      throw new IllegalStateException("Metadata doesn't exist yet")
    case Card(card): Card[A] =>
      deck = deck appended card
      if (deck.length == metaData.get.numCards) {
        val path = context.parent.path.elements.toSeq
          .reverse
          .dropWhile(_ != metaData.get.name)
          .reverse
          .mkString("/")
        context.system.actorSelection(path) ! Result(deck)
      }
  }
}
