package uk.ac.ncl.prov.lib.constraint

import org.scalatest.FunSuite
import org.scalatest.matchers._
import prov._, degree._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import uk.ac.ncl.prov.lib.prov_dm.Relation

@RunWith(classOf[JUnitRunner])
class ConstraintSuite extends FunSuite with ShouldMatchers{

  test("a non-valid constraint string should throw an exception when parsed") {
    evaluating { Constraint("foo bar baz biz") } should produce [Exception]
    evaluating { Constraint("an(Agent).when(it.has(relationship(ActedOnBehalfOf).exactly(1)).unless(have(in.degree().at.most(3)))") } should produce [Exception]
    evaluating { Constraint("an(Entity).unless(\"requirement\").may(\"requirement\")") } should produce [Exception]
  }

  test("a valid constraint string should parse without throwing an exception") {
    evaluating { Constraint("an Agent has in degree at most 3 unless it has relationship \"ActedOnBehalfOf\" exactly 1;") }
    evaluating { Constraint("the Entity, e1, has relationship \"WasGeneratedBy\" at least 1 when it has relationship \"WasAttributedTo\"  at least 1;") }
  }

  //Test Determiner
  test("a valid constraint string should correctly identify an invariable determiner") {
    val result = Constraint("the Entity, e1, has relationship \"WasGeneratedBy\" at least 1 when it has relationship \"WasAttributedTo\"  at least 1;")
    result.determiner.invariable should equal(true)
    result.determiner.identifier should (not be null and equal("e1"))
  }

  //Test Imperative
  test("a valid constraint string should correctly parse and determine an imperative") {
    val result = Constraint("the Entity, e1, has relationship \"WasGeneratedBy\" at least 1 when it has relationship \"WasAttributedTo\"  at least 1;")
    result.imperative.requirement.isInstanceOf[RelationshipRequirement] should equal(true)
    result.imperative.requirement.asInstanceOf[RelationshipRequirement].relation should equal(Relation.WASGENERATEDBY)
    result.imperative.requirement.toCypherQL should equal("(n)-[:WasGeneratedBy]-()")
  }

  //Test Condition
  test("a valid constraint string should correctly parse and determine a condition") {
    val result = Constraint("the Entity, e1, has relationship \"WasGeneratedBy\" at least 1 when it has in degree at most 1;")
    result.condition.when should equal(true)
    result.condition.requirement.isInstanceOf[DegreeRequirement] should equal(true)
    result.condition.requirement.asInstanceOf[DegreeRequirement].preposition should equal(Preposition.IN)
    result.condition.requirement.toCypherQL should equal("n.inDegree <=1")
  }

}
