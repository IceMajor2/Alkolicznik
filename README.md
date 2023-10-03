# ![Logo](src/main/resources/META-INF/resources/beer-mug-icon.png) Alkolicznik
*Alkolicznik* is a web application which tracks the prices of various types of beers in different stores within multiple cities.

The reason the application was created was - first and foremost - to consolidate my knowledge as well as expand on it. From increasing my skills on technical aspects such as experimenting with diverse features of technologies to a broad management abilities like process planning which are as valuable.

## Table of Contents
1. [Demo](#demo)
2. [What's included](#whats-included)
3. [Technologies](#technologies)
4. [REST API documentation](#rest-api-documentation)
5. [Setup](#setup)
   * [Properties](#properties)
   * [Scripts / profiles](#scripts--profiles)
7. [Issues](#issues)
8. [Contact](#contact)

## Demo
The demonstration version is available at https://alkolicznik.alwaysdata.net/

From here you can (almost) instantly poke around using **GUI** or **Swagger UI** ([REST API documentation tool](https://swagger.io/tools/swagger-ui/)) without having to set up anything.

Have fun!

**NOTE:** The page will most likely take a few seconds to open up as it is launched upon request. Moreover, the demo version reloads data every time it is started. Because it uses an external API ([ImageKit](https://imagekit.io/)) to manage images that means **the images will take a few seconds more to load completely**.

<!---PUT A VIDEO OF APP-->

## What's included
* Data
    * Hibernate as JPA implementation
    * different databases for production, tests and demo
    * "data scripts" (see [Scripts / profiles](#scripts--profiles) for more)
    * many-to-many mapping with joined entity model
    * many-to-one and one-to-one mappings
    * transactions
    * Builder design pattern
    * use of `@MappedSuperclass` to increase abstraction
    * SQL scripts (database schema is purely created using them)
    * custom "OR" conditional
    * DTOs
* External API ([ImageKit](https://imagekit.io/)) as image repository (included in integration tests)
* Tests
    * 192 integration tests (477 if including parameterization)
    * various test profiles like 'no-vaadin' or 'image' to improve performance
    * custom assertions
    * a few utility methods for clearer requests to API
    * Awaitality dependency used in tests with async calls
* Security
    * JWT (stored as cookie most of the time)
    * HTTPS protocol with self-signed certificate
    * dummy response for `401 Unauthorized`
    * roles
* Exceptions
    * custom exception model
    * multiple custom exception classes
    * custom validating annotation
    * configuration (`ExceptionHandler`s, error attributes, exception handler for filters, etc.)
* Docker
    * application on [DockerHub](https://hub.docker.com/r/icemajor/alkolicznik)
    * several Docker Compose files for production, demonstration and data deletion/reload
* GUI
    * simple frontend built with Vaadin
    * templates for increasing abstraction
    * uploading images via drag-and-drop
    * Extensive REST API documentation ([Swagger UI](https://swagger.io/tools/swagger-ui/))
* Basic logging

## Technologies
* Java 17
* Spring Boot 3.1.1
    * Spring Web
    * Spring Data JPA
        * Hibernate
    * Spring Security
* Relational databases
    * PostgreSQL 15 (for production)
    * HSQL 2.7.2 (for tests & demo)
* Vaadin 24.1.10
* Gradle 8.2
* Docker
* springdoc-openapi 2.1.0 (generates Swagger UI documentation)

## REST API documentation
As stated before, API documentation was created with the help of Swagger UI.

It's available on the [demonstration site](https://alkolicznik.alwaysdata.net/swagger-ui/index.html).

You can also access it once you setup the application yourself! On the bottom of the main page there is a *Swagger UI* button that will redirect you to `/swagger-ui/index.html` resource.

## Setup
See [SETUP.md](SETUP.md) for installation guidelines.

### Properties
There are three main properties files in the `./src/main/resources` folder:
* `application.properties` - general settings (database, logs, GUI custom properties, etc.)
* `imageKit.properties` - configuration of ImageKit
* `jwt.properties` - properties related to JWT

`.env.properties` is just a file that specifies the `.env` filename to be used in Spring Boot application.

You'll notice that some of the `.properties` files contain a suffix like `demo` or `alwaysdata`. These are overridors of the "normal" properties and used in a given environment.

Most of the settings are strictly configured so that the application may be run swiftly. However, there are still some customizable properties and some of them are even recommended to be changed.
* **`application.properties`**
    * `money.currency.unit`: defines a currency unit used throughout the application
    * `gui.default-city`: in the GUI, average user has beers, stores & prices displayed only for a specified city; if they do not select a city, the default one is shown - that is where you can set it
* **`imageKit.properties`**
    * (**CHANGE & KEEP SECRET**) `imageKit.id`, `imageKit.public-key` & `imageKit.private-key`: credentials for your ImageKit account; they are to be specified in the `.env.example` file which is not to be publicized
    * `imageKit.path`: specifies a root directory path (must be unique across all profiles & tests)
* **`jwt.properties`**
    * (**CHANGE & KEEP SECRET**) `jwt.key`: a secret key used to encode & decode tokens used for authentication & authorization; generate a key (at least 256 bits) yourself [here](https://asecuritysite.com/encryption/plain) and put it in `.env.example` file

Even more information is provided in the actual `.properties` files.

### Scripts / profiles
Alkolicznik comes in with a few options to manipulate the data. **DEMO**, **RELOAD** and **DELETE** "profiles" are supported. Take a minute learning about them, because they might turn out helpful. In [DATASCRIPTS.md](DATASCRIPTS.md) you will find basic informations about each of the "profiles" as well as the instructions needed to perform in order to use them. (It is assumed that the [Setup](#setup) was already performed).

## Issues
* **Using default ImageKit account may result in ImageKit's repository becoming out-of-sync, if someone else touches it.**
* Tests that perform calls to ImageKit API may ocasionally fail when the third-party service takes longer than specified to respond.
* Tests that perform calls to ImageKit API require internet connection.
* SQL scripts (so, also the "profiles") support only "PLN" as currency now.

## Contact
I'm open to discussion and collaboration. Please do contact me by email: icemajorr2@gmail.com :)
