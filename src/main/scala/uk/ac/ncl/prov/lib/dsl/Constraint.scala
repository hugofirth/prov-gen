package uk.ac.ncl.prov.lib.dsl

import PROV._, Type._, Relation._
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

sealed abstract class Requirement {}
//TODO: Worlk out how to actually structure the requirements. Own class or object ?
case class Determiner(provType: Type, invariable: Boolean = false, identifier: String = "")
case class Imperative(necessary: Boolean, requirement: Requirement)
case class Condition[T](check: (T) => Boolean)