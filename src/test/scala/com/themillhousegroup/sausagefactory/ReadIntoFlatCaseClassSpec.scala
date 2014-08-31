package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import java.lang.{ IllegalArgumentException, UnsupportedOperationException }
import scala.Predef.String
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification
import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._

class ReadIntoFlatCaseClassSpec extends Specification with CaseClassSpecification {

  case class CannotCreate(first: String, second: String)

  "Reading maps into case classes - flat structures -" should {

    "Reject a case class that won't be instantiable" in new CaseClassScope(
      buildMap("a", "b")) {

      readInto[CannotCreate] must beAFailedTry[CannotCreate].withThrowable[UnsupportedOperationException]
    }

    "Support single-level mapping of simple strings" in new CaseClassScope(
      buildMap("foo", "bar", "baz")) {

      val readResult = readIntoResult[AllStrings]
      readResult must not beNull

      readResult.first must beEqualTo("foo")
      readResult.second must beEqualTo("bar")
      readResult.third must beEqualTo("baz")

    }

    "Return a failed Try: IllegalArgumentException if a field is missing" in new CaseClassScope(
      buildMap("foo", "bar")) {

      readInto[AllStrings] must beAFailedTry[AllStrings].withThrowable[IllegalArgumentException]
    }

    "Support single-level mapping of optional strings - present" in new CaseClassScope(
      buildMap("foo", "bar", "baz")) {

      val readResult: OptionalStrings = readIntoResult[OptionalStrings]
      readResult must not beNull

      readResult.first must beEqualTo("foo")
      readResult.second must beEqualTo("bar")
      readResult.third must beSome("baz")
    }

    "Support single-level mapping of optional strings - absent" in new CaseClassScope(
      buildMap("foo", "bar")) {

      val readResult: OptionalStrings = readIntoResult[OptionalStrings]
      readResult must not beNull

      readResult.first must beEqualTo("foo")
      readResult.second must beEqualTo("bar")
      readResult.third must beNone
    }

    //
    //    "Support single-level mapping of mixed types" in new CaseClassScope(
    //      """ :first "foo" :third 6 :second 9 """) {
    //
    //      val readResult = readIntoResult[MixedBunch]
    //      readResult must not beNull
    //
    //      readResult.first must beEqualTo("foo")
    //      readResult.third must beSome(6)
    //      readResult.second must beEqualTo(9)
    //    }
    //
    //    "Support single-level mapping where a member is a list" in new CaseClassScope(
    //      """ :first "foo" :third ("x" "y" "z") :second 9 """) {
    //
    //      val readResult = readIntoResult[BasicWithList]
    //      readResult must not beNull
    //
    //      readResult.first must beEqualTo("foo")
    //      readResult.third must containTheSameElementsAs(Seq("x", "y", "z"))
    //      readResult.second must beEqualTo(9)
    //    }
    //
    //    "Support single-level mapping where a member is a vector" in new CaseClassScope(
    //      """ :first "foo" :third ["x" "y" "z"] :second 9 """) {
    //
    //      val readResult = readIntoResult[BasicWithList]
    //      readResult must not beNull
    //
    //      readResult.first must beEqualTo("foo")
    //      readResult.third must containTheSameElementsAs(Seq("x", "y", "z"))
    //      readResult.second must beEqualTo(9)
    //    }
    //
    //    "Support single-level mapping where a member is a set" in new CaseClassScope(
    //      """ :first "foo" :third #{"x" "y" "z"} :second 9 """) {
    //
    //      val readResult = readIntoResult[BasicWithSet]
    //      readResult must not beNull
    //
    //      readResult.first must beEqualTo("foo")
    //      readResult.third must containTheSameElementsAs(Seq("x", "y", "z"))
    //      readResult.second must beEqualTo(9)
    //    }
    //
    //    "Support single-level mapping where a member is a map" in new CaseClassScope(
    //      """ :first "foo" :third {:x "eks" :y "wye" :z "zed" } :second 9 """) {
    //
    //      val readResult = readIntoResult[BasicWithMap]
    //      readResult must not beNull
    //
    //      readResult.first must beEqualTo("foo")
    //      readResult.third must havePairs("x" -> "eks", "y" -> "wye", "z" -> "zed")
    //      readResult.second must beEqualTo(9)
    //    }
  }
}