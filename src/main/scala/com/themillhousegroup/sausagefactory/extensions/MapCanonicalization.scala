package com.themillhousegroup.sausagefactory.extensions

import scala.collection.immutable.Map

trait MapCanonicalization {

  /**
   * If for any reason the keys or values of the incoming map need some
   * adjustment in order for the conversion to work (for example, if the
   * keys would be illegal identifiers in Java/Scala)
   * then override this method, returning a new Map with corrected data.
   */
  def canonicalize(map: Map[String, Any]): Map[String, Any]
}

trait DefaultMapCanonicalization extends MapCanonicalization {
  def canonicalize(map: Map[String, Any]): Map[String, Any] = map
}

