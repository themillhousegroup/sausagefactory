package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._

object AlwaysMakeJavaLongsIntoInts extends FieldConverter with ReflectionHelpers {
  override def convert[F](t: Type, v: Any): F = {
    if (isInt(t) && isJLong(v.getClass)) {
      v.asInstanceOf[Long].toInt.asInstanceOf[F]
    } else {
      v.asInstanceOf[F]
    }
  }
}

class CustomFieldConverterExampleSpec extends Specification {

  "FieldConverters extension point (JLong->Int autoconverter)" should {

    "Support automatic mapping of java.lang.Longs to Ints" in {

      val map = buildMap(new java.lang.Long(99), new java.lang.Long(77))

      val result = CaseClassConverter[IntsNotLongs](map, AlwaysMakeJavaLongsIntoInts)

      val readResult = result.get
      readResult must not beNull

      readResult.first must beSome[Int](99)
      readResult.second must beEqualTo[Int](77)
    }
  }

}
