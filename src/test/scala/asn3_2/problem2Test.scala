package asn3_2

import asn3_2.problem2._


import org.scalatest.flatspec.AnyFlatSpec


class problem2Test() extends AnyFlatSpec {
  "factors()" should "return [1] with n=1" in {
    assert(factors(1) == LazyList(1))
  }
  it should "return [1, 2, 4, 8] with n=8" in {
    assert(factors(8) == LazyList(1, 2, 4, 8))
  }
  "perfectNumbers()" should "generate [6, 28, 496] as the first 3 elements" in {
    assert(perfectNumbers().take(3) == LazyList(6, 28, 496))
  }
}
