package uk.ac.ncl.prov.lib.constraint


sealed abstract class Operator {
  def check(value: Int): Int
}

case class Least(min: Int) extends Operator {
  def check(value: Int): Int = value match {
    case v if v < this.min => -1
    case v => 0
  }
}
case class Most(max: Int) extends Operator {
  def check(value: Int): Int = value match {
    case v if v > this.max => 1
    case v => 0
  }
}
case class Exact(exact: Int) extends Operator {
  def check(value: Int): Int = value match {
    case v if v < this.exact => -1
    case v if v > this.exact => 1
    case v => 0
  }
}
case class Between(min: Int, max: Int) extends Operator {
  def check(value: Int): Int = value match {
    case v if v < this.min => -1
    case v if v > this.max => 1
    case v => 0
  }
}
