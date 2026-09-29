# Ultreon Binary Object
**File Extension:** `.ubo`  
**API Language:** `Java 8`  

## Usage
Assuming you use Gradle.
1. Then, in `build.gradle`, define the dependency:  
    ```gradle
    // --- Needed for 1.6.0 or above ---
    repositories {
        maven { url = "https://maven.ultreon.dev/releases" }
        maven { url = "https://maven.ultreon.dev/snapshots" }
    }
    
    // Add the dependency
    dependencies {
         implementation 'dev.ultreon:ubo:1.6.0'
   
        // Or use the 1.6.0 snapshot
        implementation 'dev.ultreon:ubo:1.6.0-SNAPSHOT'
    }
    ```
    Or if you use the Kotlin DSL:
    ```kotlin
    // --- Needed for 1.6.0 or above ---
    repositories {
        maven("https://maven.ultreon.dev/releases")
        maven("https://maven.ultreon.dev/snapshots")
    }
   
    // Add the dependency
    dependencies {
        implementation("dev.ultreon:ubo:1.5.0")
   
        // Or use the 1.6.0 snapshot
        implementation("dev.ultreon:ubo:1.6.0-SNAPSHOT")
    } 
2. We can now proceed to the using section. The `DataIo` class is capable of reading and writing UBO data.  
    Example:
   ```java
   import dev.ultreon.ubo.DataIo;
   import dev.ultreon.ubo.types.MapType;
   import java.io.File;
   
   public class DataTest {
       public static void main(String[] args) {
           MapType dataType = new MapType();
           dataType.putString("greetings", "Hello World");
           
           DataIo.write(dataType, new File("data.ubo"));
       }
   }
   ```
   Or if you want to use Kotlin:
   ```kotlin
   import dev.ultreon.ubo.DataIo
   import dev.ultreon.ubo.types.MapType
   import java.io.File
   
   fun main() {
       val dataType = MapType()
       dataType.putString("greetings", "Hello World")
       
       DataIo.write(dataType, File("data.ubo"))
   }
   ```
3. You can now build it. You can change the example in step 2 to suit your needs. 

~~Here's the [jitpack listing](https://jitpack.io/#dev.ultreon/ubo) for the current versions and builds you can use.~~
Check out the project's 'releases' page for latest releases.

## Naming conventions
The following conventions are for map keys:
 * `MapType` and `ListType` are in `PascalCase`.
 * Any other types are in `camelCase`.

Do note that in some cases, keys can have a different case.  
For example, if the key is used for identifiers (Like those: `namespace:id`).  
Tho it's generally not recommended to use map keys for objects like identifiers or numbers.
