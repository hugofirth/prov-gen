package uk.ac.ncl.prov.lib.constraint


import uk.ac.ncl.prov.lib.prov_dm.{Type, Relation}
import uk.ac.ncl.prov.lib.graph.vertex.Vertex.DegreePreposition


/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 12/12/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */

//TODO: Refactor and improve very brittle implementation of operators, should be ok because of Cypher dynamic params, but still not ideal ...
//TODO: Refactor out very close coupling with Cypher. Enums for operators?

sealed abstract class Requirement {

  private var max: Int = 0
  private var min: Int = 0
  private var n: Int = 0
  private var p: Double = 1.0
  private var op: String = ""

  def most = this.max
  def least = this.min
  def exact = this.n
  def probability = this.p
  def range = (this.min, this.max)
  def operation = op

  def most_=(max: Int) = {
    if(max > 0){
      this.max = max
      this.op = "<="+max
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def least_=(min: Int) = {
    if(min > 0){
      this.min = min
      this.op = ">="+min
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def range_=(range: (Int,Int)) = {
    if(range._1>0 &&  range._2>0 && range._1<range._2){
      this.min = range._1
      this.max = range._2
      this.op = "IN range("+range._1+","+range._2+")"
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def exact_=(n: Int) = {
    if(n>0){
      this.n = n
      this.op = "="+n
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def probability_=(p: Double) = {
    if(p>=0.0 && p<=1.0){
      this.p = p
    } else {
      throw new IllegalArgumentException("Requirement probability stipulations must be between 0 & 1")
    }
  }

  def toCypherQL: String
}

case class DegreeRequirement(preposition: DegreePreposition = DegreePreposition.TOTAL) extends Requirement {
  def toCypherQL: String = "n."+preposition.toString+"Degree"+" "+this.operation
}

case class RelationshipRequirement(relation: Relation) extends Requirement {

  private var mustRelatedTo: Option[Type] = None

  def related = this.mustRelatedTo

  def related_=(t: Type) = {
    this.mustRelatedTo = Some(t)
  }

  def toCypherQL: String = "(n)-[:"+relation.toString+"]-()"

}

case class PropertyRequirement(propertyKey: String, propertyValue: Any) extends Requirement {
  def toCypherQL = "n."+propertyKey+" = \""+propertyValue+"\""
}