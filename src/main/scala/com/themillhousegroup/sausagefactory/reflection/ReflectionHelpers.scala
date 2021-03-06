package com.themillhousegroup.sausagefactory.reflection

import java.lang.Class
import scala.reflect.runtime.universe._
import java.lang.reflect.Constructor

trait ReflectionHelpers extends TypeSymbols {

  val m = runtimeMirror(getClass.getClassLoader)

  def hasClass(t: Type, desired: Symbol): Boolean = t.baseClasses.exists(_ == desired)

  def isCaseClass(t: Type): Boolean =
    t.baseClasses.exists {
      case cs: ClassSymbol => cs.isCaseClass
    }

  def isOption(t: Type) = hasClass(t, optionType)
  def isIterableOfMaps(t: Type) = hasClass(t, iterableOfMaps)
  def isMapOfMaps(t: Type) = hasClass(t, mapOfMaps)

  def isInt(t: Type) = hasClass(t, intType)

  def isJLong(fieldType: Class[_]) = {
    fieldType.isAssignableFrom(classOf[java.lang.Long])
  }

  /** For container types like Option[T], List[T], finds T */
  def findContainerClassTarget(t: Type): Type = {
    t.typeArgs.head
  }

  /** For map types like Map[A, B], finds B */
  def findMapClassTarget(t: Type): Type = {
    t.typeArgs(1)
  }

  def constructorArguments(t: Type): List[(String, Type)] = {
    val constructor = t.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get

    val constructorArgs = constructor.paramss.head

    constructorArgs.map { field =>
      val fieldName = field.name.decoded
      val fieldType = field.typeSignature
      fieldName -> fieldType
    }
  }

  def constructor[_](m: RuntimeMirror, t: Type): Constructor[_] = {
    val c = m.runtimeClass(t.typeSymbol.asClass)
    c.getConstructors()(0)
  }

  def construct[T](t: Type, args: List[Object]) = {
    constructor(m, t).newInstance(args.toArray: _*).asInstanceOf[T]
  }
}
