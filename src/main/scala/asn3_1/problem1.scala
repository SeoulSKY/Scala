package asn3_1

object problem1 {

  /**
   * person -> (gender, parent1, parent2) according to the following link: https://www.bbc.com/news/uk-23272491
   */
  val loyalFamily: Map[String, (String, String, String)] = Map(
    "George" -> ("m", "William", "Catherine"),
    "Charlotte" -> ("f", "William", "Catherine"),
    "Louis" -> ("m", "William", "Catherine"),
    "Archie" -> ("m", "Harry", "Meghan"),
    "Lilibet" -> ("f", "Harry", "Meghan"),
    "Savannah" -> ("f", "Autumn", "Peter"),
    "Isla" -> ("f", "Autumn", "Peter"),
    "Mia" -> ("f", "Zara", "Mike"),
    "Lena" -> ("f", "Zara", "Mike"),
    "Lucas" -> ("m", "Zara", "Mike"),
    "Sienna" -> ("f", "Beatrice", "Edoardo"),
    "August" -> ("m", "Eugenie", "Jack"),
    "Beatrice" -> ("f", "Andrew", "Sarah"),
    "Eugenie" -> ("f", "Andrew", "Sarah"),
    "Louise" -> ("f", "Edward", "Sophie"),
    "James" -> ("m", "Edward", "Sophie"),
    "Peter" -> ("m", "Mark", "Anne"),
    "Zara" -> ("f", "Mark", "Anne"),
    "William" -> ("m", "Diana", "Charles"),
    "Harry" -> ("m", "Diana", "Charles"),
    "Charles" -> ("m", "Elizabeth", "Philip"),
    "Anne" -> ("f", "Elizabeth", "Philip"),
    "Andrew" -> ("m", "Elizabeth", "Philip"),
    "Edward" -> ("m", "Elizabeth", "Philip"),
    "Elizabeth" -> ("f", "", ""),
    "Philip" -> ("m", "", ""),
    "Diana" -> ("f", "", ""),
    "Mark" -> ("m", "", ""),
    "Sophie" -> ("f", "", ""),
    "Sarah" -> ("f", "", ""),
    "Mike" -> ("m", "", ""),
    "Autumn" -> ("f", "", ""),
    "Meghan" -> ("f", "", ""),
    "Catherine" -> ("f", "", ""),
    "Timothy" -> ("m", "", ""),
    "Jack" -> ("m", "", ""),
    "Camilla" -> ("f", "", ""),
    "Edoardo" -> ("m", "", "")
  )

  /**
   * Find all children of given parents. The order of given parents doesn't affect anything
   * @param parent1 The first parent of the children
   * @param parent2 The second parent of the children
   * @return List of children of the couple, or None if the given couple is not found
   */
  def children(parent1: String, parent2: String): Option[List[String]] = {
    val children = loyalFamily.map(x => (x._1, (x._2._2, x._2._3))) // remove genders
      .filter(x => x._2 == (parent1, parent2) || x._2 == (parent2, parent1))
      .keys

    if (children.isEmpty) None else Some(children.toList)
  }

  /**
   * Find the parents of the given person
   * @param p The person to find their parents
   * @return List of parents, or None if the person is not found
   */
  def parents(p: String): Option[List[String]] = {
    loyalFamily.find(x => x._1 == p)
      .map(x => List(x._2._2, x._2._3)
        .filter(_.nonEmpty))
  }

  /**
   * Find the grandparents of the given person
   * @param p The person to find their grandparents
   * @return List of grandparents, or None if the person is not found
   */
  def grandparents(p: String): Option[List[String]] = {
    parents(p)
      .map(_
        .flatMap(parent => parents(parent).get)
    )
  }

  /**
   * Find the sisters of the given person
   * @param p The person to find their sisters
   * @return List of sisters, or None if the person is not found
   */
  def sisters(p: String): Option[List[String]] = {
    parents(p)
      .map(ps =>
        if (ps.isEmpty) Nil else children(ps.head, ps.last).get
          .filter(child => child != p && loyalFamily.find(childInfo => childInfo._1 == child).get._2._1 == "f")
      )
  }

  /**
   * Find the first cousins of the given person
   * @param p The person to find their first cousins
   * @return List of first cousins, or None if the person is not found
   */
  def firstCousins(p: String): Option[List[String]] = {
    grandparents(p)
      .map(gps =>
        if (gps.isEmpty) Nil else loyalFamily.filter(x => parents(x._1) != parents(p) && grandparents(x._1).get == gps)
          .keys.toList
      )
  }

  /**
   * Find the uncles of the given person
   * @param p The person to find their uncles
   * @return List of uncles, or None if the person is not found
   */
  def uncles(p: String): Option[List[String]] = {
    firstCousins(p)
      .map(_
        .flatMap(cousin => parents(cousin).get)
        .filter(parent =>
          loyalFamily.find(parentInfo => parent == parentInfo._1).get._2._1 == "m")
        .distinct
      )
  }
}
