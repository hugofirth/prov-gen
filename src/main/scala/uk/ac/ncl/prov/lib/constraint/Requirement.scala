package uk.ac.ncl.prov.lib.constraint


import uk.ac.ncl.prov.lib.prov.Relation
import uk.ac.ncl.prov.lib.graph.util.DegreePreposition
import uk.ac.ncl.prov.lib.statistical._
import scala.collection.JavaConverters._
import uk.ac.ncl.prov.lib.graph.vertex.Vertex
import uk.ac.ncl.prov.lib.util.State
import scala.util.Random


/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 12/12/2013
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
sealed abstract class Requirement {

  private var p: Option[Double] = None
  private var op: Option[Operator] = None
  private var dist: Distribution = _
  private val ident: String =  "__"+java.util.UUID.randomUUID().toString

  def probability = this.p
  def operation = this.op
  def distribution = this.dist

  def atMost(max: Int) = {
    if(max > 0){
      this.op = Some(Most(max))
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def atLeast(min: Int) = {
    if(min > 0){
      this.op = Some(Least(min))
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def inRange(range: (Int,Int)) = {
    if(range._1>0 &&  range._2>0 && range._1<range._2){
      this.op = Some(Between(range._1, range._2))
      this.dist = Uniform(List(range._1.toString, range._2.toString))
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def exactly(n: Int) = {
    if(n>0){
      this.op = Some(Exact(n))
    } else {
      throw new IllegalArgumentException("Requirement range stipulations may not be for 0 or less")
    }
  }

  def probability_=(p: Double) = {
    if(p>=0.0 && p<=1.0){
      this.p = Some(p)
    } else {
      throw new IllegalArgumentException("Requirement probability stipulations must be between 0 & 1")
    }
  }

  //TODO: remove unneeded dependency on Java Enum completely (rather than this halfway hodgepodge)
  def distribution(d: DistributionType, params: List[String]) = d match {
    case DistributionType.BINOMIAL => this.dist = Binomial(params)
    case DistributionType.GAMMA => this.dist = Gamma(params)
    case DistributionType.HYPERGEOMETRIC => this.dist = HyperGeometric(params)
    case DistributionType.PASCAL => this.dist = Pascal(params)
    case DistributionType.ZIPF => this.dist = Zipf(params)
  }

  protected def operationCheck(value: Int, vertex: Vertex) = this.op match {
    case Some(o: Between) => //Does this work? Yes apparently
      val rnd: Int = {
        if(vertex.hasProperty(ident))
        {
          vertex.getProperty(ident, classOf[java.lang.Integer])
        }
        else
        {
          vertex.setProperty(ident, this.distribution.getRand)
          vertex.getProperty(ident, classOf[java.lang.Integer])
        }
      }
      //TODO: work something out here other than exact
      Exact(rnd).check(value)
    case Some(o) => o.check(value)
    case _ => throw new IllegalArgumentException("Requirement operation should not be None!")
  }

  protected def check(v: Vertex): RequirementState

  //TODO: Keep an eye
  def check(key: String): RequirementState = {
    val checks: Set[RequirementState] = Requirement.determiners.get(key).get.map(v => check(v)).filter(v => v.shouldContinue)
    if(checks.nonEmpty)
    {
      checks.find(v => v.isSatisfied).getOrElse(RequirementState(satisfied = Some(false), continue = Some(true)))
    }
    else
    {
      RequirementState(satisfied = Some(false), continue = Some(false))
    }
  }
}

object Requirement {
  private var determinedVertices: scala.collection.mutable.Map[String, Set[Vertex]] = _
  def determiners(vertexSetMap: scala.collection.mutable.Map[String, Set[Vertex]]) = this.determinedVertices = vertexSetMap
  def determiners = this.determinedVertices
}

case class DegreeRequirement(preposition: DegreePreposition = DegreePreposition.TOTAL) extends Requirement {

  protected def check(v: Vertex): RequirementState = preposition match {
    case DegreePreposition.IN => RequirementState(state = Some(this.operationCheck(v.getInEdges.size(), v)))
    case DegreePreposition.OUT => RequirementState(state = Some(this.operationCheck(v.getOutEdges.size(), v)))
    case DegreePreposition.TOTAL => RequirementState(state = Some(this.operationCheck(v.getEdges.size(), v)))
  }

}

case class RelationshipRequirement(relation: Relation) extends Requirement {

  private var mustBeRelatedTo: Option[Determiner] = None

  def relatedTo = this.mustBeRelatedTo

  def relatedTo(d: Determiner) = {
    this.mustBeRelatedTo = Some(d)
  }

  protected def check(v: Vertex): RequirementState = {
    //TODO: Fix this... The logic of the if statements has gotten mangled.
    val edgeSet = v.getEdgesWithLabels(relation).asScala
    if (mustBeRelatedTo.isDefined)
    {
      if(mustBeRelatedTo.get.invariable)
      {
        val vertexSet = edgeSet.map(e => e.other(v))
        Requirement.determiners.put(mustBeRelatedTo.get.identifier, vertexSet.toSet)
      }
      if(this.operation.isDefined)
      {
        RequirementState(state = Some(this.operationCheck(edgeSet.count(e => e.other(v).getLabel.equals(mustBeRelatedTo.get.provType)), v)))
      }
      else if(edgeSet.exists(e => e.other(v).getLabel.equals(mustBeRelatedTo.get.provType)))
      {
        RequirementState(satisfied = Some(true), continue = Some(true))
      }
      else
      {
        RequirementState(satisfied = Some(false), continue = Some(true))
      }
    }
    else if(this.operation.isDefined)
    {
      RequirementState(state = Some(this.operationCheck(edgeSet.size, v)))
    }
    else
    {
      val hasRel: Boolean = edgeSet.nonEmpty
      val probability: Boolean = this.probability.isDefined && (this.probability.get < Random.nextDouble)
      if(!hasRel && probability)
      {
        RequirementState(satisfied = Some(hasRel), continue = Some(true))
      }
      else
      {
        RequirementState(satisfied = Some(hasRel), continue = Some(hasRel == probability))
      }
    }
  }

}

case class PropertyRequirement(propertyKey: String, propertyValue: Any) extends Requirement {
  protected def check(v: Vertex): RequirementState = {
    val hasProperty: Boolean = v.hasProperty(propertyKey) && v.getProperty(propertyKey).equals(propertyValue)
    val probability: Boolean = this.probability.isDefined && (this.probability.get > Random.nextDouble)
    RequirementState(satisfied = Some(hasProperty), continue = Some(probability == hasProperty))
  }
}

case class RequirementState(private val satisfied: Option[Boolean] = None, private val continue: Option[Boolean] = None, private val state: Option[Int] = None) extends State {
  require( state.isDefined || (satisfied.isDefined && continue.isDefined), "You must specify either a State (-1, 0 or 1) or both satisfied and continue parameters!")
  override def isSatisfied: Boolean = if(satisfied.isDefined) satisfied.get else state.get == 0
  override def shouldContinue: Boolean = if(continue.isDefined) continue.get else state.get < 1
}