package uk.ac.ncl.prov.lib.constraint


import scala.util.parsing.combinator.syntactical._
import uk.ac.ncl.prov.lib.prov_dm.{Type, Relation}
import uk.ac.ncl.prov.lib.graph.vertex.Vertex.DegreePreposition

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 03/12/2013
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
class Constraint private (val determiner: Determiner, val imperative: Imperative, val condition: Condition) {


  //TODO: Create a method for creating a Cypher query from the above - toCypherQL or some such

  //is Applicable if

  //Query MATCH Label == Determiner provType

  //Query CREATE relationship matches Imperative

  //How to cope with degree - can we assume that any create statement increases degree so applies?

  //Once applicability is established,

}

//TODO: Fill out Class and Method stubs for all below - placing in their own source files.
case class Determiner(provType: Type, invariable: Boolean = false, identifier: String = "")
case class Imperative(requirement: Requirement)
case class Condition(requirement: Requirement, when: Boolean = true)

object Constraint extends StandardTokenParsers {

  lexical.delimiters += ("(",")",".",",",";")
  lexical.reserved += (
    "an", "a", "the", "has", "when", "unless", "it",
    "degree", "in", "out", "relationship", "at", "most",
    "least", "between", "exactly", "role", "with", "probability"
  )

  def apply(dsl: String): Constraint = {parse(dsl)}

  //Method to take in a DSL string and parse it
  private def parse(dsl: String): Constraint =
    constraint(new lexical.Scanner(dsl)) match {
      case Success(constr, _) => constr
      case Failure(msg, _) => throw new Exception(msg)
      case Error(msg, _) => throw new Exception(msg)
    }

  private def constraint: Parser[Constraint] = determiner~imperative~condition<~";"^^{
    case d~i~c => new Constraint(d, i, c)
  }

  //TODO: Add some exception handling to the string->enum matching
  //Get the determined object or type to which a constraint applies
  private def variableDeterminer = ("a" | "an") ~> ident
  private def inVariableDeterminer = "the" ~> (ident <~ ",") ~ ident <~ ","
  private def determiner: Parser[Determiner] =
    (variableDeterminer^^{ case t => Determiner(Type.withName(t)) }) |
      (inVariableDeterminer^^{ case t~o => Determiner(Type.withName(t), invariable = true, o) })

  //Get the imperative statement and its requirement function
  private def imperative: Parser[Imperative] = requirement^^Imperative

  //Get the condition statement and its check function
  private def condition: Parser[Condition] = (("unless"|"when") ~ ("it" ~> requirement))^^{
    case "when"~r => Condition(r)
    case "unless"~r => Condition(r, when=false)
  }

  //Get the requirements (for imperative and condition checks)
  private def requirement: Parser[Requirement] = "has"~>requirementFeature into requirementModifier

  private def requirementModifier(req: Requirement): Parser[Requirement] =  rangeModifier(req) | withModifier(req);

  private def rangeModifier(req: Requirement): Parser[Requirement] =
    (((("at"~>("most" | "least")) | "exactly") ~ numericLit)^^{
      case "most"~n => req.most = n.toInt; req
      case "least"~n => req.least = n.toInt; req
      case "exactly"~n => req.exact = n.toInt; req
    }) | (("between"~>numericLit~","~numericLit)^^{
      case min~","~max => req.range = (min.toInt, max.toInt); req
    })

  private def withModifier(req: Requirement): Parser[Requirement] =  {
    req match {
      case r: DegreeRequirement => err("_with_ modifiers may not be applied to Degree requirements")
      case r: RelationshipRequirement => withDeterminer(r) | withProbability(r)
      case r: Requirement => withProbability(r)
      case _ => err("_with_ modifiers must be applied to Requirements")
    }
  }

  private def withDeterminer(req: RelationshipRequirement): Parser[Requirement] = ("with"~>determiner)^^{
    case d => req.related = d.provType; req
  }

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
    })

}
