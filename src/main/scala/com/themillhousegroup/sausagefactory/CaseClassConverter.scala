package com.themillhousegroup.sausagefactory

import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import scala.collection.immutable.Map
import scala.Predef._
import scala.util.Try

object CaseClassConverter {

  def apply[T <: Product: TypeTag](
    map: Map[String, Any],
    converter: => FieldConverter = defaultFieldConverter): Try[T] = Try {
    new CaseClassConverter(converter).buildCaseClass[T](typeOf[T], map)
  }

  // TODO: Use of a PartialFunction so that this default case
  // can be orElse'd after any custom converters?
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

      if (isSeqOfMaps(fieldType)) {

        println(s"Seq $fieldType, mapValue: $mapValue")
        val targetType = findContainerClassTarget(fieldType)
        if (isCaseClass(targetType)) {
          convertSeq(targetType, mapValue.asInstanceOf[Seq[Map[String, Any]]])
        } else {
          fc.convert(fieldType, mapValue)
        }
      } else {
        fc.convert(fieldType, mapValue)
      }
    }
  }

  protected def convertSeq[C[T] <: Seq[T]](targetType: Type, v: C[Map[String, Any]]): Seq[Product] = {
    v.map { innerMap =>
      buildCaseClass[Product](targetType, innerMap)
    }
  }
}
