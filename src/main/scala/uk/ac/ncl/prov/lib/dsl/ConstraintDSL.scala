package uk.ac.ncl.prov.lib.dsl

import scala.util.parsing.combinator.syntactical._

object ConstraintDSL extends StandardTokenParsers {

    //an(Agent).must(have(relationship('')))
    //OR an Agent must have relationship wasGeneratedBy at least 1 times, unless it ...

    lexical.delimiters += ("(",")",".")
    lexical.reserved += ("an", "a", "the", "must", "may", "have", "be", "when", "unless")

    def constraint: Parser[Constraint] = determiner~imperative~condition^^{
      case d~i~c => new Constraint(Constraint.Type.ACTIVITY) //TODO actually work out constructor
    }
    //Get the determined object or type to which a constraint applies
    def variableDeterminer = ("a" | "an")
    def inVariableDeterminer = "the"
    def determiner: Parser[String] = variableDeterminer ~> "(" ~> stringLit <~ ")"


    //


}
