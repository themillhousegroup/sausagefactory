package com.themillhousegroup.sausagefactory.reflection

import java.lang.Class
import scala.reflect.runtime.universe._

trait ReflectionHelpers extends TypeSymbols {
  def hasClass(t: Type, desired: Symbol): Boolean = t.baseClasses.exists(_ == desired)

  def isCaseClass(t: Type): Boolean =
    t.baseClasses.exists {
      case cs: ClassSymbol => cs.isCaseClass
    }

  def isOption(t: Type) = hasClass(t, optionType)

  def isInt(t: Type) = hasClass(t, intType)

  def isJLong(fieldType: Class[_]) = {
    fieldType.isAssignableFrom(classOf[java.lang.Long])
  }

  def findOptionTarget(t: Type) = {
    t.typeArgs.head
  }

  def constructorArguments(t: Type): List[Symbol] = {
    val constructor = t.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get

    val constructorArgs = constructor.paramss.head

    constructorArgs
  }
}
