package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import com.themillhousegroup.sausagefactory.extensions.FieldConverters
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._

trait AlwaysMakeJavaLongsIntoInts extends FieldConverters with ReflectionHelpers {
  override def fieldConverter[F](t: Type, v: Any): F = {
    if (isInt(t) && isJLong(v.getClass)) {
      v.asInstanceOf[Long].toInt.asInstanceOf[F]
    } else {
      v.asInstanceOf[F]
    }
  }
}

class CustomLongConverter extends DefaultCaseClassConverter with AlwaysMakeJavaLongsIntoInts

class ExtensionExampleSpec extends Specification {

  "FieldConverters extension point (JLong->Int autoconverter)" should {

    "Support automatic mapping of java.lang.Longs to Ints" in {

      val result = CaseClassConverter[IntsNotLongs](
        buildMap(
          new java.lang.Long(99),
          new java.lang.Long(77)), new CustomLongConverter())
      val readResult = result.get
      readResult must not beNull

      readResult.first must beSome[Int](99)
      readResult.second must beEqualTo[Int](77)
    }
  }

}
