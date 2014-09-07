package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification
import org.specs2.mutable.Specification
import scala.reflect._

class ReadIntoCollectionOfCaseClassSpec extends Specification with CaseClassSpecification {

  "Reading maps into case classes - nested collections of case classes  -" should {

    def thenIShouldHaveA[T <: CollectionCaseClass, I: ClassTag](readResult: T) = {
      readResult must not beNull

      readResult.first must not beNull

      readResult.first must beEqualTo("foo")

      readResult.second must not beNull

      readResult.second must haveSize(2)

      readResult.second.head must beAnInstanceOf[I]
    }

    "Support nested Lists of case classes" in new CaseClassScope(
      buildMap(
        "foo",
        List(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      val readResult = readIntoResult[ListOfNestedCaseClasses]

      thenIShouldHaveA[ListOfNestedCaseClasses, AllStrings](readResult)
    }

    "Support nested Seqs of case classes" in new CaseClassScope(
      buildMap(
        "foo",
        Seq(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      val readResult = readIntoResult[SeqOfNestedCaseClasses]

      thenIShouldHaveA[SeqOfNestedCaseClasses, AllStrings](readResult)

    }

    "Support nested Sets of case classes" in new CaseClassScope(
      buildMap(
        "foo",
        Set(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      val readResult = readIntoResult[SetOfNestedCaseClasses]

      thenIShouldHaveA[SetOfNestedCaseClasses, AllStrings](readResult)

    }
  }
}
