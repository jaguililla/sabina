
BLOG
====

Blog with MongoDB

Taken of Mongo University (M101J: MongoDB for Java Developers)

Install
-------

tar -Jxvf posts.txz && \
mongoimport --db blog --file posts.json --collection posts && \
rm -f posts.json

Run
---

gradle run
