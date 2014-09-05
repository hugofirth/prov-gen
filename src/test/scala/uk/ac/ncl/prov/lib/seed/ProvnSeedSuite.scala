package uk.ac.ncl.prov.lib.seed

import org.scalatest.FunSuite
import org.scalatest.matchers._
import uk.ac.ncl.prov.lib.graph.util.DegreePreposition

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ProvnSeedSuite extends FunSuite with ShouldMatchers{

  /**
   * =====================================================
   * Test cases to ensure correct parsing of simple PROV-N
   * =====================================================
   */
  test("A valid PROV-N string should be parsed successfully") {
    //TODO: Expand this test and make more of them - this does almost nothing.
    val eol: String = sys.props("line.separator")
    val result = ProvnSeed("" +
      "document" + eol +
      " activity(a1, [fct=\"save\", ex=\"124\"])" + eol +
      " entity(e1)" + eol +
      " wasGeneratedBy(r1; e1, a1)" + eol +
      "endDocument")
    result.getEdges.size should equal(1)
  }


}
