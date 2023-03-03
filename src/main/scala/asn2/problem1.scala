package asn2

import scala.annotation.tailrec

object problem1 {

  def main(args: Array[String]): Unit = {
    val list = List.range(0, 52)
    println("How many out-shuffles are required to return a stack of 52 cards to its original ordering?")
    println(howManyShuffles(outshuffle)(list, list))

    println("How many in-shuffles are required to completely reverse a stack of 52 cards?")
    println(howManyShuffles(inshuffle)(list, list.reverse))
  }

  /**
   * Create a new list shuffling the given two lists
   * so that an element from one sub-stack is followed by one from the other, and so on
   * @param l1 The first list
   * @param l2 The second list
   * @tparam A The type of the elements in those lists
   * @return The new list created by Faro shuffle
   */
  def shuffle[A](l1: List[A], l2: List[A]): List[A] = {

    @tailrec
    def go(l1: List[A], l2: List[A], i: Int, acc: List[A]): List[A] = {
      if (l1.isEmpty) return acc ++ l2
      else if (l2.isEmpty) return acc ++ l1

      if (i % 2 == 0) {
        go(l1.tail, l2, i + 1, acc.appended(l1.head))
      } else {
        go(l1, l2.tail, i + 1, acc.appended(l2.head))
      }
    }

    go(l1, l2, 0, List())
  }

  /**
   * Split the contents of the provided list into two lists, the first with the first n elements of the list (in order),
   * and the second with the remaining elements (in order)
   * @param list The list to split
   * @param n The splitting point
   * @tparam A The type of the elements in the list
   * @return A list that contains two split list
   */
  def split[A](list: List[A], n: Int): List[List[A]] = {

    @tailrec
    def go(list: List[A], i: Int, acc: List[List[A]]): List[List[A]] = {
      if (list.isEmpty) return acc

      if (i <= n) {
        go(list.tail, i + 1, List(acc.head.appended(list.head), acc(1)))
      } else {
        go(list.tail, i + 1, List(acc.head, acc(1).appended(list.head)))
      }
    }

    go(list, 1, List(List(), List()))
  }

  /**
   * Perform a perfect Faro shuffle where the top and the bottom cards of the stack remains the same
   * @tparam A The type of the elements in the list
   * @return A list after shuffling
   */
  def outshuffle[A](list: List[A]): List[A] = {
    val List(list1, list2) = split(list, list.length / 2)
    shuffle(list1, list2)
  }

  /**
   * Perform a perfect Faro shuffle where the top and the bottom cards of the stack becomes the second and
   * the second last cards of the shuffled stack respectfully
   * @tparam A The type of the elements in the list
   * @return A list after shuffling
   */
  def inshuffle[A](list: List[A]): List[A] = {
    val List(list1, list2) = split(list, list.length / 2)
    shuffle(list2, list1)
  }

  /**
   * Shuffle the list n times
   * @param f The function that shuffles a list
   * @param list The list to shuffle
   * @param n The number of times to shuffle
   * @tparam A The type of the elements in the list
   * @return The shuffled list
   */
  def nshuffle[A](f: List[A] => List[A]) (list: List[A], n: Int): List[A] = {

    @tailrec
    def go(i: Int, acc: List[A]): List[A] = {
      if (i >= n) acc
      else go(i + 1, f(acc))
    }

    go(0, list)
  }

  /**
   * Calculate the number of shuffling required to make the first list identical to the second list
   * @param f A function that shuffles a list
   * @param list1 The list to shuffle
   * @param list2 The target list
   * @tparam A The type of the elements in the list
   * @return The count
   */
  def howManyShuffles[A](f: List[A] => List[A])(list1: List[A], list2: List[A]): Int = {

    @tailrec
    def go(list1: List[A], i: Int): Int = {
      if (i != 0 && list1 == list2) i
      else go(f(list1), i + 1)
    }

    go(list1, 0)
  }
}