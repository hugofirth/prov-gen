package uk.ac.ncl.prov.lib.constraint

import org.scalatest.FunSuite
import org.scalatest.matchers._
import uk.ac.ncl.prov.lib.graph.util.DegreePreposition

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import uk.ac.ncl.prov.lib.prov.Relation

@RunWith(classOf[JUnitRunner])
class ConstraintSuite extends FunSuite with ShouldMatchers{

  /**
   * ========================================================================
   * Test cases to ensure correct error handling in Constraint rule parsing.
   * ========================================================================
   */

  test("a non-valid constraint string should throw an exception when parsed") {
    //TODO: Add some more incorrect test cases here - also add cases to check capitalisation
    an [IllegalArgumentException] should be thrownBy { Constraint("foo bar baz biz") }
    an [IllegalArgumentException] should be thrownBy { Constraint("an(Agent).when(it.has(relationship(ActedOnBehalfOf).exactly(1)).unless(have(in.degree().at.most(3)))") }
    an [IllegalArgumentException] should be thrownBy { Constraint("an(Entity).unless(\"requirement\").may(\"requirement\")") }
  }

  /**
   * ==================================================================================================
   * Test cases to ensure correct identification of effected Nodes through Constraint rule determiner.
   * ==================================================================================================
   */

  test("a valid constraint string should correctly identify an invariable determiner") {
    val result = Constraint("the Entity(e1) must have relationship \"WasGeneratedBy\" when e1 has relationship \"WasAttributedTo\";")
    result.determiner.invariable should equal(true)
    result.determiner.identifier should (not be null and equal("e1"))
  }

  test("a valid constraint string should correctly identify a variable determiner") {
    val result = Constraint("an Entity must have relationship \"WasGeneratedBy\" when it has relationship \"WasAttributedTo\";")
    result.determiner.invariable should equal(false)
    result.determiner.identifier should equal("it")
  }

  /**
   * ====================================================================================================================
   * Test cases to ensure correct handling of Requirements in both Imperative and Condition clauses of Contraint rules.
   * ====================================================================================================================
   */

  test("a valid constraint string should correctly parse and determine a required node relationship") {
    val result = Constraint("an Entity must have relationship \"WasGeneratedBy\" exactly 1 times").imperative.requirement
    result match {
      case r: RelationshipRequirement => r.relation should equal(Relation.WASGENERATEDBY)
      case _ => fail("The rule, when parsed, does not produce a Relationship requirement.")
    }
  }

  test("a valid constraint string should correctly parse and determine a required node property") {
    val result = Constraint("an Activity must have property (\"prov:type\"=\"create\") with probability 0.01").imperative.requirement
    result match {
      case r: PropertyRequirement => 
        r.propertyKey should equal("prov:type")
        r.propertyValue should equal("create") 
      case _ => fail("The rule, when parsed, does not produce a Property requirement.")
    }
  }

  test("a valid constraint string should correctly parse and determine a node degree requirement's preposition, operation and value") {
    val result = Constraint("an Entity must have out degree at most 2").imperative.requirement
    result match {
      case r: DegreeRequirement => {
        r.preposition should equal(DegreePreposition.OUT)
        r.operation match {
          case Some(op: Most) => op.check(2) shouldBe 0; op.check(5) shouldBe 1
          case _ => fail("The rule, when parsed, does not produce a Degree requirement containing the correct [at Most] operation.")
        }
      }
      case _ => fail("The rule, when parsed, does not produce a Degree requirement.")
    }

  }


//  test("a valid constraint string should correctly parse a numerical modifier to a relationship requirement") {
//
//  }
//
//  test("a valid constraint string should correctly parse a determiner modifier to a relationship requirement") {
//
//  }
//
//  test("a valid constraint string should correctly parse a probability modifier to a relationship requirement") {
//
//  }
//
//  test("a valid constraint string should correctly parse a property requirement modifier") {
//
//  }



}
