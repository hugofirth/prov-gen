package uk.ac.ncl.prov.lib.util

trait State {
  def isSatisfied: Boolean
  def shouldContinue: Boolean
}
