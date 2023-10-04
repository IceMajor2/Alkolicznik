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
       * [Docker Compose](#docker-compose)
       * [Build](#build)
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
* External API: [ImageKit](https://imagekit.io/) as image repository
* Tests
    * 192 integration tests (477 if including parameterization)
    * communication with external API included
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
    * multiple exception classes
    * custom validating annotation
    * configuration (`ExceptionHandler`s, error attributes, exception handler for filters, etc.)
* Docker
    * application on [DockerHub](https://hub.docker.com/r/icemajor/alkolicznik)
    * several Docker Compose files for production, demonstration and data deletion/reload
* GUI
    * simple frontend built with Vaadin
    * templates for increasing abstraction
    * uploading images via drag-and-drop
    * extensive REST API documentation ([Swagger UI](https://swagger.io/tools/swagger-ui/))
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
* springdoc-openapi 2.1.0 (Swagger UI)

## REST API documentation
As stated before, API documentation was created with the help of Swagger UI.

It's available on the [demonstration site](https://alkolicznik.alwaysdata.net/swagger-ui/index.html).

You can also access it once you setup the application yourself! On the bottom of the main page there is a *Swagger UI* button that will redirect you to `/swagger-ui/index.html` resource.

## Setup
See [SETUP.md](SETUP.md) for installation guidelines.

### Properties
There are three main properties files in the `./src/main/resources` folder:
* `application.properties` - general settings (database, logs, GUI config, etc.)
* `imageKit.properties` - configuration of ImageKit
* `jwt.properties` - JWT-related

`.env.properties` defines only the `.env` file which is used as "storage" for environment variables that are then accessible in the Spring Boot application.

You'll notice that some of the `.properties` files contain a suffix like `demo` or `alwaysdata`. These are overridors of the "normal" properties and they are used in a given setting.

Most of the settings are strictly configured so that the application may be run smoothly. However, there are still properties to be customized and some are recommended to be replaced.
#### Docker Compose
`.env` file is a conglomerate of properties. Most of them are quite self-explanatory, but you'll still find more information inside the actual file.

However, one thing should be noted: the "sensitive" data should be changed and kept secret by you. This includes credentials to ImageKit account (`IMAGEKIT_` prefix), database connection configuration (`DB_` prefix) and JWT key.

#### Build
* **`application.properties`**
    * `money.currency.unit`: defines a currency unit used throughout the application
    * `gui.default-city`: in the GUI, average user has beers, stores & prices displayed only for a specified city; if they do not select a city, the default one is shown - that is where you can set it
* **`imageKit.properties`**
    * (**CHANGE & KEEP SECRET**) `imageKit.id`, `imageKit.public-key` & `imageKit.private-key`: credentials for your ImageKit account; they are to be specified in the `.env.example` file which is not to be publicized
    * `imageKit.path`: specifies a root directory path (must be unique across all profiles & tests)
* **`jwt.properties`**
    * (**CHANGE & KEEP SECRET**) `jwt.key`: a secret key used to sign the tokens which are used for authentication & authorization; generate a key (at least 256-bit) yourself [here](https://asecuritysite.com/encryption/plain), then put it in `.env.example` file

Find even more details in the `.properties` files.

### Scripts / profiles
Alkolicznik comes in with a few options to manipulate the data. **DEMO**, **RELOAD** and **DELETE** "profiles" are supported. Take a minute learning about them, because they might turn out helpful. In [DATASCRIPTS.md](DATASCRIPTS.md) you will find basic informations about each of the "profiles" as well as the instructions needed to perform in order to use them. (It is assumed that the [Setup](#setup) was already performed).

## Issues
* **Using default ImageKit account may result in ImageKit's repository becoming out of sync if someone else touches it.**
* GUI may ocassionally refresh when interacting with it.
* **Drag-and-drop upload very often "fails" on the frontend (feedback stating a fail & page refresh) while the truth is that the image was sent and is being processed.**
* Tests that perform calls to ImageKit API may ocasionally fail when the third-party service takes longer than specified to respond.
* Tests that perform calls to ImageKit API require internet connection.
* SQL scripts (so, also the "profiles") support only "PLN" as currency now.

## Contact
I'm open to discussion and collaboration. Please do contact me by email: icemajorr2@gmail.com :)
