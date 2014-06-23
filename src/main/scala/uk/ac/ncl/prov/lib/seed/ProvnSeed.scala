package uk.ac.ncl.prov.lib.seed

import uk.ac.ncl.prov.lib.graph.edge.{EdgeLabel, Edge}
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import uk.ac.ncl.prov.lib.prov.{Relation, Type}
import uk.ac.ncl.prov.lib.graph.{Label, Element}
import uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder.V
import uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder.E
import uk.ac.ncl.prov.lib.graph.vertex.{VertexLabel, Vertex}
import uk.ac.ncl.prov.lib.util.ExtLexical
import uk.ac.ncl.prov.lib.util.Quote._

class ProvnSeed private (val e: ListBuffer[Edge]) extends Seed(e.asJava){

}

object ProvnSeed extends StandardTokenParsers {

  private val edges: ListBuffer[Edge] = ListBuffer()
  override val lexical = new ExtLexical

  lexical.delimiters += ("(",")","[","]","-","=", ",", ";", ":")
  lexical.reserved += ("document", "endDocument", "prefix", "default")

  def apply(provn: String): Seed = {
    edges.clear()
    parse(provn)
  }

  private def parse(provn: String): Seed = {
    provDocument(new lexical.Scanner(provn)) match {
      case Success(prov, _) => new ProvnSeed(prov)
      case Failure(msg, _) => throw new IllegalArgumentException(msg)
      case Error(msg, _) => throw new IllegalArgumentException(msg)
    }
  }

  private def provDocument: Parser[ListBuffer[Edge]] = "document"~>provTerms<~"endDocument"

  private def provTerms: Parser[ListBuffer[Edge]] = rep(provTerm<~")")^^{
    case t:List[Element] if t.nonEmpty => t.filter(el => el.isInstanceOf[Edge]).map(e => edges += e.asInstanceOf[Edge]); edges
    case t => throw new IllegalArgumentException("There were no parsable terms found in PROV document! "+t.toString)
  }

  private def provTerm: Parser[Element] = (ident<~"("^^{
    case i: String if Relation.existsWithName(i) => Relation.withName(i)
    case i: String if Type.existsWithName(i) => Type.withName(i)
    case _ => throw new IllegalArgumentException("Identifier "+ident.toString()+" not a recognized PROV term")
  }) >> {
    case l: Relation => relationshipTerm(l)
    case l: Type => typeTerm(l)
  }

  private def relationshipTerm(l: EdgeLabel): Parser[Edge] = opt(ident<~";")~rep1sep(ident, ",")~opt(","~>properties)^^{
    case identifier~params~props if params.size < 2 => throw new IllegalArgumentException("Insufficient number of parameters provided for a relationship "+l.getName+"!")
    case Some(identifier)~params~Some(props) => E(identifier).label(l).to( V(params(1)).get ).from( V(params(0)).get ).properties(props.asJava).build()
    case None~params~Some(props) => E().label(l).to( V(params(1)).get ).from( V(params(0)).get ).properties(props.asJava).build()
    case None~params~None => E().label(l).to( V(params(1)).get ).from( V(params(0)).get ).build()
    case Some(identifier)~params~None => E(identifier).label(l).to( V(params(1)).get ).from( V(params(0)).get ).build()
    case _ => throw new IllegalArgumentException("Improperly formed relationship term!")
  }

  private def typeTerm(l: VertexLabel):Parser[Vertex] = rep1sep(ident, ",")~opt(","~>properties)^^{
    case params~Some(props) => V(params(0)).label(l).properties(props.asJava).build()
    case params~None => V(params(0)).label(l).build()
    case _ => throw new IllegalArgumentException("Improperly formed type term!")
  }

  private def properties: Parser[Map[String, AnyRef]] = "["~>repsep(pair, ",")<~"]"^^{
    case pairs: List[(String, Any)] =>
      var m: Map[String, AnyRef] = Map(); pairs foreach { case (k,v: AnyRef) => m += k -> v }; m
    case _ =>
      throw new IllegalArgumentException ("Incorrectly formatted properties!")
  }

  private def pair: Parser[(String, Any)] =
    ident ~ ":" ~ (
      stringLit ^^ (s => s) |
        numericLit ^^ (n => n.toInt)
      ) ^^ { case k ~ ":" ~ v => (k, v)}

}
