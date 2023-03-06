package asn3_2

import asn3_2.problem1._

import org.scalatest.flatspec.AnyFlatSpec

class problem1Test extends AnyFlatSpec {
  "pyth()" should "return [] with 1" in {
    assert(pyth(1) == List())
  }
  it should "return [(3, 4, 5), (4, 3, 5), (6, 8, 10), (8, 6, 10)] with 10" in {
    assert(pyth(10) == List((3, 4, 5), (4, 3, 5), (6, 8, 10), (8, 6, 10)))
  }
  it should "return a list of pythagorean triples where z <= 100 with integer 100" in {
    val n = 100
    val result = pyth(n).filterNot(x => x._3 <= n && math.pow(x._1, 2) + math.pow(x._2, 2) == math.pow(x._3, 2))
    assert(result.isEmpty)
  }
}
