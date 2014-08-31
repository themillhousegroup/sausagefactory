package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.extensions.{ DefaultMapCanonicalization, DefaultFieldConverters }
import _root_.com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import scala.collection.Map
import scala.Predef.String
import scala.util.Try

object CaseClassConverter {

  val m = runtimeMirror(getClass.getClassLoader)

  def apply[T <: Product: TypeTag](
    map: Map[String, AnyRef],
    converterSupplier: => CaseClassConverter = defaultSupplier): Try[T] = Try {
    converterSupplier.buildCaseClass[T](typeOf[T], map)
  }

  private[this] def defaultSupplier = {
    new DefaultCaseClassConverter()
  }
}

protected abstract class CaseClassConverter {
  def buildCaseClass[T: TypeTag](t: Type, map: Map[String, AnyRef]): T
}

class DefaultCaseClassConverter extends CaseClassConverter with ReflectionHelpers with DefaultMapCanonicalization with DefaultFieldConverters {

  import CaseClassConverter._

  def buildCaseClass[T: TypeTag](t: Type, map: Map[String, AnyRef]): T = {
    rejectIfScoped(t)

    val canonicalMap = canonicalize(map)

    val constructor = t.declarations.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get

    val constructorArgs = constructor.paramss.head

    val args = constructorArgs.map { field =>
      val fieldName = field.name.decoded
      val fieldType = field.typeSignature

      if (isOption(fieldType)) {
        matchOptionalField(fieldName, fieldType, canonicalMap.get(fieldName))
      } else {
        matchRequiredField(fieldName, fieldType, canonicalMap.get(fieldName))
      }.asInstanceOf[Object]
    }.toArray

    val c = m.runtimeClass(t.typeSymbol.asClass)

    c.getConstructors()(0).newInstance(args: _*).asInstanceOf[T]
  }

  private[this] def rejectIfScoped(t: Type) = {
    val TypeRef(pre, sym, _) = t
    if (pre.toString.contains("this")) { // FIXME OMFG what? Gotta be a better detection mechanism than this...
      throw new UnsupportedOperationException(
        s"Can't create an instance of ${sym} - is it an inner class?")
    }
  }

  protected def matchOptionalField[F](fieldName: String, fieldType: Type, mapValue: Option[AnyRef]): Option[F] = {
    mapValue.fold {
      None.asInstanceOf[Option[F]]
    } { v =>
      // given that fieldType is an Option[X], find what X is...
      val optionTargetType = findOptionTarget(fieldType)
      if (isCaseClass(optionTargetType)) {
        Some(buildCaseClass(optionTargetType, v.asInstanceOf[Map[String, AnyRef]]))
      } else {
        Some(fieldConverter(fieldType, v))
      }
    }
  }

  protected def matchRequiredField[F](fieldName: String, fieldType: Type, mapValue: Option[AnyRef]): F = {
    mapValue.fold[F] {
      // Map does NOT contain a field with this keyword
      throw new IllegalArgumentException(s"Non-optional field '${fieldName}' was not found in the given map.")
    } { v =>
      if (isCaseClass(fieldType)) {
        buildCaseClass(fieldType, v.asInstanceOf[Map[String, AnyRef]])
      } else {
        fieldConverter(fieldType, v)
      }
    }
  }
}
