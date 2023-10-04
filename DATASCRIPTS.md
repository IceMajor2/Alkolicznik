# ![Logo](src/main/resources/META-INF/resources/beer-mug-icon.png) *Data scripts*
Below presented are the options with which you can run the application. They are created for convenience and presentation.

Note that if you've **built** the application, then you may customize the "data scripts" (described later in the **Build** sections). This feature is not provided for **Docker Compose** setup.

## Demo
*Demo* is actually a legitimate Spring profile. If you want to load demonstrational data (a bunch of stores, beers, prices, users & images), then this is the right place.

This profile uses **HSQL** in-memory database that you do not have to set up. The database will be reloaded at every app launch. Plus - obviously - it won't collide with the production database keeping previous data (i.e. outside the `demo` profile) untouched.

### Docker Compose
* Download `docker-compose-demo.yml` and move it to the directory with the `.env` file.
* Run it with `docker-compose -f docker-compose-demo.yml up`.
* `docker-compose down` to stop.

### Build
* Add arguments `--spring.profiles.active=demo --spring.config.name=application,imageKit,jwt` to `java` command at launch.
   * For example: `java -jar alkolicznik-X.X.X.jar --server.port=443 --spring.profiles.active=demo --spring.config.name=application,imageKit,jwt`

#### Customize
The `demo` profile's data is provided with the application. However, that doesn't mean it is immutable. If you're interested in changing this data, feel free to do so.
* To modify **beers**, **stores**, **prices** or **users** (passwords must be encrypted), then:
    * Go into `./src/main/resources/data_sql/data-demo-hsql.sql` and feel free to remove, create or adjust the entities. (It is written in SQL, but you'll surely get the hang of it quickly).
* To change the initialized **images**, then:
    * Go into `./src/main/resources/images/*-demo` and rearrange images the way you want it. However, **they have to meet the requirements**:
        * proportions must be valid (see [REST API documentation](README.md#rest-api-documentation) for more info)
        * if adding a *beer image*, then the first characters must represent an ID of the beer the image relates to
        * if adding a *store image*, then the filename must match almost exactly (case-insensitive) the store name.

## Reload data
Reloading is nothing other than deleting everything from the database and then loading exemplary data that comes with the application. It is quite similiar to `demo` profile, but it differs in that it operates on a production database (+ the data sample is different here).

If you don't change **user** entities, then there are three users created by default:
| **username** | **password** |  **role**  |
|:------------:|:------------:|:----------:|
|     admin    |     admin    |    ADMIN   |
|     user1    |    abcdef    | ACCOUNTANT |
|     user     |     user     |    USER    |

### Docker Compose
* Download `docker-compose-reload.yml` and move it to the directory with the `.env` & `docker-compose.yml` files.
* Run it with `docker-compose -f docker-compose.yml -f docker-compose-reload.yml up`.
* `docker-compose down` to stop.

### Build
* Add argument `--data.reload=true` to `java` command at launch. Example:

  `java -jar alkolicznik-X.X.X.jar --server.port=443 --data.reload=true`
  
#### Customize
If you want different data to be loaded with this "data script", then by all means adjust it.

The approach is pretty much the same as [with the `demo` profile](#demo). The difference is that the files with the `demo` string are not of interest here. For example, `data-demo-hsql.sql` should be replaced with `data.sql`.

## Delete data
Title says it all. Erase everything... as simple as that. Don't expect to customize anything here!

### Docker Compose
* Download `docker-compose-delete.yml` and move it to the directory with the `.env` & `docker-compose.yml` files.
* Run it with `docker-compose -f docker-compose.yml -f docker-compose-delete.yml up`.
* `docker-compose down` to stop.

### Build
* Add argument `--data.delete=true` to `java` command at launch. Example:

  `java -jar alkolicznik-X.X.X.jar --server.port=443 --data.delete=true`
