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

### How are fields mapped?
Fields in the map not found in the target case class will be ignored.

Fields in the target class MUST be present in the map, unless they are `Option` types, in which case they will be set to None.

### Supported case class structure examples
```
    case class Basic(foo: Int, bar: String, baz: Boolean)

    case class BasicOptions(foo: Option[Int], bar: Option[String])

    case class BasicCollections(foo: Set[String], bar: Seq[Int], baz: Map[String,String])

    case class NestedCaseClasses( foo: SomeCaseClass, bar: SomeOtherCaseClass)

    case class NestedOptionalCaseClass (foo: Option[Basic], bar: Option[Basic])

```

## I have special needs!
Don't we all. If you need to perform some additional adjustment/casting/conversion at any stage during the
sausage-making process (that you don't think would benefit anyone else with a pull request!), there are extension-points
built into the process that will allow you to hook in and change what you need.

Currently there are two traits that you can mix in implementations of your own:

`MapCanonicalization` - gets invoked whenever a `Map` is about to be transformed.
Provide your own if (for example) you need keys in the map to be adjusted in order
for them to be Java/Scala-legal field names in a case class.

`FieldConverters` - gets invoked once we've found a match between a
case class field and a key in the incoming map. Provide your own if
(for example) you're getting a type mismatch because the map has a value
of type `Long` but your case class expects an `Int`.


