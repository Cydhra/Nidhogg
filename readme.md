# Nidhogg
A Mojang [Yggdrasil](http://wiki.vg/Authentication) Java client for Minecraft Account authentication

![License: CC BY 4.0](https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg)


## Compiling
The library JAR can be build using ``mvn package``. Javadoc can be generated using ``mvn javadoc:javadoc`` or ``mvn javadoc:jar`` if you
like the documentation in a JAR file.

## Usage

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