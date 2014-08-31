package com.themillhousegroup.sausagefactory.extensions

import scala.collection.Map

trait MapCanonicalization {

  /**
   * If for any reason the keys or values of the incoming map need some
   * adjustment in order for the conversion to work (for example, if the
   * keys would be illegal identifiers in Java/Scala)
   * then override this method, returning a new Map with corrected data.
   */
  def canonicalize(map: Map[String, AnyRef]): Map[String, AnyRef]
}

trait DefaultMapCanonicalization extends MapCanonicalization {
  def canonicalize(map: Map[String, AnyRef]): Map[String, AnyRef] = map
}

