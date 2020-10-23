# Nidhogg
A Mojang [Yggdrasil](http://wiki.vg/Authentication) Kotlin/Multplatform client for Minecraft account authentication
 and communication to the Mojang APIs

## Compiling
The library JAR can be build using ``gradle jar``.

## Usage
The Yggdrasil API is wrapped in ``YggdrasilClient``. When creating the client, a secret token can be given as
 parameter that can later be reused to refresh generated sessions. If no token is passed, a random one will be
  generated.

The client instance then offers the following methods, each of wich is a coroutine and is thus performed
 asynchronously:

````Kotlin
val client = YggdrasilClient(clientToken = uuid4().toString())

// login using credentials
val response = runBlocking {
    client.authenticate(AccountCredentials("username", "verysecurepassword"), MinecraftAgent, true)
}
val session = response.session

// validate a session. Returns true, if the session is valid, throws an appropriate exception otherwise.
runBlocking {
    client.validate(session, doSendClientToken = true)
}

// refreshes a session, if it is no longer valid
val response = runBlocking {
    client.refresh(session, selectedProfile = null, requestProfile = false)
}
session = response.session

// invalidate a session
runBlocking {
    client.invalidate(session)
}

// sign out of any existing sessions
runBlocking {
    client.signOut(AccountCredentials("username", "verysecurepassword"))
}
````

The Mojang API is wrapped in ``MojangClient``. It can be instantiated the same way as the ``YggrasilClient``, it just
 does not require a secret token.

Refer to the 
[Yggdrasil test cases](https://github.com/Cydhra/Nidhogg/blob/master/src/jvmTest/kotlin/net/cydhra/nidhogg/YggdrasilClientTest.kt),
[Mojang test cases](https://github.com/Cydhra/Nidhogg/blob/master/src/jvmTest/kotlin/net/cydhra/nidhogg/MojangClientTest.kt)
and the [Batch test cases](https://github.com/Cydhra/Nidhogg/blob/master/src/jvmTest/kotlin/net/cydhra/nidhogg/BatchRequestsTest.kt)
 for more information about the clients' usage.
 
## Dependency Management
This project is currently not in any Maven repository, but you can use [Jitpack](https://jitpack.io/) instead:

#### Maven
````
<repositories>
    <repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
````

````
<dependency>
    <groupId>com.github.Cydhra</groupId>
    <artifactId>Nidhogg</artifactId>
    <version>2.0.0</version>
</dependency>
````

#### Gradle 

````
repositories {
     maven { url "https://jitpack.io" }
}
````
````
dependencies {
    compile group: 'com.github.Cydhra', name: 'Nidhogg', version: '2.0.0'
}
````

## Additional Dependencies
The library is built upon ktor, but does not depend on a specific HTTP engine. You can choose the engine by simply
 adding it to project dependencies like this: 

````
implementation group: 'io.ktor', name: 'ktor-client-cio', version: ktor_version
````
Refer to the [Ktor Documentation](https://ktor.io/docs/http-client-engines.html#jvm) for more information about the
 available HTTP engines.

## License
This project is subject to the [MIT License](https://en.wikipedia.org/wiki/MIT_License) license.
Earlier versions of this library were distributed under Creative Commons.
If you still use those old versions, the license agreement is unchanged.
The new license should not make a notable difference to you, Creative Commons is just unsuited for code, so any new
 code added to the library will be distributed under the terms of MIT license.
