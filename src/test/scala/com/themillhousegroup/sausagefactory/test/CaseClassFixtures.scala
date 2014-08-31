package com.themillhousegroup.sausagefactory.test

import scala.Predef.String
import scala.collection.immutable.Set
import scala.collection.immutable.Map

object CaseClassFixtures {

  // Basic case classes:
  case class AllStrings(first: String, second: String, third: String)
  case class OptionalStrings(first: String, second: String, third: Option[String])
  case class AllLongs(first: Option[Long], second: Long)
  case class IntsNotLongs(first: Option[Int], second: Int)
  case class MixedBunch(first: String, second: Option[Int], third: Int)

  // Basic case classes with collections of simple types as members:
  case class BasicWithList(first: String, second: List[String], third: Int)
  case class BasicWithSet(first: String, second: Set[String], third: Int)
  case class BasicWithMap(first: String, second: Map[String, Int], third: Int)

  // Case classes with another level of case classes within
  case class NestedJustOnce(first: AllStrings)
  case class NestedWithFields(first: AllStrings, second: Int, third: Int)
  case class NestedOptionally(first: Option[AllStrings])

  // Multiply-nested case class
  case class StringsAllTheWayDown(first: AllStrings, second: Option[AllStrings])
  case class ThreeLevelsDeep(first: Int, second: Int, third: Option[StringsAllTheWayDown])

  val keys = Seq("first", "second", "third")

  def buildMap(values: Any*): Map[String, Any] = {
    keys.zip(values).toMap
  }

}
