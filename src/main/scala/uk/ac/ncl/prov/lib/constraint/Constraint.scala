package uk.ac.ncl.prov.lib.constraint

import uk.ac.ncl.prov.lib.generator.OperationState
import uk.ac.ncl.prov.lib.graph.edge.Edge
import uk.ac.ncl.prov.lib.graph.vertex.Vertex
import scala.util.parsing.combinator.syntactical._
import uk.ac.ncl.prov.lib.prov.{Type, Relation}
import uk.ac.ncl.prov.lib.graph.util.DegreePreposition
import uk.ac.ncl.prov.lib.util.ExtLexical
import uk.ac.ncl.prov.lib.util.Quote._
import uk.ac.ncl.prov.lib.statistical.DistributionType

class Constraint private (val determiner: Determiner,
                          val imperative: Imperative,
                          val conditions: Option[Conditions],
                          val determiners: Map[String, Determiner], val constraintString: String) {

  private def evaluate(v: Vertex): OperationState = {
    if(!isApplicableTo(v))
    {
      OperationState(satisfied = false, continue = true) //Constraint is restrictive not permissive.
    }
    else
    {
      //TODO: keep an eye out on the below and perform some more exhaustive testing to avoid unexpected consequences
      //Where does this get cleared? Oh - is created from scratch each time?
      Requirement.determiners(scala.collection.mutable.Map[String, Set[Vertex]]() += ("it" -> Set[Vertex](v)))
      val impReqState: RequirementState = this.imperative.requirement.check("it")
      val impState: OperationState = OperationState(impReqState.isSatisfied == this.imperative.positive, impReqState.shouldContinue)

      if(!this.conditions.isDefined || (this.conditions.isDefined && (this.conditionsAreMet == this.conditions.get.when)))
      {
        impState
      }
      else
      {
        //TODO: look into the possibility of using conditition's continue here
        //val continue = this.conditions.get.conditions.forall(c => c.requirement.check(c.determiner.identifier).shouldContinue)
        OperationState(satisfied = false, continue = false)

      }
    }
  }

    //Any requirement may be preceded and followed by a determiner.
    //The first vertex is passed as the head of a set of vertices stored in a determiner
    //The second determiner is recorded as a set of vertices which match the requirement, into a determiner with the variable specified.
    //The Determiners vertex sets are cleared after each check for satisfaction.
    //The Activity a1 must have relationship "used" unless it has property create;

    //Vertex is checked for applicability. If v.label = determiner.provType then proceed and pass the vertex to determiner.
    //Vertex from determiner is checked against imperative requirement. If result matches imperative.positive then proceed.
      //When checking vertex against a requirement, if another determiner is specified this determiner is matched against vertex's relationships, returning
      //appropriate vertices in a set. This set is then assigned to the new determiner object which is stored in the determiners map against the variable name.
    //Vertex is finally checked against condition, if a forall over all conditions returns a boolean matching condition.when then return true.

    //a Vertex, or a Set of Vertices, must be passable to a determiner.
    //An imperative requirement should check() on the first vertex in a set of vertices belonging to a determiner


  def evaluate(e: Edge): OperationState = {
    val right: OperationState = this.evaluate(e.getConnecting()(1))
    val left: OperationState = this.evaluate(e.getConnecting()(0))
    OperationState(satisfied = left.isSatisfied && right.isSatisfied, continue = left.shouldContinue && right.shouldContinue)
  }

  def isApplicableTo(v: Vertex): Boolean = this.determiner.provType.equals(v.getLabel)

  private def conditionsAreMet: Boolean = if(this.conditions.isDefined) this.conditions.get.conditions.forall(c => c.requirement.check(c.determiner.identifier).isSatisfied) else true

}

case class Determiner(provType: Type, invariable: Boolean = false, identifier: String = "")
case class Imperative(requirement: Requirement, positive: Boolean = true)
case class Conditions(conditions: List[Condition], when: Boolean = true)
case class Condition(requirement: Requirement, determiner: Determiner)

object Constraint extends StandardTokenParsers {

  private var determiners: Map[String, Determiner] = _
  private var constraintString: String = _
  override val lexical = new ExtLexical

  lexical.delimiters += ("(",")",".",",",";","=")
  lexical.reserved += (
    "an", "a", "the", "have", "when", "unless", "it", "must", "distribution",
    "degree", "in", "out", "relationship", "at", "most", "not", "and", "has",
    "least", "between", "exactly", "property", "with", "probability", "times"
  )

  def apply(dsl: String): Constraint = {
    determiners = Map()
    constraintString = dsl
    parse(dsl)
  }

  //Method to take in a DSL string and parse it
  private def parse(dsl: String): Constraint =
    constraint(new lexical.Scanner(dsl)) match {
      case Success(constr, _) => constr
      case Failure(msg, _) => throw new IllegalArgumentException(msg)
      case Error(msg, _) => throw new IllegalArgumentException(msg)
    }

  private def constraint: Parser[Constraint] = determiner~imperative~opt(conditions)^^{
    case d~i~c => new Constraint(d, i, c, determiners, constraintString)
  }

  //Get the determined object or type to which a constraint applies
  private def variableDeterminer = ("a" | "an") ~> ident
  private def inVariableDeterminer = "the" ~> (ident <~ ",") ~ ident <~ ","
  private def determiner: Parser[Determiner] =
    (variableDeterminer^^{
      case t =>
        val d: Determiner = Determiner(Type.withName(t))
        if (determiners.isEmpty) determiners += ("it" -> d); d
    }) |
    (inVariableDeterminer^^{
      case t~o =>
        val d: Determiner = Determiner(Type.withName(t), invariable = true, o)
        determiners += (o -> d); d
    })

  //Get the imperative statement and its requirement function
  private def imperative: Parser[Imperative] = "must"~>opt("not")~("have"~>requirement)^^{
    case None~r => Imperative(r)
    case Some("not")~r => Imperative(r, positive=false)
  }

  //Get the condition statement and its check function
  private def condition: Parser[Condition] = (reference~("has"~>requirement))^^{
    case ref~req if determiners.isDefinedAt(ref) => Condition(req, determiners.get(ref).get)
    case ref~req => throw new IllegalArgumentException("Condition requirement reference "+ref+" is undefined!")
  }

  private def conditions: Parser[Conditions] = (("when"|"unless")~rep1sep(condition, "and"))^^{
    case "when"~cons => Conditions(cons)
    case "unless"~cons => Conditions(cons, when=false)
  }

  private def reference: Parser[String] = "it"|ident

  //Get the requirements (for imperative and condition checks)
  private def requirement: Parser[Requirement] = requirementFeature into requirementModifier

  private def requirementModifier(req: Requirement): Parser[Requirement] =
    (((rangeModifier(req)<~opt("times")) ~ opt(withDistribution(req))) | withModifier(req))^^{ _ => req }

  private def rangeModifier(req: Requirement): Parser[Requirement] =
    (((("at"~>("most" | "least")) | "exactly") ~ numericLit)^^{
      case "most"~n => req atMost n.toInt; req
      case "least"~n => req atLeast n.toInt; req
      case "exactly"~n => req exactly n.toInt; req
    }) | (("between"~>numericLit~","~numericLit)^^{
      case min~","~max => req inRange(min.toInt, max.toInt); req
    })

  private def withDistribution(req: Requirement) =
    "with"~>"distribution"~>ident~("("~>rep1sep(numericLit, ",")<~")")^^{
      case d~p => req.distribution(DistributionType.withName(d),p); req
    }

  private def withModifier(req: Requirement): Parser[Requirement] =  {
    req match {
      case r: DegreeRequirement => err("_with_ modifiers may not be applied to Degree requirements")
      case r: RelationshipRequirement => withDeterminer(r) | withProbability(r)
      case r: Requirement => withProbability(r)
      case _ => err("_with_ modifiers must be applied to Requirements")
    }
  }

  //TODO: Decide between with and to...
  private def withDeterminer(req: RelationshipRequirement): Parser[Requirement] = ("with"~>determiner)^^{
    case d => req relatedTo d; req
  }

  //TODO: Check how floats/doubles are actually parsed.
  private def withProbability(req: Requirement): Parser[Requirement] = "with"~>"probability"~>numericLit^^{
    case p => req.probability = p.toDouble; req
  }

  private def requirementFeature: Parser[Requirement] =
    (("relationship" ~> stringLit )^^{
      case r => new RelationshipRequirement(Relation.withName(r))
    }) |
    ((opt("in" | "out")~"degree")^^{
      case Some("in")~"degree" => new DegreeRequirement(DegreePreposition.IN)
      case Some("out")~"degree" => new DegreeRequirement(DegreePreposition.OUT)
      case None~"degree" => new DegreeRequirement()
    }) |
    (("property" ~> "(" ~> pair <~ ")")^^{
      case (k, v) => PropertyRequirement(k, v)
    })

  private def pair: Parser[(String, Any)] =
    stringLit ~ "=" ~ (
      stringLit ^^ (s => s) |
      numericLit ^^ (n => n.toInt)
    ) ^^ { case k ~ "=" ~ v => (unquote(k), v)}

}

