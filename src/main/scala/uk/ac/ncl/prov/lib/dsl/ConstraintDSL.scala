package uk.ac.ncl.prov.lib.dsl

import scala.util.parsing.combinator.syntactical._

object ConstraintDSL extends StandardTokenParsers {

    lexical.delimiters += ("(",")",".")
    lexical.reserved += ("an", "a", "the", "must", "may", "have", "be", "when", "unless")


}
