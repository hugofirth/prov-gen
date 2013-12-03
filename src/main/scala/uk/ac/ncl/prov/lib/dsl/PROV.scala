package uk.ac.ncl.prov.lib.dsl

/**
 * Created with IntelliJ IDEA.
 * User: hugofirth
 * Date: 03/12/2013
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
object PROV {
  //Define PROV Types
  object Type extends Enumeration {
    type Type = Value
    val Entity = Value("Entity")
    val Activity = Value("Activity")
    val Agent = Value("Agent")
  }
  //Define PROV Relations
  object Relation extends Enumeration {
    type Relation = Value
    val WasGeneratedBy = Value("WasGeneratedBy")
    val Used = Value("Used")
    val WasInformedBy = Value("WasInformedBy")
    val WasDerivedFrom = Value("WasDerivedFrom")
    val WasAttributedTo = Value("WasAttributedTo")
    val WasAssociatedWith = Value("WasAssociatedWith")
    val ActedOnBehalfOf = Value("ActedOnBehalfOf")
  }
}