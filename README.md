<div align="center">it's all about the environment</div>

#

Envi is a flexible, multi-format and simple configuration library designed in Kotlin-Java that uses Reflection to produce a magnificent yet 
simple way to set up configuration for either your Java or Kotlin projects.

To get started with Envi, you must create an instance of it by selecting an adapter that you want to use. In this example, we'll use the `SimpleDotenvAdapter`:
```kotlin
Envi.createConfigurator(SimpleDotenvAdapter)
```

After creating the instance, we have to create our configuration class which is as simple as:
```kotlin
object Configuration {
    lateinit var hello: String
}
```
```java
class Configuration {
    public static String hello;
}
```

Once the configuration has been created, we also need to introduce our configuration file. Although, Envi also supports sending the contents of the configuration 
immediately onto the instance, for cases such as cluster-sync configurations, or anything closer. In our case, we'll create a simple `.env` configuration:
```dotenv
hello=world
```

And now, we can do the final part which is allowing Envi to read the file:
```kotlin
Envi.createConfigurator(SimpleDotenvAdapter)
    .read(File(".env"), Configuration)
```
```java
Envi.createConfigurator(SimpleDotenvAdapter)
        .read(File(".env"), Configuration.class);
```

And now the class should be configured with the proper values, but we can customize it even more. 

### Annotation-based features.
You can further add more details into how the configuration should be with the annotations that Envi supports, you can view 
each feature by opening the summaries below.

<details>
    <summary>Alternatively, or different keys</summary>
```kotlin
    object Configuration {
        @Alternatively(name = "hello")
        lateinit var world: String
    }
```
```dotenv
    hello=world
```
</details>
<details>
    <summary>Regex validation</summary>
```kotlin
    object Configuration {
        @Regex(pattern = "world")
        lateinit var hello: String
    }
```
```dotenv
    hello=world
```
</details>
<details>
    <summary>Required field</summary>
```kotlin
    object Configuration {
        // throws an exception in this example
        @Required
        lateinit var world: String
    }
```
```dotenv
    hello=
```
</details>
<details>
    <summary>Validatable field</summary>

```kotlin
    object Configuration {
        @Validatable(with = "envi.world")
        lateinit var world: String
    }

    fun main {
        Envi.validators["envi.world"] = EnviValidator { contents -> contents.equals("world") }
    }
```
```dotenv
    hello=world
```

</details>
<details>
    <summary>Alternatively, or different keys.</summary>
```kotlin
    object Configuration {
        // should have no value.
        @Skip
        lateinit var world: String
    }
```
```dotenv
    hello=world
```
</details>

### Creating your own adapter

If you want to create an adapter for yourself, or for others to use, you can easily do so by implementing the `EnviAdapter` interface:
```kotlin
object SomeAdapter: EnviAdapter {

    fun adapt(stream: Stream<String>): Map<String, String> {
        val contents = stream.toList().joinToString("\n")
        // do some json magic(?)
    }
    
    // this is the default implementation, it is best that you look into it though especially if your adapter 
    // does some magic that translates the data into classes like JSON does.
    // (DO NOT INCLUDE STRING, INT, ETC. TO PARSING SINCE THOSE ARE HANDLED BY THE REFLECTION ENGINE)
    fun <Type> resolve(contents: String, clazz: Class<Type>): Type = EnviBiasedConverter.adapt(contents, clazz)

}
```

Once your adapter is created, you can use it with Envi:
```kotlin
Envi.createConfigurator(SomeAdapter)
```