package uk.ac.ncl.prov.controller

class ConstraintController extends Controller {

  get("/") {
    Constraints.all
  }

}

//Dumby Model
case class Constraint(slug: String, name: String)

//Dumby Data
object Constraints {
  val all = List (
      Constraint("con-1", "First Constraint"),
      Constraint("con-2", "Second Constraint"),
      Constraint("con-3", "Third Constraint")
  )
}
