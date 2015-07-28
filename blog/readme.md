
BLOG
====

Blog with MongoDB.

Taken of Mongo University (M101J: MongoDB for Java Developers).


Changes
-------

* Migration to Gradle
* Code formatting
* Update to Java 8
* Port to Sabina
* Bootstrap based Web


Future improvements
-------------------

* Add Bootstrap navbar
* Fix Bootstrap form issues
* Deploy to Heroku


Install
-------

Inside the `assets` directory, execute:

    tar -Jxvf posts.txz && \
    mongoimport --db blog --file posts.json --collection posts && \
    rm -f posts.json


Run
---

gradle run
