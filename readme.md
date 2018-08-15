# Nidhogg
A Mojang [Yggdrasil](http://wiki.vg/Authentication) Java/Kotlin client for Minecraft Account authentication

![License: CC BY 4.0](https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg)


## Compiling
The library JAR can be build using ``gradle jar``.

## Usage
The Yggdrasil API is wrapped in ``YggdrasilClient``. It can be optionally instantiated with a client token, that is then used for 
identification at the Yggdrasil API. Alternatively, it uses a default token.

The client instance then offers the following methods:

````Kotlin
// login using credentials
session = client.login(AccountCredentials(username, password))

// validate a session. Returns true, if the session is valid, throws an appropriate exception otherwise.
client.validate(session)

// refreshes a session, if it is no longer valid
client.refresh(session)

// invalidate a session
client.invalidate(session)

// sign out of any existing sessions
client.signOut(AccountCredentials(username, password))
````

The Mojang API is wrapped in ``MojangClient``. It can be instantiated the same way as the ``YggrasilClient``.

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
    <version>1.3</version>
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
    compile group: 'com.github.Cydhra', name: 'Nidhogg', version: '1.3'
}
````

## License
This project is subject to the [Creative Commons 4.0](https://creativecommons.org/licenses/by/4.0/) license.
