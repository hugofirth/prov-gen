package uk.ac.ncl.prov.lib.statistical

sealed abstract class Distribution(params: List[String]) {

}

case class Binomial(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Binomial distribution expects (int trials, double p). Found "+params.size+" arguments." )
  val trails: Int = params(0).toInt
  val p: Double = params(1).toDouble
}
case class Gamma(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Gamma distribution expects (double shape, double scale). Found "+params.size+" arguments." )
  val shape: Double = params(0).toDouble
  val scale: Double = params(1).toDouble
}
case class HyperGeometric(params: List[String]) extends Distribution(params)
{
  require( params.size == 3, "Wrong number of arguments! HyperGeometric distribution expects (int populationSize, int numberOfSuccess, int sampleSize). Found "+params.size+" arguments.")
  val populationSize: Int = params(0).toInt
  val numberOfSuccesses: Int = params(1).toInt
  val sampleSize: Int = params(2).toInt
}
case class Poisson(params: List[String]) extends Distribution(params)
{
  require( params.size == 1, "Wrong number of arguments! Poisson distribution expects (double mean). Found "+params.size+" arguments.")
  val mean: Double = params(0).toDouble
}
case class Pascal(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Pascal distribution expects (int r, double p). Found "+params.size+" arguments.")
  val r: Int = params(0).toInt
  val p: Double = params(1).toDouble
}
case class Zipf(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Zipf distribution expects (int numberOfElements, double exponent). Found "+params.size+" arguments.")
  val numberOfElements: Int = params(0).toInt
  val exponent: Double = params(1).toDouble
}
