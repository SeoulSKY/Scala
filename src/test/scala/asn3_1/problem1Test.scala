package asn3_1

import asn3_1.problem1._

import org.scalatest.flatspec.AnyFlatSpec
import scala.runtime.stdLibPatches.Predef.assert

class problem1Test extends AnyFlatSpec {
  "children()" should "return None with invalid parents" in {
    assert(children("hello", "world").isEmpty)
    assert(children(loyalFamily.head._2._2, "world").isEmpty)
    assert(children("hello", loyalFamily.head._2._2).isEmpty)
  }
  it should "return Some of George, Charlotte and Louis with William and Catherine" in {
    assert(children("William", "Catherine").get.sorted == List("George", "Charlotte", "Louis").sorted)
    assert(children("Catherine", "William").get.sorted == List("George", "Charlotte", "Louis").sorted)
  }
  it should "return Some of a list with length 14 with no parents" in {
    assert(children("", "").get.length == 14)
  }

  "parents()" should "return None with invalid person" in {
    assert(parents("Hello").isEmpty)
  }
  it should "return Some of Nil with Elizabeth" in {
    assert(parents("Elizabeth").get.isEmpty)
  }
  it should "return Some of Elizabeth and Philip with Charles" in {
    assert(parents("Charles").get.sorted == List("Elizabeth", "Philip"))
  }

  "grandparents()" should "return Some of Diana and Camilla with George" in {
    assert(grandparents("George").get.sorted == List("Diana", "Charles").sorted)
  }
  it should "return Some of Elizabeth and Philip with William" in {
    assert(grandparents("William").get == List("Elizabeth", "Philip"))
  }
  it should "return Some of Andrew and Sarah with August" in {
    assert(grandparents("August").get.sorted == List("Andrew", "Sarah").sorted)
  }
  it should "return None with invalid person" in {
    assert(grandparents("Hello").isEmpty)
  }
  it should "return Some of Nil with Charles" in {
    assert(grandparents("Charles").get.isEmpty)
  }

  "sisters()" should "return None with invalid person" in {
    assert(sisters("Hello").isEmpty)
  }
  it should "return Some of Nil with Elizabeth" in {
    assert(sisters("Elizabeth").get.isEmpty)
  }
  it should "return Some of Charlotte with George or Louis" in {
    assert(sisters("George").get == List("Charlotte"))
    assert(sisters("Louis").get == List("Charlotte"))
  }
  it should "return Some of Mia and Lena with Lucas" in {
    assert(sisters("Lucas").get.sorted == List("Mia", "Lena").sorted)
  }
  it should "return Some of Mia with Lena" in {
    assert(sisters("Lena").get == List("Mia"))
  }

  "firstCousins()" should "return None with invalid person" in {
    assert(firstCousins("Hello").isEmpty)
  }
  it should "return Some of Nil with Elizabeth or Charles" in {
    assert(firstCousins("Elizabeth").get.isEmpty)
    assert(firstCousins("Charles").get.isEmpty)
  }
  it should "return Some of Archie and Lilibet with Charlotte" in {
    assert(firstCousins("Charlotte").get.sorted == List("Archie", "Lilibet").sorted)
  }
  it should "return Some of Peter, Zara, Beatrice, Eugenie, Louise and James with William" in {
    assert(firstCousins("William").get.sorted == List("Peter", "Zara", "Beatrice", "Eugenie", "Louise", "James").sorted)
  }

  "uncles()" should "return None with invalid person" in {
    assert(uncles("Hello").isEmpty)
  }
  it should "return Some of Nil with Elizabeth or Charles" in {
    assert(uncles("Elizabeth").get.isEmpty)
    assert(uncles("Charles").get.isEmpty)
  }
  it should "return Some of Harry with George or Louis" in {
    assert(uncles("George").get == List("Harry"))
    assert(uncles("Louis").get == List("Harry"))
  }
  it should "return Some of Edoardo with August" in {
    assert(uncles("August").get == List("Edoardo"))
  }
}
