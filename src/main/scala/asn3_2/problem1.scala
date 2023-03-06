package asn3_2

object problem1 {
  def unfold[A, S](z: S)(f: S => Option[(A, S)]): LazyList[A] = f(z) match {
    case Some((h, s)) => h #:: unfold(s)(f)
    case None => LazyList()
  }

  /**
   * Create a list of all pythagorean triples whose components are at most a given limit n.
   * For example, pyth(10) returns [(3, 4, 5), (4, 3, 5), (6, 8, 10), (8, 6, 10)].
   * A triple (x, y, z) of positive integers is pythagorean if x&#94;2 + y&#94;2 = z&#94;2.
   * @param n The limit
   * @return The created list
   */
  def pyth(n: Int): List[(Int, Int, Int)] = {
    val range = unfold(1) {
      case i if i <= n => Some(i, i + 1)
      case _ => None
    }

    val triples = for {
      x <- range
      y <- range
      z <- range
    } yield (x, y, z)

    triples.filter(x => x._3 <= 10 && math.pow(x._1, 2) + math.pow(x._2, 2) == math.pow(x._3, 2)).toList
  }
}
