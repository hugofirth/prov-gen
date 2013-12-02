package uk.ac.ncl.prov

/**
 * @author ${user.name}
 */
import org.scalatra._

class MyServlet extends ScalatraServlet {

  get("/") {
    "Hi from PROV-Gen!"
  }
}
