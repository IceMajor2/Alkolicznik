# ![Logo](src/main/resources/META-INF/resources/beer-mug-icon.png) Installation
Below are presented two different ways to set up *Alkolicznik*. **Docker Compose** is recommended if you fancy a quick, more-automated installation. **Build**, however, allows you to get the Java application directly granting you the possibility to modify and customize it further.

## Docker Compose
Using Docker Compose will most likely be the easiest and fastest way to get the application running on your machine. The only thing you'll need to manually install is the Docker itself. [Follow this guide to do so](https://docs.docker.com/get-docker/).

Once you're done with Docker setup, next steps are the following:

1. Download `docker-compose.yml` and `.env` files from this repository and put them in a directory of your choice.
2. If `.env` file was downloaded as `env`, then rename it to `.env` manually. (`.env` is a *hidden* file. It may help to enable visibility of hidden files for future use. If you don't know how to do that, just google: *show hidden files ${YOUR_OPERATING_SYSTEM}*).
3 (**Recommended** but optional). [Create ImageKit account](https://imagekit.io/registration/), go into `imageKit.properties` and use replace your credentials with the exemplary ones.
4. Open command-line interface, move to the directory with both files and run `docker-compose up` command.

That's it! You should be able to access the application on `localhost`.

* Type `docker-compose down` to turn off the application.
* Type `docker-compose up` to start it again.

## Build
Successfully going through this section will get you an executable JAR of this application as well as the whole source code (including tests) and resources used by *Alkolicznik*.

Prerequisites:

* Java 17 (or newer)
* PostgreSQL 15 (or newer)

Then, follow these steps:
1. Create a database in PostgreSQL.
2. Download a release from this repository and unzip the archive somewhere on your drive.
3. Move to the directory with the app and open `./src/main/resources/.env.example`. Set the value of `PROD_DB_NAME` to the database name that you've created in the 1st step. Fill `PROD_DB_USERNAME` & `PROD_DB_PASSWORD` with the PostgreSQL user's credentials.
4 (**Recommended** but optional). [Create ImageKit account](https://imagekit.io/registration/), go into `./src/main/resources/.env.example` and replace exemplary account's credentials with yours.
5. Make use of Gradle wrapper (`gradlew`) in order to execute `bootJar` task:
   * Windows: `gradlew bootJar`
   * Linux:
      * set execution flag first: `chmod +x gradlew`
      * `./gradlew bootJar`

    Wait until you're notified the operation was successful.
6. Move to the `./build/libs` directory. There should be an executable JAR file that you just built. Run it with:
   * `java -jar alkolicznik-X.X.X.jar` - that launches the application on port `8443`. Application will be accessible on `https://localhost:8443` (**explicitly-stated `https://` is required**)
   * `java -jar alkolicznik-X.X.X.jar --server.port=443` - app is started on port `443`. That allows to open it through `localhost`. **NOTE:** make sure that you run this command as admin, otherwise port `443` might not open.
