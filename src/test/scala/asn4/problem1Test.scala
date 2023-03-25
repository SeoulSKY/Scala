package asn4

import asn4.problem1._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers


class problem1Test
  extends TestKit(ActorSystem("main"))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "SorterNode" should {
    "send back 1, 2, 3, 4 and 5 in order with 1, 5, 2, 4, 3 and 0" in {
      val sorter = system.actorOf(Props[SorterNode]())
      val unsorted = List(1, 5, 2, 4, 3)

      unsorted.appended(0).foreach(sorter ! SorterNode.Sort(_))
      List(1, 2, 3, 4, 5).foreach(n => expectMsg(SorterNode.Result(n)))
      expectNoMessage()
    }

    "send back 1 with 1 and 0" in {
      val sorter = system.actorOf(Props[SorterNode]())
      List(1, 0).foreach(sorter ! SorterNode.Sort(_))

      expectMsg(SorterNode.Result(1))
      expectNoMessage()
    }

    "send back 1, 1, 2, 2 and 3 in order with 1, 3, 2, 1, 2 and 0" in {
      val sorter = system.actorOf(Props[SorterNode]())
      val unsorted = List(1, 3, 2, 1, 2)

      unsorted.appended(0).foreach(sorter ! SorterNode.Sort(_))
      List(1, 1, 2, 2, 3).foreach(n => expectMsg(SorterNode.Result(n)))
      expectNoMessage()
    }
  }

  "Sorter" should {
    "send back [1, 2, 3, 4, 5] with [1, 5, 2, 4, 3]" in {
      val client = system.actorOf(Props[Sorter]())
      val unsorted = List(1, 2, 3, 4, 5)

      client ! Sorter.Request(unsorted)
      expectMsg(List(1, 2, 3, 4, 5))
      expectNoMessage()
    }

    "send back [1] with [1]" in {
      val client = system.actorOf(Props[Sorter]())
      val unsorted = List(1)

      client ! Sorter.Request(unsorted)
      expectMsg(List(1))
      expectNoMessage()
    }

    "send back Nil with Nil" in {
      val client = system.actorOf(Props[Sorter]())
      val unsorted = Nil

      client ! Sorter.Request(unsorted)
      expectMsg(Nil)
      expectNoMessage()
    }

    "send back [1, 1, 2, 2, 3] with [1, 3, 2, 1, 2]" in {
      val client = system.actorOf(Props[Sorter]())
      val unsorted = List(1, 3, 2, 1, 2)

      client ! Sorter.Request(unsorted)
      expectMsg(List(1, 1, 2, 2, 3))
      expectNoMessage()
    }
  }
}
