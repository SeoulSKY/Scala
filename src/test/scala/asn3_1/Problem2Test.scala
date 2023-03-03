package asn3_1

import asn3_1.problem2._
import asn3_1.problem2.Partial._

import org.scalatest.flatspec.AnyFlatSpec
import scala.runtime.stdLibPatches.Predef.assert

class Problem2Test extends AnyFlatSpec {
  "map()" should "return the same errors when errors are given" in {
    val es = Errors(List("Hello", "World"))
    assert(map(es)(_ => 1) == es)
  }
  it should "return the Success of the function's evaluated value when a success is given" in {
    assert(map(Success(1))(_ + 3) == Success(4))
  }

  "flatMap()" should "return the same errors when errors are given" in {
    val es = Errors(List("Hello", "World"))
    assert(flatMap(es)(_ => Success(1)) == es)
  }
  it should "return the function's evaluated value when a success is given" in {
    assert(flatMap(Success(5))(x => Success(x + 1)) == Success(6))

    val es = Errors(List("Hello", "World"))
    assert(flatMap(Success(5))(_ => es) == es)
  }

  "getOrElse()" should "return the default value when errors are given" in {
    assert(getOrElse(Errors(List("Hello", "World")))(5) == 5)
  }
  it should "return the same value when a success is given" in {
    assert(getOrElse(Success(8))(1) == 8)
  }

  "orElse()" should "return the given success value when errors and a success are given" in {
    assert(orElse(Errors(List("Hello", "World")))(Success(5)) == Success(5))
  }
  it should "return the accumulated errors when errors and an error are given" in {
    assert(orElse(Errors(List("Hello", "World")))(Errors(List("a", "s"))) == Errors(List("Hello", "World", "a", "s")))
  }
  it should "return the same value when success is given" in {
    assert(orElse(Success(5))(Success(8)) == Success(5))
  }

  "map2()" should "return the given errors when one of the arguments is errors" in {
    val es = Errors(List("Hello", "World"))
    val es2 = Errors(List("a", "s"))

    assert(map2(es)(Success(1))((_, _) => "Hi") == es)
    assert(map2(Success(1))(es2)((_, _) => "Hi") == es2)
  }
  it should "return the accumulated errors when two arguments are errors" in {
    val es = Errors(List("Hello", "World"))
    val es2 = Errors(List("a", "s"))

    assert(map2(es)(es2)((_, _) => "Hi") == Errors(List("Hello", "World", "a", "s")))
  }
  it should "return the success of the function's evaluated value when two arguments are successes" in {
    assert(map2(Success(1))(Success(2))(_ + _) == Success(3))
  }

  "traverse()" should "return the same errors when a given seq contains one errors and multiple successes" in {
    val es = Errors(List("Hello", "World"))
    assert(traverse(List(Success(1), es, Success(4)))(s => Success(s + 5)) == es)
  }
  it should "return the success of a seq containing given function's evaluated values when a given seq contains only successes" in {
    assert(traverse(List(Success(1), Success(4)))(s => Success(s + 5)) == Success(List(6, 9)))
  }
  it should "return the accumulated errors of the given function's evaluated values when the given function sometimes evaluates to errors" in {
    val es = Errors(List("Hello", "World"))
    val es2 = Errors(List("a", "s"))

    assert(traverse(List(Success(1), Success(2), Success(3))) {
      case 1 => Success(1)
      case 2 => es
      case 3 => es2
    } == Errors(List("Hello", "World", "a", "s")))
  }

  "Try()" should "return Success of 1 when 1 is given" in {
    assert(Try(1) == Success(1))
  }
  it should "return errors of NumberFormatException with \"Hello\".toInt" in {
    Try("Hello".toInt) match {
      case Errors(exs) =>
        assert(exs.length == 1)
        assert(exs.head.isInstanceOf[NumberFormatException])
      case Success(_) => assert(false)
    }
  }
}
