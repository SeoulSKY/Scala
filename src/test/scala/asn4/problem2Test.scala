package asn4

import asn4.problem2.*
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.Instant
import scala.language.postfixOps
import scala.reflect.ClassTag


class problem2Test
  extends TestKit(ActorSystem("main"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  object Proxy {
    final case class Request(data: Any)
  }

  class Proxy[A <: Actor : ClassTag] extends Actor {
    import Proxy._

    var client: Option[ActorRef] = None
    val target: ActorRef = context.actorOf(Props[A]())

    override def receive: Receive = {
      case Request(data) =>
        client = Some(sender())
        target ! data
      case response => client.get forward response
    }
  }

  "CardCollector" should {
    "send back [1, 2, 3, 4, 5] with name = test1, numCards = 5 and then 1, 2, 3, 4, 5" in {
      val proxy = system.actorOf(Props(new Proxy[CardCollector[Int]]()), "test1")

      val unshuffled = Range(1, 6).toList

      proxy ! Proxy.Request(CardCollector.MetaData("test1", unshuffled.length))
      unshuffled.foreach(n => proxy ! Proxy.Request(CardCollector.Card(n)))

      expectMsg(CardCollector.Result(unshuffled))
      expectNoMessage()
    }

    "send back [\"hello\", \"world\"] with name = test2, numCards = 2 and then \"hello\", \"world\"" in {
      val proxy = system.actorOf(Props(new Proxy[CardCollector[String]]()), "test2")

      val unshuffled = List("hello", "world")

      proxy ! Proxy.Request(CardCollector.MetaData("test2", unshuffled.length))
      Thread.sleep(10)
      unshuffled.foreach(s => proxy ! Proxy.Request(CardCollector.Card(s)))

      expectMsg(CardCollector.Result(unshuffled))
      expectNoMessage()
    }

    "send back [1] with name = test3, numCards = 1 and then 1" in {
      val proxy = system.actorOf(Props(new Proxy[CardCollector[String]]()), "test3")

      val unshuffled = List(1)

      proxy ! Proxy.Request(CardCollector.MetaData("test3", unshuffled.length))
      Thread.sleep(10)
      unshuffled.foreach(s => proxy ! Proxy.Request(CardCollector.Card(s)))

      expectMsg(CardCollector.Result(unshuffled))
      expectNoMessage()
    }
  }

  "FaroShuffler" should {
    "create a CardCollector and it should send back [1, 4, 2, 5, 3, 6] with [1, 2, 3] and [4, 5, 6] and isOutShuffle = true" in {
      val proxy = system.actorOf(Props(new Proxy[FaroShuffler[Int]]()), Instant.now().toString)

      val deck1 = List(1, 2, 3)
      val deck2 = List(4, 5, 6)
      val isOutShuffle = true

      proxy ! Proxy.Request(FaroShuffler.SplitDeck(deck1))
      Thread.sleep(10)
      proxy ! Proxy.Request(FaroShuffler.SplitDeck(deck2))
      proxy ! Proxy.Request(FaroShuffler.ShuffleMethod(isOutShuffle))

      expectMsg(CardCollector.Result(List(1, 4, 2, 5, 3, 6)))
      expectNoMessage()
    }

    "create a CardCollector and it should send back [4, 1, 5, 2, 6, 3] with [1, 2, 3] and [4, 5, 6] and isOutShuffle = false" in {
      val proxy = system.actorOf(Props(new Proxy[FaroShuffler[Int]]()), Instant.now().toString)

      val deck1 = List(1, 2, 3)
      val deck2 = List(4, 5, 6)
      val isOutShuffle = false

      proxy ! Proxy.Request(FaroShuffler.SplitDeck(deck1))
      Thread.sleep(10)
      proxy ! Proxy.Request(FaroShuffler.SplitDeck(deck2))
      proxy ! Proxy.Request(FaroShuffler.ShuffleMethod(isOutShuffle))

      expectMsg(CardCollector.Result(List(4, 1, 5, 2, 6, 3)))
      expectNoMessage()
    }
  }

  "Shuffler" should {

    "send back [1, 4, 2, 5, 3, 6] with [1, 2, 3, 4, 5, 6], n = 1 and isOutShuffle = true" in {
      val shuffler = system.actorOf(Props[Shuffler[Int]]())

      val deck = Range(1, 7).toList
      val n = 1
      val isOutShuffle = true

      shuffler ! Shuffler.Request(deck, n, isOutShuffle)

      expectMsg(List(1, 4, 2, 5, 3, 6))
      expectNoMessage()
    }

    "send back [1, 2, 3, ..., 52] with [1, 2, 3, ..., 52], n = 8 and isOutShuffle = true" in {
      val shuffler = system.actorOf(Props[Shuffler[Int]]())

      val deck = Range(1, 53).toList
      val n = 8
      val isOutShuffle = true

      shuffler ! Shuffler.Request(deck, n, isOutShuffle)

      expectMsg(deck)
      expectNoMessage()
    }

    "send back [52, 51, 50, ..., 1] with [1, 2, 3, ..., 52], n = 26 and isOutShuffle = false" in {
      val shuffler = system.actorOf(Props[Shuffler[Int]]())

      val deck = Range(1, 53).toList
      val n = 26
      val isOutShuffle = false

      shuffler ! Shuffler.Request(deck, n, isOutShuffle)

      expectMsg(deck.reverse)
      expectNoMessage()
    }
  }
}