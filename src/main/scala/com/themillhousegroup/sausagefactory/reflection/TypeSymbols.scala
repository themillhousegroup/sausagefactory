package com.themillhousegroup.sausagefactory.reflection

import scala.collection.GenTraversableOnce

import scala.reflect.runtime.universe._

trait TypeSymbols {
  lazy val productTrait = typeOf[Product].typeSymbol
  lazy val traversableOnceTrait = typeOf[TraversableOnce[_]].typeSymbol
  lazy val traversableOnceOfMaps = typeOf[TraversableOnce[Map[String, _]]].typeSymbol
  lazy val listOfMaps = typeOf[List[Map[String, _]]].typeSymbol
  lazy val seqOfMaps = typeOf[Seq[Map[String, _]]].typeSymbol
  lazy val genTraversableOnceTrait = typeOf[GenTraversableOnce[_]].typeSymbol
  lazy val optionType = typeOf[Option[_]].typeSymbol
  lazy val intType = typeOf[Int].typeSymbol
}
