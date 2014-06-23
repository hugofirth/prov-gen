package uk.ac.ncl.prov.start

import uk.ac.ncl.prov.controller._

import org.scalatra._
import javax.servlet.ServletContext

class Bootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context mount(new ConstraintController, "/demo/*")
  }
}
