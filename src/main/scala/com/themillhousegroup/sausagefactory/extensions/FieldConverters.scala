package com.themillhousegroup.sausagefactory.extensions

import scala.reflect.runtime.universe._

trait FieldConverters {
  def convert[F](t: Type, v: Any): F
}

trait DefaultFieldConverters extends FieldConverters {
  def convert[F](t: Type, v: Any) = {
    v.asInstanceOf[F]
  }
}
