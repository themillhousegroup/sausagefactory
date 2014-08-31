sausagefactory
==============

Turns Maps into Scala case classes.

## Usage

Assuming the following declarations:

```
   case class MyCaseClass(...)
   ...
   val myMap[String, Anyref] = ...
```

Invoke the `CaseClassConverter` on the map; it'll give you back a `Try` holding the result:

```
   import com.themillhousegroup.sausagefactory.CaseClassConverter

   val conversionResult:Try[MyCaseClass] = CaseClassConverter[MyCaseClass](myMap)

   conversionResult.map { cc =>
      println(s"It all worked! My case class is $cc")
   }

```

## Supported case class structure examples
```
    case class Basic(foo: Int, bar: String, baz: Boolean)

    case class BasicOptions(foo: Option[Int], bar: Option[String])

    case class BasicCollections(foo: Set[String], bar: Seq[Int], baz: Map[String,String])

```

## I have special needs!
Don't we all. If you need to perform some additional adjustment/casting/conversion at any stage during the
sausage-making process (that you don't think would benefit anyone else with a pull request!), there are extension-points
built into the process that will allow you to hook in and change what you need.


