package uk.ac.ncl.prov.controller

import org.json4s.jackson.Json
import uk.ac.ncl.prov.lib.constraint.Constraint
import uk.ac.ncl.prov.lib.generator.{Generator, Neo4jGenerator}
import uk.ac.ncl.prov.lib.graph.edge.Edge
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

    val expectedEdge: Edge = E().label(Relation.WASGENERATEDBY).from( V().label(Type.ENTITY).build() ).to( V().label(Type.ACTIVITY).build() ).build()
    println(parsedSeed.getEdges.size + " edges parsed from seed! "+parsedSeed.getEdges.get(0).getLabel.getName)
    println("From: "+parsedSeed.getEdges.get(0).from().getLabel + " should be "+expectedEdge.from().getLabel + ". "+parsedSeed.getEdges.get(0).from().getLabel.equals(expectedEdge.from().getLabel))
    println("To: "+parsedSeed.getEdges.get(0).to().getLabel + " should be "+expectedEdge.to().getLabel + ". "+parsedSeed.getEdges.get(0).to().getLabel.equals(expectedEdge.to().getLabel))
    println("Properties: "+parsedSeed.getEdges.get(0).getProperties.toString + " should be "+expectedEdge.getProperties)
    println("Orientation: "+parsedSeed.getEdges.get(0).getOrientation + " should be "+expectedEdge.getOrientation + ". "+parsedSeed.getEdges.get(0).getOrientation.equals(expectedEdge.getOrientation))
    println("Is similar "+parsedSeed.getEdges.get(0).isSimilar(expectedEdge))

    //Parse constraints
    val constraints = constraintsBlob.split(";").toList
    val parsedConstraints:List[Constraint] = for (c <- constraints) yield Constraint(c)

    //If all successful generate random id and store in a key value store with an unfinished flag

    //Create Generator object with all parameters, pass id to generator or maybe success callback?
    val generator: Generator = new Neo4jGenerator(parsedSeed, parsedConstraints.asJava)

    //Execute generator
    generator.execute(executionParams.size, executionParams.order, executionParams.numGraphs)
    //Return success with random id
    println("Finished executing")

  }

  get("/graphs/:id") {

    //This should actually be a websocket connection.

  }

}
