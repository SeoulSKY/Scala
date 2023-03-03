package asn2

import asn2.problem2.*
import asn2.problem2.Tree.*
import org.scalatest.flatspec.AnyFlatSpec

class problem2Test extends AnyFlatSpec {
  val expressionTree: Tree[Char] = Node(Leaf('3'), '+', Node(Node(Leaf('5'), '+', Leaf('9')), '*', Leaf('2')))

  "inOrder()" should "return a sorted list with a binary search tree" in {
    val t =                   Node(
      Node(Leaf(0), 1, Leaf(2)), 3, Node(Leaf(4), 5, Leaf(6))
    )

    assert(inOrder(t) == List.range(0, 7))
  }

  "preOrder()" should "return prefix expressions with an expression tree" in {
    assert(preOrder(expressionTree) == List('+', '3', '*', '+', '5', '9', '2'))
  }

  "postOrder()" should "return postfix expressions with an expression tree" in {
    assert(postOrder(expressionTree) == List('3', '5', '9', '+', '2', '*', '+'))
  }

  "search()" should "return true when the key is in the tree" in {
    assert(search(expressionTree, '9'))
  }

  it should "return false when the key is not in the tree" in {
    assert(!search(expressionTree, '\n'))
  }

  "replace()" should "return the same tree if 'before' is not in the tree" in {
    assert(replace(expressionTree, '\n', 'l') == expressionTree)
  }

  it should "return a tree with all instances of before replaced" in {
    val before = 6
    val after = 9

    val t = Node(
      Node(Leaf(0), before, Leaf(2)), 3, Node(Leaf(4), 5, Leaf(before))
    )
    val newT = Node(
      Node(Leaf(0), after, Leaf(2)), 3, Node(Leaf(4), 5, Leaf(after))
    )

    assert(replace(t, before, after) == newT)
  }

}
