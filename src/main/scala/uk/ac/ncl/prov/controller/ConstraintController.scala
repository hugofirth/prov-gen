package uk.ac.ncl.prov.controller


import scala.io.Source._
import java.io.File
import org.apache.commons.io.FileUtils
import org.neo4j.graphdb.{Transaction, Node, Relationship, GraphDatabaseService}
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.tooling.GlobalGraphOperations
import org.scalatra.NotFound
import uk.ac.ncl.prov.lib.constraint.Constraint
import uk.ac.ncl.prov.lib.export.{ProvnExport, Export}
import uk.ac.ncl.prov.lib.generator.{Generator, Neo4jGenerator}
import uk.ac.ncl.prov.lib.seed.{ProvnSeed, Seed}
import scala.collection.JavaConverters._


//AST for request
case class ExecutionParams(size:Int, order:Int, numGraphs:Int)
case class Generation(seed:String,constraints:String, executionParams:ExecutionParams)
//AST for response
case class ProvnFile(name:String)

class ConstraintController extends Controller {

  post("/graphs") {
    println("Received a request")
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
    val generator: Generator = new Neo4jGenerator("target/prov-db", parsedSeed, parsedConstraints.asJava)

    //Execute generator
    generator.execute(executionParams.size, executionParams.order, executionParams.numGraphs)

    //Export generated graph to PROV-N
    val graphDb: GraphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase("target/prov-db")
    val tx: Transaction = graphDb.beginTx()
    val time: String = System.currentTimeMillis().toString; //TRACE
    val fileName: String = time+".provn"
    try {
      val exporter: Export = new ProvnExport("target/"+fileName)
      val nodes: Iterable[Node] = GlobalGraphOperations.at(graphDb).getAllNodes.asScala
      val relationships: Iterable[Relationship] = GlobalGraphOperations.at(graphDb).getAllRelationships.asScala
      for (n <- nodes) { exporter.serializeNode(n) }
      for (r <- relationships) { exporter.serializeRelationship(r) }
      exporter.export()
      tx.success()
    }
    graphDb.shutdown()

    //Clear database folders
    val db: File = new File("target/prov-db")
    FileUtils.deleteDirectory(db)
    //Return the name of the file
    ProvnFile(fileName)
  }

  get("/graphs/:name") {
    val fileName: String = params("name")
    val file: File = new File("target/"+fileName)
    if (file.exists) {
      //TODO: Investigate making this a websocket connection.
      //Return prov-n File
      contentType = "text/provenance-notation"
      response.setHeader("Content-Disposition", "attachment; filename=" + file.getName)
      fromFile(file).getLines mkString "\n"
    }
    else
    {
      NotFound("Sorry, the file could not be found")
    }
  }

  after("/graphs/:name") {
    //Delete the file after it has been requested.
    val fileName: String = params("name")
    val file: File = new File("target/"+fileName)
    if (file.exists) {
      file.delete()
    }
  }

}
