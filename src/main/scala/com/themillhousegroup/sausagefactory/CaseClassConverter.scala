package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.extensions.{ DefaultMapCanonicalization, DefaultFieldConverters }
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import scala.collection.immutable.Map
import scala.Predef.String
import scala.util.Try

object CaseClassConverter {

  def apply[T <: Product: TypeTag](
    map: Map[String, Any],
    converterSupplier: => CaseClassConverter = defaultSupplier): Try[T] = Try {
    converterSupplier.buildCaseClass[T](typeOf[T], map)
  }

  private[this] def defaultSupplier = {
    new DefaultCaseClassConverter()
  }
}

protected trait CaseClassConverter {
  def buildCaseClass[T: TypeTag](t: Type, map: Map[String, Any]): T
}

class DefaultCaseClassConverter extends CaseClassConverter with ReflectionHelpers with DefaultMapCanonicalization with DefaultFieldConverters {

  def buildCaseClass[T: TypeTag](t: Type, map: Map[String, Any]): T = {
    rejectIfScoped(t)

    val canonicalMap = canonicalize(map)

    val args = constructorArguments(t).map {
      case (fieldName, fieldType) =>

        if (isOption(fieldType)) {
          matchOptionalField(fieldName, fieldType, canonicalMap.get(fieldName))
        } else {
          matchRequiredField(fieldName, fieldType, canonicalMap.get(fieldName))
        }.asInstanceOf[Object]
    }

    construct(t, args)
  }

  private[this] def rejectIfScoped(t: Type) = {
    val TypeRef(pre, sym, _) = t
    if (pre.toString.contains("this")) { // FIXME OMFG what? Gotta be a better detection mechanism than this...
      throw new UnsupportedOperationException(
        s"Can't create an instance of ${sym} - is it an inner class?")
    }
  }

  protected def matchOptionalField[F](fieldName: String, fieldType: Type, mapValue: Option[Any]): Option[F] = {
    mapValue.fold {
      None.asInstanceOf[Option[F]]
    } { v =>
      // given that fieldType is an Option[X], find what X is...
      val optionTargetType = findOptionTarget(fieldType)
      if (isCaseClass(optionTargetType)) {
        Some(buildCaseClass(optionTargetType, v.asInstanceOf[Map[String, Any]]))
      } else {
        Some(convert(fieldType, v))
      }
    }
  }

  protected def matchRequiredField[F](fieldName: String, fieldType: Type, mapValue: Option[Any]): F = {
    mapValue.fold[F] {
      // Map does NOT contain a field with this keyword
      throw new IllegalArgumentException(s"Non-optional field '${fieldName}' was not found in the given map.")
    } { v =>
      if (isCaseClass(fieldType)) {
        buildCaseClass(fieldType, v.asInstanceOf[Map[String, Any]])
      } else {
        convert(fieldType, v)
      }
    }
  }
}
