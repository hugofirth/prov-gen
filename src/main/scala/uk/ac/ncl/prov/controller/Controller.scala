package uk.ac.ncl.prov.controller

import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

abstract class Controller extends ScalatraServlet with JacksonJsonSupport with CorsSupport{

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats

  // Before every route runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  // Every controller should respond to options requests for CORS
  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

}
