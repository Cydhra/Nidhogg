# Nidhogg
A Mojang [Yggdrasil](http://wiki.vg/Authentication) Java/Kotlin client for Minecraft Account authentication

![License: CC BY 4.0](https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg)


## Compiling
The library JAR can be build using ``mvn package``. Javadoc can be generated using ``mvn javadoc:javadoc`` or ``mvn javadoc:jar`` if you want the documentation in a JAR file.

## Usage
The library consists of three main classes that may be used:
``YggdrasilClient``, ``AccountCredentials``, ``Session``.

``AccountCredentials`` and ``Session`` are simple data classes with one constructor, ``AccountCredentials`` is immutable, ``Session`` can
be mutated, since refresh might change the access token.

``YggdrasilClient`` is a wrapped REST client for the Yggdrasil authentication service. It provides all methods supported by the Yggdrasil
authentication service:
* ``login`` takes an ``AccountCredentials`` object and logs in at Yggdrasil. Returns a ``Session`` object.
* ``validate`` takes a ``Session`` object and validates, that the session is still legal.
* ``refresh`` takes a ``Session`` object and refreshes it, if it is too old to be valid.
* ``invalidate`` takes a ``Session`` object and invalidates it, so it cannot be used for authentication anymore.
* ``signout`` takes a ``AccountCredentials`` object and invalidates any session currently associated with this user.

The ``YggdrasilClient`` takes one optional parameter in its constructor that sets the client token, that Nidhogg will use to identify itself
at the Yggdrasil service. By default the value is set to "Nidhogg". It can be chosen arbitrarily.
## Maven Dependency
This project is currently not in any Maven repository, but you can use [Jitpack](https://jitpack.io/) instead:

Add the Jitpack service repository
````
<repositories>
    <repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
````

And then the following dependency:
````
<dependency>
    <groupId>com.github.Cydhra</groupId>
    <artifactId>Nidhogg</artifactId>
    <version>1.0</version>
</dependency>
````

The version should be replaced with the latest release. Jitpack will automatically fetch the requested release by its tag (provided as
version) and provide it in its own maven repository.

## License
This project is subject to the [Creative Commons 4.0](https://creativecommons.org/licenses/by/4.0/) license.
