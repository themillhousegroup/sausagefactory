package com.themillhousegroup.sausagefactory.extensions

import scala.reflect.runtime.universe._

trait FieldConverters {
  def fieldConverter[F](t: Type, v: AnyRef): F
}

trait DefaultFieldConverters extends FieldConverters {
  def fieldConverter[F](t: Type, v: AnyRef) = {
    v.asInstanceOf[F]
  }
}
