package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import com.themillhousegroup.sausagefactory.extensions.{ MapCanonicalization, FieldConverters }
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import scala.Predef._
import scala.collection.immutable.Map

/**
 * A canonicalizer that solves a hypothetical situation where
 * the incoming map has keys that are UPPERCASE and we need
 * it to be mapped to a case class with lowercase fieldnames.
 */
trait MakeMapKeysLowerCase extends MapCanonicalization {
  override def canonicalize(map: Map[String, Any]): Map[String, Any] = {
    map.map {
      case (k, v) =>
        (k.toLowerCase, v)
    }.toMap
  }
}

class CustomMapConverter extends DefaultCaseClassConverter with MakeMapKeysLowerCase

class MapCanonicalizationExtensionExampleSpec extends Specification {

  "MapCanonicalization extension point (map-key-lowercaser)" should {

    "Allow a map with UPPERCASE keys to be mapped to a normal case class" in {

      val shoutyMap = Map(
        ("FIRST", "one"),
        ("SECOND", "two"),
        ("THIRD", "three")
      )

      val result = CaseClassConverter[AllStrings](
        shoutyMap, new CustomMapConverter())
      val readResult = result.get
      readResult must not beNull

      readResult.first must beEqualTo("one")
      readResult.second must beEqualTo("two")
      readResult.third must beEqualTo("three")
    }
  }

}
