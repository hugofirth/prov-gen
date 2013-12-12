package uk.ac.ncl.prov.lib.dsl

import org.scalatest.FunSuite
import org.scalatest.matchers._
import prov._, degree._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ConstraintSuite extends FunSuite with ShouldMatchers{

  test("a non-valid constraint string should throw an exception when parsed") {
    evaluating { ConstraintDSL.parseDSL("foo bar baz biz") } should produce [Exception]
    evaluating { ConstraintDSL.parseDSL("an(Agent).when(it.has(relationship(ActedOnBehalfOf).exactly(1)).unless(have(in.degree().at.most(3)))") } should produce [Exception]
    evaluating { ConstraintDSL.parseDSL("an(Entity).unless('requirement').may('requirement')") } should produce [Exception]
  }

  test("a valid constraint string should parse without throwing an exception") {
    evaluating { ConstraintDSL.parseDSL("an(Agent).must(have(in.degree().at.most(3))).unless(it.has(relationship(ActedOnBehalfOf).exactly(1)))") }
    evaluating { ConstraintDSL.parseDSL("the(Entity, e1).may(have(relationship(WasGeneratedBy).at.least(1))).when(it.has(relationship(WasAttributedTo).at.least(1)))") }
  }

  //Test Determiner
  test("a valid constraint string should correctly identify an invariable determiner") {
    val result = ConstraintDSL.parseDSL("the(Entity, e1).may(have(relationship(WasGeneratedBy).at.least(1))).when(it.has(relationship(WasAttributedTo).at.least(1)))")
    result.determiner.invariable should equal(true)
    result.determiner.identifier should (not be null and equal("e1"))
  }

  //Test Imperative
  test("a valid constraint string should correctly parse and determine an imperative") {
    val result = ConstraintDSL.parseDSL("the(Entity, e1).may(have(relationship(WasGeneratedBy).at.least(1))).when(it.has(in.degree().at.most(1)))")
    result.imperative.necessary should equal(false)
    result.imperative.requirement.isInstanceOf[RelationshipRequirement] should equal(true)
    result.imperative.requirement.asInstanceOf[RelationshipRequirement].relation should equal(Relation.WASGENERATEDBY)
  }

  //Test Condition




}
