package uk.ac.ncl.prov.lib.generator

import uk.ac.ncl.prov.lib.util.State

case class OperationState(private val satisfied: Boolean, private val continue: Boolean) extends State {
  override def isSatisfied: Boolean = this.satisfied
  override def shouldContinue: Boolean = this.continue
}
