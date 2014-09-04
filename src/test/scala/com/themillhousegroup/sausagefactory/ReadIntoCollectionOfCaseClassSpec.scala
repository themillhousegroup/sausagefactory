package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification
import org.specs2.mutable.Specification

class ReadIntoCollectionOfCaseClassSpec extends Specification with CaseClassSpecification {

  "Reading maps into case classes - nested collections of case classes  -" should {

    "Support nested collections of case classes" in new CaseClassScope(
      buildMap(
        "foo",
        List(
          buildMap("a", "b", "c"),
          buildMap("x", "y", "z")
        ))) {

      //      val readResult = readIntoResult[ListOfNestedCaseClasses]
      //      readResult must not beNull
      //
      //      readResult.first must not beNull
      //
      //      readResult.first must beEqualTo("foo")
      //
      //      readResult.second must not beNull
      //
      //      readResult.second must haveSize(2)
      //
      //      readResult.second.head must beAnInstanceOf[AllStrings] // Currently failing - implement!

      pending("not yet implemented")
    }
  }
}
