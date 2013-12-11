package uk.ac.ncl.prov.lib.dsl

import prov._, degree._



/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 03/12/2013
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
class Constraint(val determiner: Determiner, val imperative: Imperative, val condition: Condition) {

  //TODO: Create a method for creating a Cypher query from the above - toCypherQL or some such

}

//TODO: Fill out Class and Method stubs for all below - placing in their own source files.
class Requirement() {
  def atMost(max: Int): Requirement = { this }
  def atLeast(min: Int): Requirement = { this }
  def between(min: Int, max: Int): Requirement = { this }
  def exactly(n: Int): Requirement = { this }
}
case class DegreeRequirement(degree: Int = 0, preposition: Preposition = Preposition.TOTAL) extends Requirement
case class RelationshipRequirement(relation: Relation) extends Requirement
case class Determiner(provType: Type, invariable: Boolean = false, identifier: String = "")
case class Imperative(requirement: Requirement, necessary: Boolean = true)
case class Condition(requirement: Requirement, exception: Boolean = true)
