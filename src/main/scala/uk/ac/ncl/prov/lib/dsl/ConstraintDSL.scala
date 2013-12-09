package uk.ac.ncl.prov.lib.dsl

import scala.util.parsing.combinator.syntactical._

//TODO: Refactor object to ConstraintParser (makes more sense semantically)
object ConstraintDSL extends StandardTokenParsers {

    //TODO: Try and look up why ++= in some examples
    lexical.delimiters += ("(",")",".",",")
    lexical.reserved += ("an", "a", "the", "must", "may", "have", "has", "be", "when", "unless", "it")

    //Method to take in a DSL string and parse it
    def parseDSL(dsl: String): Constraint = {
      constraint(new lexical.Scanner(dsl)) match {
        case Success(constr, _) => constr
        case Failure(msg, _) => throw new Exception(msg)
        case Error(msg, _) => throw new Exception(msg)
      }
    }

    //TODO: Read up on companion objects in scala and the idiomatic removal of new statements
    def constraint: Parser[Constraint] = determiner~imperative~condition^^{
      case d~i~c => new Constraint(d, i, c)
    }

    //TODO: Add some exception handling to the string->enum matching
    //Get the determined object or type to which a constraint applies
    def variableDeterminer = ("a" | "an") ~> "(" ~> ident <~ ")"
    def inVariableDeterminer = "the" ~> "(" ~> (ident <~ ",") ~ ident <~ ")"
    def determiner: Parser[Determiner] =
      (variableDeterminer^^{ case t => new Determiner(PROV.Type.withName(t)) }) |
      (inVariableDeterminer^^{ case t~o => new Determiner(PROV.Type.withName(t), true, o) })


    //Get the imperative statement and its requirement function
    def imperative: Parser[Imperative] = ("."~>("may"|"must") ~ ("(" ~> requirement <~ ")"))^^{
      case "may"~r => new Imperative(r, false)
      case "must"~r => new Imperative(r)
    }


    //Get the condition statement and its check function
    def condition: Parser[Condition] = ("."~>("unless"|"when") ~ ("(" ~> requirement <~ ")"))^^{
      case "when"~r => new Condition(r, false)
      case "unless"~r => new Condition(r)
    }

    //TODO: Create requirement object structure and syntax

    //Get the requirements (for imperative and condition checks)
    //("have" | "it" ~ "." ~ "has") ~> "(" ~> ident <~ ")"^^
    def requirement: Parser[Requirement] = stringLit^^{
      case s => new Requirement(s)
    }
}
