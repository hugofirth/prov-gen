package uk.ac.ncl.prov.lib.dsl

import prov._, degree._

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 12/12/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */

//TODO: Reafactor and improve very brittle implementation of operators, should be ok because of Cypher dynamic params, but still not ideal ...

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

  def most_=(max: Int) = {
    if(max >= 0){
      this.max = max
      this.op = " <="+max
    } else {
      throw new IllegalArgumentException("Requirement stipulations may not be for less than 0")
    }
  }

  def least_=(min: Int) = {
    if(min >= 0){
      this.min = min
      this.op = " >="+min
    } else {
      throw new IllegalArgumentException("Requirement stipulations may not be for less than 0")
    }
  }

  def range_=(range: (Int,Int)) = {
    if(range._1>=0 &&  range._2>=0 && range._1<range._2){
      this.min = range._1
      this.max = range._2
      this.op = " IN range("+range._1+","+range._2+")"
    } else {
      throw new IllegalArgumentException("Requirement stipulations may not be for less than 0")
    }
  }

  def exact_=(n: Int) = {
    if(n>=0){
      this.n = n
      this.op = " ="+n
    } else {
      throw new IllegalArgumentException("Requirement stipulations may not be for less than 0")
    }
  }

  def toCypherQL: String
}

class DegreeRequirement(n: Int = 0, val preposition: Preposition = Preposition.TOTAL) extends Requirement {

  def toCypherQL: String = "n."+preposition.toString+"Degree"+this.operation

}

class RelationshipRequirement(val relation: Relation) extends Requirement {

  def toCypherQL: String = "(n)-[:"+relation.toString+"]-()"

}