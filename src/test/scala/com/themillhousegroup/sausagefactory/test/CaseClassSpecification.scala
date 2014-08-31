package com.themillhousegroup.sausagefactory.test

import org.specs2.mutable.Specification
import java.lang.String
import org.specs2.specification.Scope
import com.themillhousegroup.sausagefactory.CaseClassConverter
import scala.util.Try
import scala.reflect.runtime.universe._

trait CaseClassSpecification {
  this: Specification =>

  class CaseClassScope(m: Map[String, AnyRef]) extends Scope {

    def readInto[T <: Product: TypeTag]: Try[T] = {
      CaseClassConverter[T](m)
    }

    def readIntoResult[T <: Product: TypeTag]: T = {
      readInto[T].get
    }

  }
}
