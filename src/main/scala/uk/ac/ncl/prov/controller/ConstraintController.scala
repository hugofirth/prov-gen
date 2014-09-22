package uk.ac.ncl.prov.controller


import org.zeroturnaround.zip.ZipUtil

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
case class Generation(seed:String,constraints:String,executionParams:ExecutionParams,includeDB:Boolean)
//AST for response
case class GraphFile(name:String)

class ConstraintController extends Controller {

  post("/graphs") {
    println("Received a request")
    //Parse Json input
    val input:Generation = parsedBody.extract[Generation]

    //Get input from POST request
    val seed:String = input.seed
    val constraintsBlob:String = input.constraints
    val executionParams:ExecutionParams = input.executionParams

    println("Parsing Seed...")
    val parseSeedStart = System.currentTimeMillis()
    //Parse seed
    val parsedSeed:Seed = ProvnSeed(seed)
    println("Parsing Seed Finished. Took "+(System.currentTimeMillis()-parseSeedStart))

    println("Parsing Constraints...")
    val parseConstraintsStart = System.currentTimeMillis()
    //Parse constraints
    val constraints = constraintsBlob.split(";").toList
    val parsedConstraints:List[Constraint] = for (c <- constraints) yield Constraint(c)
    println("Parsing Constraints Finished. Took "+(System.currentTimeMillis()-parseConstraintsStart))
    //If all successful generate random id and store in a key value store with an unfinished flag

    println("Creating Generator...")
    val createGeneratorStart = System.currentTimeMillis()
    //Create Generator object with all parameters, pass id to generator or maybe success callback?
    val generator: Generator = new Neo4jGenerator("target/prov-db", parsedSeed, parsedConstraints.asJava)
    println("Finished Creating Generator. Took "+(System.currentTimeMillis()-createGeneratorStart))

    println("Generating...")
    val generatingStart = System.currentTimeMillis()
    //Execute generator
    generator.execute(executionParams.size, executionParams.order, executionParams.numGraphs)
    println("Finished Generating. Took "+(System.currentTimeMillis()-generatingStart))

    println("Exporting Graph to Prov-N...")
    val exportingStart = System.currentTimeMillis()
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
    println("Finished Exporting Graph. Took "+(System.currentTimeMillis()-exportingStart))

    //Clear database folders
    if(input.includeDB)
    {
      val zip: File = new File("target/"+time+".zip")
      FileUtils.moveDirectory(new File("target/prov-db"), new File("target/"+time+".zip/graph.db"))
      FileUtils.moveFile(new File("target/"+fileName), new File("target/"+time+".zip/"+fileName))
      ZipUtil.unexplode(zip)
      println("Zipping up files")
      GraphFile(time+".zip")
    }
    else
    {
      val db: File = new File("target/prov-db")
      FileUtils.deleteDirectory(db)
      //Return the name of the file
      GraphFile(fileName)
    }
  }

  get("/graphs/:name") {
    val fileName: String = params("name")
    val file: File = new File("target/"+fileName)
    if (file.exists && file.getName.endsWith(".provn")) {
      //TODO: Investigate making this a websocket connection.
      //Return prov-n File
      contentType = "text/provenance-notation"
      response.setHeader("Content-Disposition", "attachment; filename=" + file.getName)
      fromFile(file).getLines mkString "\n"
    }
    else if (file.exists && file.getName.endsWith("zip"))
    {
      contentType = "application/zip"
      response.setHeader("Content-Disposition", "attachment; filename=" + file.getName)
      file
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
      //file.delete()
    }
  }

}
