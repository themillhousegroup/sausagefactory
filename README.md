sausagefactory
==============

Turns Maps into Scala case classes.

[![Build Status](https://travis-ci.org/themillhousegroup/sausagefactory.svg?branch=master)](https://travis-ci.org/themillhousegroup/sausagefactory)

## Getting Started

#### Including the dependency
Bring in the library by adding the following to your ```build.sbt```. 

  - The release repository: 

```
   resolvers ++= Seq(
     "millhouse-releases" at "http://repository-themillhousegroup.forge.cloudbees.com/release"
   )
```
  - The dependency itself: 

```
   libraryDependencies ++= Seq(
     "com.themillhousegroup" %% "sausagefactory" % "0.4.0"
   )

```

#### A note on Scala versions
For versions up to 0.3.x: the library is cross-built for Scala 2.10.4 and 2.11.2, but 2.11.2 is _highly_ recommended; a number of reflection features are used and `scala-reflect` seems to be much more reliable in Scala 2.11.

As of version __0.4.0__ only Scala 2.11 is supported. 


## Usage

Assuming the following declarations:

```
   case class MyCaseClass(...)
   ...
   val myMap[String, Any] = ...
```

Invoke the `CaseClassConverter` on the map; it'll give you back a `Try` holding the result:

```
   import com.themillhousegroup.sausagefactory.CaseClassConverter

   val conversionResult:Try[MyCaseClass] = CaseClassConverter[MyCaseClass](myMap)

   conversionResult.map { cc =>
      println(s"It all worked! My case class is $cc")
   }

```

### How are fields mapped?
We're looking for exact-matches between the keys of the _map_ and the field names of the _target case class_.

 - Fields in the _map_ not found in the _target case class_ will be ignored.

 - Fields in the _target case class_ MUST be present in the _map_, unless they are `Option` types, in which case they will be set to None.

If you're having trouble getting things to line up (and you aren't able to change the _map_ or the _case class_ definition to compensate), check out the ***extension mechanisms*** explained at the bottom of this document.

### Supported case class structure examples
```
    case class Basic(foo: Int, bar: String, baz: Boolean)

    case class BasicOptions(foo: Option[Int], bar: Option[String])

    case class BasicCollections(foo: Set[String], bar: Seq[Int], baz: Map[String,String])

    case class NestedCaseClasses( foo: SomeCaseClass, bar: SomeOtherCaseClass)

    case class NestedOptionalCaseClass (foo: Option[Basic], bar: Option[Basic])
    
    case class IterablesOfCaseClass (foo: Set[Basic], bar: Seq[Basic], baz: List[Basic])

    case class MapOfCaseClass (foo: Map[String, Basic], bar: Map[Int, Basic])
```
The above are just simple examples; Your case classes can be as deeply-nested as you like - Sausagefactory will recurse down as far as needed to instantiate nested objects.


## I have special needs!
Don't we all. If you need to perform some additional adjustment/casting/conversion during the sausage-making process (that you don't think would benefit anyone else with a [pull request](https://github.com/themillhousegroup/sausagefactory/pulls)!), there is an extension-point
built into the process that will allow you to hook in and change what you need.



If you look at the signature for the `CaseClassConverter` object's `apply` method, you'll see that there is an optional second argument:

```
object CaseClassConverter {

  def apply[T <: Product: TypeTag](map: Map[String, Any]): Try[T] 
  ...

  def apply[T](	map: Map[String, Any],
    			converter: => FieldConverter): Try[T]
    
    ...
}
``` 

Where `FieldConverter` is a `PartialFunction` defined as follows:

```
type FieldConverter = PartialFunction[(Type, Any), Any]
```

The `FieldConverter` gets invoked once we've found a match between a
case class fieldname and a key in the incoming map. 

You might want to provide your own if (for example) you're getting a type mismatch because the map has a value of type `Long` but your case class expects an `Int`.

When you see `PartialFunction` you just need to read "match expression" - just provide a `case` for the particular situation you need to intercept; e.g.:

```
import com.themillhousegroup.sausagefactory.reflection.ReflectionHelpers
import com.themillhousegroup.sausagefactory.CaseClassConverter.FieldConverter

val alwaysMakeJavaLongsIntoInts: FieldConverter = {
    case (t: Type, v: Any) if (isInt(t) && isJLong(v.getClass)) => {
      v.asInstanceOf[Long].toInt
    }
  }
```

For more info, see the Example / test spec: [CustomFieldConverterExampleSpec](https://github.com/themillhousegroup/sausagefactory/blob/master/src/test/scala/com/themillhousegroup/sausagefactory/CustomFieldConverterExampleSpec.scala)


