package asn2

import scala.annotation.tailrec

object problem3 {

  /**
   * Apply Luhn algorithm to the given digit
   * @param d The digit
   * @return The new digit created by Luhn algorithm
   */
  def luhnDouble(d: Int): Int = if (d > 4) d * 2 - 9 else d * 2

  /**
   * Apply the functions to the elements in order
   * @param fs The functions to apply
   * @param as the elements to map
   * @tparam A The type of the elements in the input list
   * @tparam B The type of the elements in the output list
   * @return The list containing the evaluated value of the functions with the digits
   */
  def altMap[A, B](fs: List[A => B])(as: List[A]): List[B] = {

    @tailrec
    def go(as: List[A], acc: List[B])(gs: List[A => B]): List[B] = {
      if (as.isEmpty) return acc

      val g = if (gs.isEmpty) fs.head else gs.head
      go(as.tail, acc.appended(g(as.head))) (if (gs.isEmpty) fs.tail else gs.tail)
    }

    go(as, Nil)(fs)
  }

  /**
   * Check if the given digits of the card is valid using luhn algorithm
   * @param as The digits of the card
   * @return true if the digits are valid, false otherwise
   */
  def luhn(as: List[Int]): Boolean = altMap(List((a: Int) => a, luhnDouble))(as.reverse).sum % 10 == 0
}
