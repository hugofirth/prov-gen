package uk.ac.ncl.prov.controller

import org.json4s.jackson.Json
import uk.ac.ncl.prov.lib.constraint.Constraint
import uk.ac.ncl.prov.lib.generator.{Generator, Neo4jGenerator}
import uk.ac.ncl.prov.lib.graph.Element
import uk.ac.ncl.prov.lib.graph.edge.Edge
import uk.ac.ncl.prov.lib.graph.vertex.Vertex
import uk.ac.ncl.prov.lib.prov.{Type, Relation, Definition}
import uk.ac.ncl.prov.lib.seed.{ProvnSeed, Seed}
import scala.collection.JavaConverters._
import uk.ac.ncl.prov.lib.graph.vertex.Vertex.VertexBuilder.V
import uk.ac.ncl.prov.lib.graph.edge.Edge.EdgeBuilder.E

//AST for request
case class ExecutionParams(size:Int, order:Int, numGraphs:Int)
case class Generation(seed:String,constraints:String, executionParams:ExecutionParams)

class ConstraintController extends Controller {

  post("/graphs") {

    //Parse Json input
    val input:Generation = parsedBody.extract[Generation]

    //Get input from POST request
    val seed:String = input.seed
    val constraintsBlob:String = input.constraints
    val executionParams:ExecutionParams = input.executionParams

    //Parse seed
    val parsedSeed:Seed = ProvnSeed(seed)

    //Parse constraints
    val constraints = constraintsBlob.split(";").toList
    val parsedConstraints:List[Constraint] = for (c <- constraints) yield Constraint(c)

    //If all successful generate random id and store in a key value store with an unfinished flag

    //Create Generator object with all parameters, pass id to generator or maybe success callback?
    val generator: Generator = new Neo4jGenerator(parsedSeed, parsedConstraints.asJava)

    //Execute generator
    generator.execute(executionParams.size, executionParams.order, executionParams.numGraphs)
    //Return success with random id
    println("Finished executing: Generated graph of size: "+generator.getEdges.size()+" and order: "+generator.getVertices.size())
    if(generator.getEdges.size()<50) println("Elements -> "+generator.getEdges)

  }

  get("/graphs/:id") {

    //This should actually be a websocket connection.

  }

}
