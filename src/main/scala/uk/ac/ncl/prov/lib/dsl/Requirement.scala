package uk.ac.ncl.prov.lib.dsl

import prov._, degree._

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 12/12/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */

abstract class Requirement {

  private var max: Int = 0
  private var min: Int = 0
  private var n: Int = 0
  private var op: String = ""

  def most = this.max
  def least = this.min
  def exact = this.n
  def range = (this.min, this.max)
  def operation = op

  def atMost(max: Int):Boolean = {
    if(max >= 0){
      this.max = max
      this.op = "<="+max
      true
    } else {
      false
    }
  }

  def atLeast(min: Int):Boolean = {
    if(min >= 0){
      this.min = min
      this.op = ">="+min
      true
    } else {
      false
    }
  }

  def between(min: Int, max: Int):Boolean = {
    if(min>=0 && max >=0 && min<max){
      this.min = min
      this.max = max
      this.op = "" //TODO: Work out how to do this - op could be a function which returns a string but can take variable names?
      true
    } else {
      false
    }
  }

  def exactly(n: Int):Boolean = {
    if(n>=0){
      this.n = n
      this.op = "="+n
      true
    } else {
      false
    }
  }

  def toCypherQL: String
}
class DegreeRequirement(n: Int = 0, val preposition: Preposition = Preposition.TOTAL) extends Requirement {
  //TODO: Implement toCypherQL method for each requirement type
}

class RelationshipRequirement(relation: Relation) extends Requirement {

}