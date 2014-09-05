package uk.ac.ncl.prov.start

import uk.ac.ncl.prov.controller._
import org.scalatra._
import javax.servlet.ServletContext

class Bootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    //Mount the servlet
    context.mount(new ConstraintController, "/api/v1/*")

    //Set the environment
    context.initParameters("org.scalatra.environment") = "production"
  }
}
