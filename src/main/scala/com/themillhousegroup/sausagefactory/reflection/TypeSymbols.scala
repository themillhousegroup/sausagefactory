package com.themillhousegroup.sausagefactory.reflection

import scala.collection.GenTraversableOnce

import scala.reflect.runtime.universe._

trait TypeSymbols {
  lazy val productTrait = typeOf[Product].typeSymbol
  lazy val genTraversableOnceTrait = typeOf[GenTraversableOnce[_]].typeSymbol
  lazy val optionType = typeOf[Option[_]].typeSymbol
  lazy val intType = typeOf[Int].typeSymbol
}
