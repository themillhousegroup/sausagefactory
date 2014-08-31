package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import scala.util.Try
import scala.reflect.runtime.universe._
import scala.Product
import java.lang.{IllegalArgumentException, UnsupportedOperationException, String}
import scala.Predef.String
import com.themillhousegroup.sausagefactory.test.CaseClassSpecification

class ReadIntoFlatCaseClassSpec extends Specification with CaseClassSpecification {

  case class CannotCreate(x: String, y: String)

  "Reading EDN into case classes - flat structures -" should {

    "Reject a case class that won't be instantiable" in new CaseClassScope(
      """ :x "foo" :y "bar" """) {

      readInto[CannotCreate] must beAFailedTry[CannotCreate].withThrowable[UnsupportedOperationException]
    }

    "Support single-level mapping of simple strings" in new CaseClassScope(
      """ :bish "foo" :bash "bar" :bosh "baz" """) {

      val readResult = readIntoResult[AllStrings]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must beEqualTo("bar")

      readResult.bosh must beEqualTo("baz")
    }

    "Return a failed Try: IllegalArgumentException if a field is missing" in new CaseClassScope(
      """ :bish "foo" :bash "bar"  """) {

      readInto[AllStrings] must beAFailedTry[AllStrings].withThrowable[IllegalArgumentException]
    }

    "Support single-level mapping of optional strings - present" in new CaseClassScope(
      """ :bish "foo" :bash "bar" :bosh "baz" """) {

      val readResult: OptionalStrings = readIntoResult[OptionalStrings]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must beSome("bar")
      readResult.bosh must beEqualTo("baz")
    }

    "Support single-level mapping of optional strings - absent" in new CaseClassScope(
      """ :bish "foo" :bosh "baz" """) {

      val readResult: OptionalStrings = readIntoResult[OptionalStrings]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must beNone
      readResult.bosh must beEqualTo("baz")
    }

    "Support automatic mapping of Longs to Ints" in new CaseClassScope(
      """ :bash 6 :bosh 9 """) {

      val readResult = readIntoResult[IntsNotLongs]
      readResult must not beNull

      readResult.bash must beSome(6)
      readResult.bosh must beEqualTo(9)
    }

    "Support Longs in case classes" in new CaseClassScope(
      """ :bash 6 :bosh 9 """) {

      val readResult = readIntoResult[AllLongs]
      readResult must not beNull

      readResult.bash must beSome(6)
      readResult.bosh must beEqualTo(9)
    }

    "Support single-level mapping of mixed types" in new CaseClassScope(
      """ :bish "foo" :bash 6 :bosh 9 """) {

      val readResult = readIntoResult[MixedBunch]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must beSome(6)
      readResult.bosh must beEqualTo(9)
    }

    "Support single-level mapping where a member is a list" in new CaseClassScope(
      """ :bish "foo" :bash ("x" "y" "z") :bosh 9 """) {

      val readResult = readIntoResult[BasicWithList]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must containTheSameElementsAs(Seq("x", "y", "z"))
      readResult.bosh must beEqualTo(9)
    }

    "Support single-level mapping where a member is a vector" in new CaseClassScope(
      """ :bish "foo" :bash ["x" "y" "z"] :bosh 9 """) {

      val readResult = readIntoResult[BasicWithList]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must containTheSameElementsAs(Seq("x", "y", "z"))
      readResult.bosh must beEqualTo(9)
    }

    "Support single-level mapping where a member is a set" in new CaseClassScope(
      """ :bish "foo" :bash #{"x" "y" "z"} :bosh 9 """) {

      val readResult = readIntoResult[BasicWithSet]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must containTheSameElementsAs(Seq("x", "y", "z"))
      readResult.bosh must beEqualTo(9)
    }

    "Support single-level mapping where a member is a map" in new CaseClassScope(
      """ :bish "foo" :bash {:x "eks" :y "wye" :z "zed" } :bosh 9 """) {

      val readResult = readIntoResult[BasicWithMap]
      readResult must not beNull

      readResult.bish must beEqualTo("foo")
      readResult.bash must havePairs("x" -> "eks", "y" -> "wye", "z" -> "zed")
      readResult.bosh must beEqualTo(9)
    }
  }
