package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification
import org.specs2.mutable.Specification

class ReadIntoCollectionOfCaseClassSpec extends Specification with CaseClassSpecification {

  "Reading maps into case classes - nested single objects -" should {

    "Support nested mapping of case classes" in new CaseClassScope(
      buildMap(buildMap("foo", "bar", "baz"))) {

      val readResult = readIntoResult[NestedJustOnce]
      readResult must not beNull

      readResult.first must not beNull

      readResult.first.first must beEqualTo("foo")
      readResult.first.second must beEqualTo("bar")
      readResult.first.third must beEqualTo("baz")
    }

    "Support nested mapping of case classes together with simple fields" in new CaseClassScope(
      buildMap(buildMap("foo", "bar", "baz"), 2, 3)) {

      val readResult = readIntoResult[NestedWithFields]
      readResult must not beNull

      readResult.first must not beNull

      readResult.first.first must beEqualTo("foo")
      readResult.first.second must beEqualTo("bar")
      readResult.first.third must beEqualTo("baz")
      readResult.second must beEqualTo(2)
      readResult.third must beEqualTo(3)
    }

    "Support nested optional case classes - positive case" in new CaseClassScope(
      buildMap(buildMap("foo", "bar", "baz"))) {

      val readResult = readIntoResult[NestedOptionally]

      readResult must not beNull

      readResult.first must beSome[AllStrings]

      val c = readResult.first.get

      c.first must beEqualTo("foo")
      c.second must beEqualTo("bar")
      c.third must beEqualTo("baz")
    }

    "Support nested optional case classes - negative case" in new CaseClassScope(
      buildMap()) {

      val readResult = readIntoResult[NestedOptionally]

      readResult must not beNull

      readResult.first must beNone
    }

    "Support double-nested optional case classes" in new CaseClassScope(
      buildMap(
        buildMap("foo", "bar", "baz"),
        buildMap("curly", "larry", "moe"))) {

      val readResult = readIntoResult[StringsAllTheWayDown]

      readResult must not beNull

      readResult.first must beAnInstanceOf[AllStrings]
      readResult.second must beSome[AllStrings]

      val first = readResult.first

      first.first must beEqualTo("foo")
      first.second must beEqualTo("bar")
      first.third must beEqualTo("baz")

      val second = readResult.second.get

      second.first must beEqualTo("curly")
      second.second must beEqualTo("larry")
      second.third must beEqualTo("moe")
    }

    "Support deeply-nested optional case classes - positive case" in new CaseClassScope(
      buildMap(7, 11,
        buildMap(
          buildMap("foo", "bar", "baz"),
          buildMap("curly", "larry", "moe")))) {

      val readResult = readIntoResult[ThreeLevelsDeep]

      readResult must not beNull

      readResult.first must beEqualTo(7)
      readResult.second must beEqualTo(11)
      readResult.third must beSome[StringsAllTheWayDown]

      val nest = readResult.third.get
      nest.first must beAnInstanceOf[AllStrings]
      nest.second must beSome[AllStrings]

      val first = nest.first

      first.first must beEqualTo("foo")
      first.second must beEqualTo("bar")
      first.third must beEqualTo("baz")

      val second = nest.second.get

      second.first must beEqualTo("curly")
      second.second must beEqualTo("larry")
      second.third must beEqualTo("moe")
    }
  }
}
