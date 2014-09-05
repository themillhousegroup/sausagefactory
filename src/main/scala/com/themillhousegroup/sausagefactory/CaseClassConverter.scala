package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import scala.collection.immutable.Map
import scala.Predef._
import scala.util.Try
import scala.collection.GenTraversableOnce

object CaseClassConverter {

  def apply[T <: Product: TypeTag](
    map: Map[String, Any],
    converter: => FieldConverter = defaultFieldConverter): Try[T] = Try {
    new CaseClassConverter(converter).buildCaseClass[T](typeOf[T], map)
  }

  private[this] val defaultFieldConverter = new FieldConverter {
    def convert[F](t: Type, v: Any) = {
      v.asInstanceOf[F]
    }
  }
}

trait FieldConverter {
  def convert[F](t: Type, v: Any): F
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

  protected def matchOptionalField[F](fieldName: String, fieldType: Type, mapValue: Option[Any]): Option[F] = {
    mapValue.fold {
      None.asInstanceOf[Option[F]]
    } { v =>
      // given that fieldType is an Option[X], find what X is...
      val optionTargetType = findContainerClassTarget(fieldType)
      if (isCaseClass(optionTargetType)) {
        Some(buildCaseClass(optionTargetType, v.asInstanceOf[Map[String, Any]]))
      } else {
        Some(fc.convert(fieldType, v))
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
        if (isTraversableOfMaps(fieldType)) {

          println(s"Traversable $fieldType, mapValue: $v")
          val targetType = findContainerClassTarget(fieldType)
          println(s"Target: $targetType")
          val asList = v.asInstanceOf[TraversableOnce[Map[String, Any]]]
          println(s"asList: $asList")

          val converted = asList.map { innerMap =>
            println(s"InnerMap: $innerMap")
            val bcc = buildCaseClass(targetType, innerMap)
            println(s"BCC: $bcc")
            bcc
          }

          println(s"conv: ${converted.mkString}")
          //.asInstanceOf[F]

          fc.convert(fieldType, v)
        } else {
          fc.convert(fieldType, v)
        }
      }
    }
  }
}
