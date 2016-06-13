# ganymede-exploration

This Java application simulates an exploration of a labyrinth having many, many rooms on Ganymede, one of Jupiter's moons, using a variable number of drones.  Each drone operates independently of the others, as well as asynchronously in relation to the main controller by leveraging Java's built-in `wait()` and `notify()`.

Besides the entrypoint for the application, `GanymedeExploration.java`, which is at the top level, there are two major packages: `model`, where all domain object classes are stored along with the request/response object classes, and `service`, which contains the core networking classes.

The Retrofit library is used to simplify the RESTful interface-related networking logic, and GSON is used for serialization and deserialization of objects.

##Setup
This requires Java 1.7.
Gradle will automatically be downloaded once the build process is started, as will all dependencies.

##Running
After cloning the project locally, from the command line, change to the project module directory:
```
cd <project-directory>/Ganymede\ Exploration
```

Once there, you can build and execute the application by running:
```
../gradlew run
```

If everything works fine, you should eventually see the message `Success!  Please send your source code and report to challenge@airtime.com` (shown right before `BUILD SUCCESSFUL`).
