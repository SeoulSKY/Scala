package asn2

import asn2.problem3.*
import org.scalatest.flatspec.AnyFlatSpec

class problem3Test extends AnyFlatSpec {
  "luhnDouble()" should "return 6 if 3 is given" in {
    assert(luhnDouble(3) == 6)
  }
  it should "return 3 if 6 is given" in {
    assert(luhnDouble(6) == 3)
  }
  it should "return 8 if 4 is given" in {
    assert(luhnDouble(4) == 8)
  }
  it should "return 1 if 5 is given" in {
    assert(luhnDouble(5) == 1)
  }
  it should "return 9 if 9 is given" in {
    assert(luhnDouble(9) == 9)
  }

  "altMap()" should "return [10, 101, 12, 103, 14] with elements [0, 1, 2, 3, 4] and functions [_ + 10, _ + 100]" in {
    assert(altMap(List((a: Int) => a + 10, (a: Int) => a + 100)) (List(0, 1, 2, 3, 4)) == List(10, 101, 12, 103, 14))
  }
  it should "return [1, 4, 9, 16, 25] with elements [1, 2, 3, 4, 5] and functions [a => a * a]" in {
    assert(altMap(List((a: Int) => a * a)) (List(1, 2, 3, 4, 5)) == List(1, 4, 9, 16, 25))
  }
  it should "return [\"Hello\", \"World\"] with elements [\"\", \"\"] and functions [_ => \"Hello\", _ => \"World\"]" in {
    assert(altMap(List(_ => "Hello", _ => "World")) (List("", "")) == List("Hello", "World"))
  }

  "luhn()" should "return true with [7, 9, 9, 2, 7, 3, 9, 8, 7, 1, 3]" in {
    assert(luhn(List(7, 9, 9, 2, 7, 3, 9, 8, 7, 1, 3)))
  }
}
