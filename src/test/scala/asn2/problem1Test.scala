package asn2

import asn2.problem1.*
import org.scalatest.flatspec.AnyFlatSpec

class problem1Test extends AnyFlatSpec {
  "shuffle()" should "return Nil with two Nils" in {
    assert(shuffle(Nil, Nil) == Nil)
  }
  it should "return the first list when the second list is Nil" in {
    assert(shuffle(List.range(0, 5), Nil) == List.range(0, 5))
  }
  it should "return the second list when the first list is Nil" in {
    assert(shuffle(List.range(0, 9), Nil) == List.range(0, 9))
  }
  it should "return the shuffled list with two lists of the same length" in {
    val first = List.range(0, 10, 2)
    val second = List.range(1, 10, 2)
    assert(shuffle(first, second) == List.range(0, 10))
  }
  it should "return the shuffled list with two list of the different lengths" in {
    val first = List.range(0, 8, 2)
    val second = List.range(1, 10, 2)
    assert(shuffle(first, second) == List.range(0, 10).filterNot(num => num == 8).sorted)
  }

  "split()" should "return two Nils with Nil" in {
    assert(split(Nil, 5) == List(Nil, Nil))
  }
  it should "return the same list and Nil if n is greater than or equal to the length of the list" in {
    val list = List.range(0, 9)
    assert(split(list, list.length) == List(list, Nil))
    assert(split(list, list.length + 1) == List(list, Nil))
    assert(split(list, list.length + 3) == List(list, Nil))
  }
  it should "return Nil and the same list if n <= 0" in {
    val list = List.range(3, 15)
    assert(split(list, 0) == List(Nil, list))
    assert(split(list, -1) == List(Nil, list))
    assert(split(list, -4) == List(Nil, list))
  }
  it should "return [[1, 2, 3], [4, 5, 6, 7]] with list = [1..7] and n = 3" in {
    assert(split(List.range(1, 8), 3) == List(List.range(1, 4), List.range(4, 8)))
  }
  it should "return [[1], [2..7]] with list = [1..7] and n = 1" in {
    assert(split(List.range(1, 8), 1) == List(List(1), List.range(2, 8)))
  }
  it should "return [[1..6], [7]] with list = [1..7] and n = 6" in {
    assert(split(List.range(1, 8), 6) == List(List.range(1, 7), List(7)))
  }

  "outshuffle()" should "return a shuffled list where the first and the last elements are the same" in {
    val oldList = List.range(1, 11)
    val newList = outshuffle(oldList)
    assert(newList != oldList)
    assert(newList.sorted == oldList.sorted)
    assert(newList.head == oldList.head && newList.last == newList.last)
  }

  "inshuffle()" should "return a shuffled list where the first and the last elements are the second and the " +
    "second last elements in the returned list respectfully" in {
    val oldList = List.range(5, 21)
    val newList = inshuffle(oldList)
    assert(newList != oldList)
    assert(newList.sorted == oldList.sorted)
    assert(newList(1) == oldList.head && newList(newList.length - 2) == oldList.last)
  }

  "nshuffle()" should "return the same list if n <= 0" in {
    val list = List.range(0, 9)
    assert(nshuffle(outshuffle)(list, 0) == list)
    assert(nshuffle(inshuffle)(list, 0) == list)
    assert(nshuffle(inshuffle)(list, -5) == list)
  }

  it should "return the same list that the shuffle function returns if n == 1" in {
    val list = List.range(15, 30)
    assert(nshuffle(outshuffle)(list, 1) == outshuffle(list))
    assert(nshuffle(inshuffle)(list, 1) == inshuffle(list))
  }

  it should "return the same list that the shuffle function returns applied n times" in {
    val list = List.range(0, 10)
    val n = 5

    var l = list
    for (_ <- Seq.range(0, n)) {
      l = outshuffle(l)
    }
    assert(nshuffle(outshuffle)(list, n) == l)
  }

  "howManyShuffles()" should "return 8 with two same lists with 52 elements and outshuffle function" in {
    val list = List.range(0, 52)
    assert(howManyShuffles(outshuffle)(list, list) == 8)
  }
  it should "return 26 with the second list with 52 elements identical to the first list reversed and inshuffle function" in {
    val list = List.range(0, 52)
    assert(howManyShuffles(inshuffle)(list, list.reverse) == 26)
  }
}
