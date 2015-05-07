
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
* Upgrade MongoDB driver to 3.0
* Style with Bootstrap (just for fun ;)


Minor stuff
-----------

Other improvement could be use loggers.


Install
-------

tar -Jxvf posts.txz && \
mongoimport --db blog --file posts.json --collection posts && \
rm -f posts.json


Run
---

gradle run