package com.themillhousegroup.sausagefactory

import org.specs2.mutable.Specification
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import scala.reflect.runtime.universe._
import com.themillhousegroup.sausagefactory.test.CaseClassFixtures._
import com.themillhousegroup.sausagefactory.CaseClassConverter.FieldConverter

class CustomFieldConverterExampleSpec extends Specification with ReflectionHelpers {

  val alwaysMakeJavaLongsIntoInts: FieldConverter = {
    case (t: Type, v: Any) if (isInt(t) && isJLong(v.getClass)) => {
      v.asInstanceOf[Long].toInt
    }
  }

  "FieldConverters extension point (JLong->Int autoconverter)" should {

    "Support automatic mapping of java.lang.Longs to Ints" in {

      val map = buildMap(new java.lang.Long(99), new java.lang.Long(77))

      val result = CaseClassConverter[IntsNotLongs](map, alwaysMakeJavaLongsIntoInts)

      val readResult = result.get
      readResult must not beNull

      readResult.first must beSome[Int](99)
      readResult.second must beEqualTo[Int](77)
    }
  }

}
