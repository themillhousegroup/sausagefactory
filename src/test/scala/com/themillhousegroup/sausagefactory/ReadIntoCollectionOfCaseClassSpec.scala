package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification
import org.specs2.mutable.Specification
import scala.reflect._
import scala.reflect.runtime.universe._

class ReadIntoCollectionOfCaseClassSpec extends Specification with CaseClassSpecification {

  "Reading maps into case classes - nested collections of case classes  -" should {

    class CollectionScope(m: Map[String, Any]) extends CaseClassScope(m) {

      def shouldBeAbleToReadInto[T <: IterablesOfCaseClasses: TypeTag, I: ClassTag] = {
        val readResult = readIntoResult[T]

        readResult must not beNull

        readResult.first must not beNull

        readResult.first must beEqualTo("foo")

        readResult.second must not beNull

        readResult.second must haveSize(2)

        readResult.second.head must beAnInstanceOf[I]

      }
    }

    "Support nested Lists of case classes" in new CollectionScope(
      buildMap(
        "foo",
        List(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      shouldBeAbleToReadInto[ListOfNestedCaseClasses, AllStrings]
    }

    "Support nested Seqs of case classes" in new CollectionScope(
      buildMap(
        "foo",
        Seq(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      shouldBeAbleToReadInto[SeqOfNestedCaseClasses, AllStrings]

    }

    "Support nested Sets of case classes" in new CollectionScope(
      buildMap(
        "foo",
        Set(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      shouldBeAbleToReadInto[SetOfNestedCaseClasses, AllStrings]

    }

    "Support nested Maps of case classes with String keys" in new CaseClassScope(
      buildMap(
        "foo",
        buildMap(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      val result = readIntoResult[MapOfNestedCaseClasses]

      result must not beNull

      result.second must beAnInstanceOf[Map[String, AllStrings]]

      val rMap = result.second

      println(s"Rmap: $rMap")

      val first = rMap("first")

      first must not beNull

      first must beAnInstanceOf[AllStrings]

    }
  }
}
