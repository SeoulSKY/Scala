package asn2

import scala.annotation.tailrec

object problem2 {

  sealed trait Tree[A]

  case class Node[A](left: Tree[A], value: A, right: Tree[A]) extends Tree[A]
  case class Leaf[A](value: A) extends Tree[A]

  object Tree {

    /**
     * Traverse the tree in in-order
     * @param t The tree to traverse
     * @tparam A The type of the elements in the tree
     * @return The list of elements found in the tree traversing in in-order
     */
    def inOrder[A](t: Tree[A]): List[A] = t match {
      case Leaf(value) => List(value)
      case Node(left, value, right) => inOrder(left) ++ List(value) ++ inOrder(right)
    }

    /**
     * Traverse the tree in pre-order
     *
     * @param t The tree to traverse
     * @tparam A The type of the elements in the tree
     * @return The list of elements found in the tree traversing in pre-order
     */
    def preOrder[A](t: Tree[A]): List[A] = t match {
      case Leaf(value) => List(value)
      case Node(left, value, right) => List(value) ++ preOrder(left) ++ preOrder(right)
    }

    /**
     * Traverse the tree in post-order
     *
     * @param t The tree to traverse
     * @tparam A The type of the elements in the tree
     * @return The list of elements found in the tree traversing in post-order
     */
    def postOrder[A](t: Tree[A]): List[A] = t match {
      case Leaf(value) => List(value)
      case Node(left, value, right) => postOrder(left) ++ postOrder(right) ++ List(value)
    }

    /**
     * Search the key in the tree
     * @param t The tree to search
     * @param key The value to find in the tree
     * @tparam A The type of the elements in the tree
     * @return True if the key is found in the tree, false otherwise
     */
    def search[A](t: Tree[A], key: A): Boolean = t match {
      case Leaf(value) => value == key
      case Node(left, value, right) => value == key || search(left, key) || search(right, key)
    }

    /**
     * Create a new tree with values 'before' replaced with 'after'
     * @param t The original tree
     * @param before The value to replace
     * @param after The new value to assign
     * @tparam A The type of the elements in the tree
     * @return A new tree with new values
     */
    def replace[A](t: Tree[A], before: A, after: A): Tree[A] = t match {
      case Leaf(value) => if (value == before) Leaf(after) else t
      case Node(left, value, right) => 
        Node(replace(left, before, after), if (value == before) after else value, replace(right, before, after))
    }
  }
}
