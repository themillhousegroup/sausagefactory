sausagefactory
==============

Turns Maps into Scala case classes.

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

Fields in the _map_ not found in the _target case class_ will be ignored.

Fields in the _target case class_ MUST be present in the _map_, unless they are `Option` types, in which case they will be set to None.

If you're having trouble getting things to line up (and you aren't able to change the _map_ or the _case class_ definition to compensate), check out the ***extension mechanisms*** explained at the bottom of this document.

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

Currently there are two "extension" traits whereby you can mix in custom implementations:

#### `MapCanonicalization` 
 - Function `canonicalize()` gets invoked whenever a `Map` is about to be transformed.
 - Provide your own if (for example) you need keys in the map to be adjusted in order
for them to be Java/Scala-legal field names in a case class.
 - Example / test spec: [MapCanonicalizationExtensionExampleSpec](https://github.com/themillhousegroup/sausagefactory/blob/master/src/test/scala/com/themillhousegroup/sausagefactory/MapCanonicalizationExtensionExampleSpec.scala) 

#### `FieldConverters` 
 - Function `convert()`gets invoked once we've found a match between a
case class fieldname and a key in the incoming map. 
 - Provide your own if
(for example) you're getting a type mismatch because the map has a value of type `Long` but your case class expects an `Int`.
- Example / test spec: [FieldConverterExtensionExampleSpec](https://github.com/themillhousegroup/sausagefactory/blob/master/src/test/scala/com/themillhousegroup/sausagefactory/FieldConverterExtensionExampleSpec.scala)


