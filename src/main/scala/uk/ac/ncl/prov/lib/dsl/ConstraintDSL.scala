package uk.ac.ncl.prov.lib.dsl

import scala.util.parsing.combinator.syntactical._
import prov._, degree._

//TODO: Refactor object to ConstraintParser (makes more sense semantically)
object ConstraintDSL extends StandardTokenParsers {

    //TODO: Try and look up why ++= in some examples
    lexical.delimiters += ("(",")",".",",")
    lexical.reserved += (
      "an", "a", "the", "must", "may", "have",
      "has", "be", "when", "unless", "it",
      "degree", "in", "out", "relationship",
      "at", "most", "least", "between"
    )

    //Method to take in a DSL string and parse it
    def parseDSL(dsl: String): Constraint = {
      constraint(new lexical.Scanner(dsl)) match {
        case Success(constr, _) => constr
        case Failure(msg, _) => throw new Exception(msg)
        case Error(msg, _) => throw new Exception(msg)
      }
    }

    def constraint: Parser[Constraint] = determiner~imperative~condition^^{
      case d~i~c => new Constraint(d, i, c)
    }

    //TODO: Add some exception handling to the string->enum matching
    //Get the determined object or type to which a constraint applies
    def variableDeterminer = ("a" | "an") ~> "(" ~> ident <~ ")"
    def inVariableDeterminer = "the" ~> "(" ~> (ident <~ ",") ~ ident <~ ")"
    def determiner: Parser[Determiner] =
      (variableDeterminer^^{ case t => Determiner(Type.withName(t)) }) |
      (inVariableDeterminer^^{ case t~o => Determiner(Type.withName(t), invariable = true, o) })


    //Get the imperative statement and its requirement function
    def imperative: Parser[Imperative] = ("."~>("may"|"must") ~ ("(" ~> requirement <~ ")"))^^{
      case "may"~r => Imperative(r, necessary = false)
      case "must"~r => Imperative(r)
    }


    //Get the condition statement and its check function
    def condition: Parser[Condition] = ("."~>("unless"|"when") ~ ("(" ~> requirement <~ ")"))^^{
      case "when"~r => Condition(r, exception = false)
      case "unless"~r => Condition(r)
    }

    //TODO: Create requirement object structure and syntax

    //Get the requirements (for imperative and condition checks)
    def requirement: Parser[Requirement] = (("have" | "it" ~ "." ~ "has") ~> "(" ~> required_feature <~ ")")>>{
      feature => ("."~>(("at"~"."~("most" | "least")) | "between") ~> "(" ~> numericLit <~ ")")^^{ /* TODO: Do something here */ }
    }


    def required_feature: Parser[Requirement] =
      (("relationship" ~> "(" ~> ident <~ ")")^^{
        case r => RelationshipRequirement(Relation.withName(r))
      }) |
      ((opt(("in" | "out")~".")~("degree"<~"(")~opt(numericLit)<~")")^^{
        case "in"~"."~"degree"~n => DegreeRequirement(n.get.toInt, Preposition.IN)
        case "out"~"."~"degree"~n => DegreeRequirement(n.get.toInt, Preposition.OUT)
        case "degree"~n => DegreeRequirement(n.get.toInt)
        case "in"~"."~"degree" => DegreeRequirement(preposition = Preposition.IN)
        case "out"~"."~"degree" => DegreeRequirement(preposition = Preposition.OUT)
        case "degree" => DegreeRequirement()
      })


}
