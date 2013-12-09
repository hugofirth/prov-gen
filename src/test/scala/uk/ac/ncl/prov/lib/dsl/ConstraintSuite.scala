package uk.ac.ncl.prov.lib.dsl

import org.scalatest.FunSuite
import org.scalatest.matchers._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ConstraintSuite extends FunSuite with ShouldMatchers{

  test("a non-valid constraint string should throw an exception when parsed") {
    evaluating { ConstraintDSL.parseDSL("foo bar baz biz") } should produce [Exception]
    evaluating { ConstraintDSL.parseDSL("an(Agent).when('requirement').unless('requirement')") } should produce [Exception]
    evaluating { ConstraintDSL.parseDSL("an(Entity).unless('requirement').may('requirement')") } should produce [Exception]
  }

  test("a valid constraint string should parse without throwing an exception") {
    evaluating { ConstraintDSL.parseDSL("an(Agent).must('requirement').unless('requirement')") }
    evaluating { ConstraintDSL.parseDSL("the(Entity, e1).may('requirement').when('requirement')") }
  }

  //Test Determiner
  test("a valid constraint string should correctly identify an invariable determiner") {
    val result = ConstraintDSL.parseDSL("the(Entity, e1).may('requirement').when('requirement')");
    result.determiner.invariable should equal(true)
    result.determiner.identifier should (not be null and equal("e1"))
  }

  //Test Imperative


  //Test Condition




}
