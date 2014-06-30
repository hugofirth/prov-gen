package uk.ac.ncl.prov.lib.statistical

import org.apache.commons.math3.random.RandomDataGenerator

sealed abstract class Distribution(params: List[String]) {
  def getRand: Int
}

object Distribution {
  val rnd: RandomDataGenerator = new RandomDataGenerator()
}

case class Uniform(params: List[String]) extends Distribution(params)
{
  require( params.size ==2, "Wrong number of arguments! Uniform distribution expects (int lower, int upper). Found "+params.size+" arguments." )
  val lower: Double = params(0).toDouble
  val upper: Double = params(1).toDouble

  override def getRand: Int = Distribution.rnd.nextUniform(lower, upper).ceil.toInt
}

case class Binomial(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Binomial distribution expects (int trials, double p). Found "+params.size+" arguments." )
  val trials: Int = params(0).toInt
  val p: Double = params(1).toDouble

  override def getRand: Int = Distribution.rnd.nextBinomial(trials, p)
}

case class Gamma(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Gamma distribution expects (double shape, double scale). Found "+params.size+" arguments." )
  val shape: Double = params(0).toDouble
  val scale: Double = params(1).toDouble

  override def getRand: Int = Distribution.rnd.nextGamma(shape, scale).ceil.toInt
}

case class HyperGeometric(params: List[String]) extends Distribution(params)
{
  require( params.size == 3, "Wrong number of arguments! HyperGeometric distribution expects (int populationSize, int numberOfSuccess, int sampleSize). Found "+params.size+" arguments.")
  val populationSize: Int = params(0).toInt
  val numberOfSuccesses: Int = params(1).toInt
  val sampleSize: Int = params(2).toInt

  override def getRand: Int = Distribution.rnd.nextHypergeometric(populationSize, numberOfSuccesses, sampleSize)
}

case class Pascal(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Pascal distribution expects (int r, double p). Found "+params.size+" arguments.")
  val r: Int = params(0).toInt
  val p: Double = params(1).toDouble

  override def getRand: Int = Distribution.rnd.nextPascal(r, p)
}

case class Zipf(params: List[String]) extends Distribution(params)
{
  require( params.size == 2, "Wrong number of arguments! Zipf distribution expects (int numberOfElements, double exponent). Found "+params.size+" arguments.")
  val numberOfElements: Int = params(0).toInt
  val exponent: Double = params(1).toDouble

  override def getRand: Int = Distribution.rnd.nextZipf(numberOfElements, exponent)
}
