package uk.ac.ncl.prov.lib.constraint


import uk.ac.ncl.prov.lib.prov.Relation
import uk.ac.ncl.prov.lib.graph.util.DegreePreposition
import uk.ac.ncl.prov.lib.statistical._
import scala.collection.JavaConverters._

import uk.ac.ncl.prov.lib.graph.vertex.Vertex
import uk.ac.ncl.prov.lib.graph.edge.Edge


/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 12/12/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
sealed abstract class Requirement {

  private var p: Double = 1.0
  private var op: Operator = _
  private var dist: Option[Distribution] = None

  def probability = this.p
  def operation = this.op
  def distribution = this.dist

  def atMost(max: Int) = {
    if(max > 0){
      this.op = Most(max)
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def atLeast(min: Int) = {
    if(min > 0){
      this.op = Least(min)
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def inRange(range: (Int,Int)) = {
    if(range._1>0 &&  range._2>0 && range._1<range._2){
      this.op = Between(range._1, range._2)
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def exactly(n: Int) = {
    if(n>0){
      this.op = Exact(n)
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

  //TODO: remove unneeded dependency on Java Enum completely (rather than this halfway hodgepodge)
  def distribution(d: DistributionType, params: List[String]) = d match {
    case DistributionType.BINOMIAL => this.dist = Some(Binomial(params))
    case DistributionType.GAMMA => this.dist = Some(Gamma(params))
    case DistributionType.HYPERGEOMETRIC => this.dist = Some(HyperGeometric(params))
    case DistributionType.PASCAL => this.dist = Some(Pascal(params))
    case DistributionType.POISSON => this.dist = Some(Poisson(params))
    case DistributionType.ZIPF => this.dist = Some(Zipf(params))
  }

  protected def check(v: Vertex): Boolean

  def check(key: String): Boolean = Requirement.determiners.get(key).get.exists(v => check(v))
}

object Requirement {
  private var determinedVertices: scala.collection.mutable.Map[String, Set[Vertex]] = _
  def determiners(vertexSetMap: scala.collection.mutable.Map[String, Set[Vertex]]) = this.determinedVertices = vertexSetMap
  def determiners = this.determinedVertices
}

case class DegreeRequirement(preposition: DegreePreposition = DegreePreposition.TOTAL) extends Requirement {

  protected def check(v: Vertex): Boolean = preposition match {
    case DegreePreposition.IN => this.operation.check(v.getInEdges.size())
    case DegreePreposition.OUT => this.operation.check(v.getOutEdges.size())
    case DegreePreposition.TOTAL => this.operation.check(v.getEdges.size())
  }

}

case class RelationshipRequirement(relation: Relation) extends Requirement {

  private var mustBeRelatedTo: Option[Determiner] = None

  def relatedTo = this.mustBeRelatedTo

  def relatedTo(d: Determiner) = {
    this.mustBeRelatedTo = Some(d)
  }

  protected def check(v: Vertex): Boolean = {
    val edgeSet = v.getEdgesWithLabels(relation).asScala
    if (mustBeRelatedTo.isDefined && mustBeRelatedTo.get.invariable)
    {
      val vertexSet = edgeSet.map(e => e.other(v))
      Requirement.determiners.put(mustBeRelatedTo.get.identifier, vertexSet.toSet)
      edgeSet.exists(e => e.other(v).getLabel.equals(mustBeRelatedTo.get.provType))
    }
    else if(mustBeRelatedTo.isDefined)
    {
      edgeSet.exists(e => e.other(v).getLabel.equals(mustBeRelatedTo.get.provType))
    }
    else
    {
      edgeSet.nonEmpty
    }
  }

}

case class PropertyRequirement(propertyKey: String, propertyValue: Any) extends Requirement {
  protected def check(v: Vertex): Boolean = {
    if(v.hasProperty(propertyKey)) v.getProperty(propertyKey).equals(propertyValue) else false
  }
}