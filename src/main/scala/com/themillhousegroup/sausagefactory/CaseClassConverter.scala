package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import scala.collection.immutable.Map
import scala.Predef._
import scala.util.Try
import com.themillhousegroup.sausagefactory.CaseClassConverter.FieldConverter

object CaseClassConverter {

  type FieldConverter = PartialFunction[(Type, Any), Any]

  def apply[T <: Product: TypeTag](map: Map[String, Any]): Try[T] = Try {
    new CaseClassConverter(defaultFieldConverter).buildCaseClass[T](typeOf[T], map)
  }

  def apply[T <: Product: TypeTag](
    map: Map[String, Any],
    converter: => FieldConverter): Try[T] = Try {
    new CaseClassConverter(converter orElse defaultFieldConverter).buildCaseClass[T](typeOf[T], map)
  }

  private[this] val defaultFieldConverter: FieldConverter = {
    case (t: Type, v: Any) => v
  }
}

class CaseClassConverter(fc: FieldConverter) extends ReflectionHelpers {

  def buildCaseClass[T: TypeTag](t: Type, map: Map[String, Any]): T = {
    rejectIfScoped(t)

    val args = constructorArguments(t).map {
      case (fieldName, fieldType) =>

        if (isOption(fieldType)) {
          matchOptionalField(fieldName, fieldType, map.get(fieldName))
        } else {
          matchRequiredField(fieldName, fieldType, map.get(fieldName))
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

  protected def matchOptionalField(fieldName: String, fieldType: Type, mapValue: Option[Any]): Option[Any] = {
    mapValue.fold {
      None.asInstanceOf[Option[Any]]
    } { v =>
      // given that fieldType is an Option[X], find what X is...
      val optionTargetType = findContainerClassTarget(fieldType)
      Some(matchField(optionTargetType, v))
    }
  }

  protected def matchRequiredField(fieldName: String, fieldType: Type, mapValue: Option[Any]): Any = {
    mapValue.fold {
      // Map does NOT contain a field with this keyword
      throw new IllegalArgumentException(s"Non-optional field '${fieldName}' was not found in the given map.")
    } { v =>
      matchField(fieldType, v)
    }
  }

  protected def matchField(fieldType: Type, mapValue: Any): Any = {
    if (isCaseClass(fieldType)) {
      buildCaseClass(fieldType, mapValue.asInstanceOf[Map[String, Any]])
    } else {

      if (isIterableOfMaps(fieldType)) {

        println(s"Iterable $fieldType, mapValue: $mapValue")
        val targetType = findContainerClassTarget(fieldType)
        if (isCaseClass(targetType)) {
          convertIterable(targetType, mapValue.asInstanceOf[Iterable[Map[String, Any]]])
        } else {
          fc(fieldType, mapValue)
        }
      } else {
        fc(fieldType, mapValue)
      }
    }
  }

  protected def convertIterable[C[T] <: Iterable[T]](targetType: Type, v: C[Map[String, Any]]): Iterable[Product] = {
    v.map { innerMap =>
      buildCaseClass[Product](targetType, innerMap)
    }
  }
}
