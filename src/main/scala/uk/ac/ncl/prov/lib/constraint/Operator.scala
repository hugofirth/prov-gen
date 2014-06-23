package uk.ac.ncl.prov.lib.constraint


sealed abstract class Operator {
  def check(value: Int): Boolean
}

case class Least(min: Int) extends Operator {
  def check(value: Int): Boolean = value >= this.min
}
case class Most(max: Int) extends Operator {
  def check(value: Int): Boolean = value <= this.max
}
case class Exact(exact: Int) extends Operator {
  def check(value: Int): Boolean = value == this.exact
}
case class Between(min: Int, max: Int) extends Operator {
  def check(value: Int): Boolean = value >= this.min && value <= this.max
}
