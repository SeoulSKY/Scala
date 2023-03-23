//package asn4
//
//import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props}
//
//import java.time.Instant
//import scala.annotation.unused
//import scala.language.postfixOps
//
//object problem2 extends App {
//  val system = ActorSystem("main")
//  val shuffler = system.actorOf(Props[Shuffler]())
//
//  val deck = Range(1, 53).toList
//
//}
//
//object Shuffler {
//  /**
//   * Wrapper to send request to this actor
//   * @param deck A deck of cards with even number of elements to shuffle
//   * @param n Number of times to shuffle
//   * @param isOutShuffle Whether the shuffles should be out-shuffle or not
//   * @tparam A The type of the elements in the deck
//   */
//  final case class Request[A](deck: List[A], n: Int, isOutShuffle: Boolean)
//}
//
//class Shuffler extends Actor {
//  import Shuffler._
//
//  var requestTable: Map[String, (Int, ActorRef, ActorRef, ActorRef)] = Map()
//
//  def receive: Receive = {
//    case Request(deck, n, isOutShuffle) =>
//      val key = Instant.now().toString
//      val splitter = context.actorOf(Props[Splitter]())
//      val faroShuffler = context.actorOf(Props[FaroShuffler[deck.type]](), key)
//
//      requestTable += key -> (n, sender(), splitter, faroShuffler)
//
//      splitter ! Splitter.Request(deck, key)
//      faroShuffler ! FaroShuffler.ShuffleMethod(isOutShuffle)
//  }
//}
//
//object Splitter {
//  /**
//   * Wrapper to send request to this actor
//   * @param deck A deck of cards with even number of elements to shuffle
//   * @param name The name of the the paired faro shuffler
//   * @tparam A The type of the elements in the deck
//   */
//  final case class Request[A](deck: List[A], name: String)
//}
//
//class Splitter extends Actor {
//  import Splitter._
//
//  def receive: Receive = {
//    case Request(deck, name) =>
//      val faroShuffler = context.actorSelection(s"../$name")
//
//      faroShuffler ! deck.zipWithIndex.filter(_._2 % 2 == 0).map(_._1)
//      faroShuffler ! deck.zipWithIndex.filter(_._2 % 2 != 0).map(_._1)
//  }
//}
//
//object FaroShuffler {
//  /**
//   * Wrapper to send a split deck to this actor
//   * @param deck A split deck of cards to shuffle
//   * @tparam A The type of the elements in the deck
//   */
//  case class SplitDeck[A](deck: List[A])
//
//  /**
//   * Wrapper to send a shuffle method to this actor
//   * @param isOutShuffle Whether the shuffles should be out-shuffle or not
//   */
//  final case class ShuffleMethod(isOutShuffle: Boolean)
//
//  private case object Ready
//}
//
//class FaroShuffler[A] extends Actor {
//  import FaroShuffler._
//
//  var decks: (Option[List[A]], Option[List[A]]) = (None, None)
//  var isOutShuffle: Option[Boolean] = None
//
//  def receive: Receive = {
//    case SplitDeck(deck: List[A]) if decks._1.isEmpty =>
//      decks = (decks._1, Some(deck))
//      sendIfReady()
//    case SplitDeck(deck: List[A]) =>
//      decks = (Some(deck), decks._2)
//      sendIfReady()
//    case ShuffleMethod(isOutShuffle: Boolean) =>
//      this.isOutShuffle = Some(isOutShuffle)
//      sendIfReady()
//    case Ready =>
//      val collector = context.actorOf(Props[CardCollector]())
//      val n = decks._1.get.length + decks._2.get.length
//      collector ! CardCollector.MetaData(context.parent.path.name, n)
//
//      decks = if (isOutShuffle.get) decks else decks.swap
//
//      Range(0, n).foreach(_ =>
//        collector ! CardCollector.Card(decks._1.get.head)
//        decks = (Some(decks._1.get.tail), decks._2)
//        decks = decks.swap
//      )
//
//      context.stop(self)
//    case _ => throw new IllegalArgumentException("Invalid type of message")
//  }
//
//  private def sendIfReady(): Unit = {
//    if (decks._1.isDefined && decks._2.isDefined && isOutShuffle.isDefined) {
//      context.self ! Ready
//    }
//  }
//}
//
//object CardCollector {
//  /**
//   * Wrapper to send meta data to this actor
//   * @param name The name of the shuffler to send the result
//   * @param numCards Number of cards to receive
//   */
//  final case class MetaData(name: String, numCards: Int)
//
//  /**
//   * Wrapper to send a card to this actor
//   * @param card The card to collect
//   */
//  final case class Card[A](card: A)
//}
//
//class CardCollector extends Actor {
//  def receive: Receive = {
//
//  }
//}
