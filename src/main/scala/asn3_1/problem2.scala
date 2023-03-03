package asn3_1

object problem2 {

  trait Partial[+A,+B]

  case class Errors[+A](get: Seq[A]) extends Partial[A, Nothing]

  case class Success[+B](get: B) extends Partial[Nothing, B]

  object Partial {

    /**
     * The given function is applied if p is a Success
     * @param p The partial
     * @param f The function to apply
     * @tparam A Type for Errors
     * @tparam B Type for Success
     * @tparam C Type of the function's evaluated value
     * @return The result
     */
    def map[A, B, C](p: Partial[A, B])(f: B => C): Partial[A, C] = p match {
      case Errors(es) => Errors(es)
      case Success(b) => Success(f(b))
    }

    /**
     * Binds the given function if p is Success.
     * @param p The partial
     * @param f The function to apply
     * @tparam A Type for Errors
     * @tparam B Type for Success
     * @tparam C Type of the function's evaluated value
     * @return The result
     */
    def flatMap[A, B, C](p: Partial[A, B])(f: B => Partial[A, C]): Partial[A, C] = p match {
      case Errors(es) => Errors(es)
      case Success(b) => f(b)
    }

    /**
     * Returns the value of Success or default if p is an Errors.
     * @param p The partial
     * @param default The value to be returned when p is an Errors
     * @tparam A Type for Errors
     * @tparam B Type for Success
     * @return Either the value of Success of default
     */
    def getOrElse[A, B](p: Partial[A, B])(default: => B): B = p match {
      case Errors(_) => default
      case Success(b) => b
    }

    /**
     * Returns p or the c if p is an Errors.
     * @param p The partial
     * @param c The value to be returned when p is an Errors
     * @tparam A Type for Errors
     * @tparam B Type for Success
     * @return Either p or c
     */
    def orElse[A, B](p: Partial[A, B])(c: => Partial[A, B]): Partial[A, B] = p match {
      case Errors(es) => c match {
        case Errors(es2) => Errors(es ++ es2)
        case Success(b) => Success(b)
      }
      case Success(b) => Success(b)
    }

    /**
     * Lift the given function to take b and c as argument
     * @param b The first argument of the given function
     * @param c The second argument of the given function
     * @param f The function to lift
     * @tparam A Type for Errors
     * @tparam B Type for Success for b
     * @tparam C Type for Success for c
     * @tparam D Type for function's evaluated value
     * @return The returned value of the lifted function
     */
    def map2[A, B, C, D](b: Partial[A, B])(c: => Partial[A, C])(f: (B, C) => D): Partial[A, D] = b match {
      case Errors(es) => c match {
        case Errors(es2) => Errors(es ++ es2)
        case Success(_) => Errors(es)
      }
      case Success(bb) => map(c)(cc => f(bb, cc))
    }

    /**
     * Try to apply the given function to each element in ps
     * @param ps The partials
     * @param f The function
     * @tparam A Type for Errors
     * @tparam B Type for Success of ps
     * @tparam C Type for the function's evaluated value
     * @return The result
     */
    def traverse[A, B, C](ps: Seq[Partial[A, B]])(f: B => Partial[A, C]): Partial[A, Seq[C]] = ps match {
      case Nil => Success(Nil)
      case h :: t => map2(h match {
        case Errors(es) => Errors(es)
        case Success(hh) => f(hh)
      })(traverse(t)(f))(_ +: _)
    }

    /**
     * Try b and wrap with Partial
     * @param b The expression to try to execute
     * @tparam B Type for the return type of b
     * @return The result
     */
    def Try[B](b: => B): Partial[Exception, B] = {
      try Success(b)
      catch {
        case e: Exception => Errors(List(e))
      }
    }
  }
}
