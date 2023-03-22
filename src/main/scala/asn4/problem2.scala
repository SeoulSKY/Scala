//package asn4
//
//import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props}
//
//import java.util.Calendar
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
//  case class Request[A](deck: List[A], n: Int, isOutShuffle: Boolean)
//}
//
//class Shuffler extends Actor {
//  import Shuffler._
//
//  var requestQueue: List[ActorRef] = List()
//
//  private val splitter = context.actorOf(Props[Splitter]())
//  private val faroShuffler = context.actorOf(Props[FaroShuffler](), Calendar.getInstance().getTime.toString)
//
//  def receive: Receive = {
//    case Request(deck, n, isOutShuffle) =>
//      requestQueue = requestQueue.appended(sender())
//
//      splitter ! Splitter.Request(deck, faroShuffler.path)
//      faroShuffler ! FaroShuffler.ShuffleMethod(isOutShuffle)
//  }
//}
//
//object Splitter {
//  /**
//   * Wrapper to send request to this actor
//   * @param deck A deck of cards with even number of elements to shuffle
//   * @param path The path to the paired faro shuffler
//   * @tparam A The type of the elements in the deck
//   */
//  case class Request[A](deck: List[A], path: ActorPath)
//}
//
//class Splitter extends Actor {
//  import Splitter._
//
//  def receive: Receive = {
//    case Request(deck, tag) =>
//      val faroShuffler = context.child(tag).getOrElse(context.actorOf(Props[FaroShuffler](), tag))
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
//  case class ShuffleMethod(isOutShuffle: Boolean)
//}
//
//class FaroShuffler extends Actor {
//  def receive: Receive = {
//
//  }
//}
//
//object CardCollector {
//
//}
//
//class CardCollector extends Actor {
//  def receive: Receive = {
//
//  }
//}
