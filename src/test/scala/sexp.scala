import net.pushl.elrpc._
import org.scalatest._

class SExp extends FlatSpec with Matchers {
  "toString('a)" should " -> a" in {
    SExpFunctions.toString(SSymbol('a)) should be ("a")
  }
  "toString(1)" should " -> 1" in {
    SExpFunctions.toString(SInteger(1)) should be ("1")
  }
  "toString(1.1)" should " -> 1.1" in {
    SExpFunctions.toString(SFloat(1.1)) should be ("1.1")
  }
  "toString((1 2 3))" should " -> (1 2 3)" in {
    SExpFunctions.toString(SList(List(SInteger(1), SInteger(2), SInteger(3)))) should be ("(1 2 3)")
  }
  "toString((1 (3.14)))" should " -> (1 (3.14))" in {
    SExpFunctions.toString(SList(List(SInteger(1), SList(List(SFloat(3.14)))))) should be ("(1 (3.14))")
  }
  "toString((1 . 2) (3 . 4))" should " -> ((1 . 2) (3 . 4))" in {
    SExpFunctions.toString(SAList(Map(SInteger(1)->SInteger(2), SInteger(3) -> SInteger(4)))) should be ("((1 . 2) (3 . 4))")
  }

  "fromString(a)" should " -> 'a" in {
    SExpFunctions.fromString("a").get should be (SSymbol('a))
  }
  "fromString(1)" should " -> 1" in {
    SExpFunctions.fromString("1").get should be (SInteger(1))
  }
  "fromString(1.1)" should " -> 1.1" in {
    SExpFunctions.fromString("1.1").get should be (SFloat(1.1))
  }
  "fromString((1 2 3))" should " -> (1 2 3)" in {
    SExpFunctions.fromString("(1 2 3)").get should be (SList(List(SInteger(1), SInteger(2), SInteger(3))))
  }
  "fromString((1 (3.14)))" should " -> (1 (3.14))" in {
    SExpFunctions.fromString("(1 (3.14))").get should be (SList(List(SInteger(1), SList(List(SFloat(3.14))))))
  }
  "fromString((1 . 2) (3 . 4))" should " -> ((1 . 2) (3 . 4))" in {
    SExpFunctions.fromString("((1 . 2) (3 . 4))").get should be (SAList(Map(SInteger(1)->SInteger(2), SInteger(3) -> SInteger(4))))
  }
}
