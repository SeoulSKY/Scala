package asn3_2

object problem2 {
  def unfold[A, S](z: S)(f: S => Option[(A, S)]): LazyList[A] = f(z) match {
    case Some((h, s)) => h #:: unfold(s)(f)
    case None => LazyList()
  }

  /**
   * Generate an infinite list of perfect numbers.
   * A positive integer is perfect if it equals the sum of all of its factors, excluding the number itself.
   * For example, 6, 28, 496, and so on.
   * @return the perfect numbers
   */
  def perfectNumbers(): LazyList[Int] = {
    unfold(1)(i => Some(i, i + 1)) // infinite positive integers
      .map(i => factors(i).reverse)
      .filter(fs => fs.head == fs.tail.sum)
      .map(fs => fs.head)
  }

  /**
   * Generate the factors of the given number
   * @param n the number
   * @return the factors
   */
  def factors(n: Int): LazyList[Int] = {
    unfold(1) {
      case i if i <= n => Some(i, i + 1)
      case _ => None
    }.filter(i => n % i == 0)
  }
}
