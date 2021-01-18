title=Why
date=2013-09-24
type=page
status=published
~~~~~~


Why?
====

Sabina's intention is not to compete with Sinatra, or the dozen of clones in different
languages (Grafitti, Nancy, Flask, Sammy etc...).  Its intention is to be used by Java
developers that want or are required to develop in pure Java.

Sabina focuses on being as simple and straight-forward as possible, without the need for
cumbersome (XML) configuration, to enable very fast web application development in pure Java
with minimal effort. (It’s a totally different paradigm when compared to the overuse of
annotations for accomplishing pretty trivial stuff seen in JAX-RS).


Why not go with JAX-RS?
-----------------------

JAX-RS is a nice API that made life easier for Java web developers when it arrived.  However,
it's a bit messy due to the overuse of annotations for accomplishing pretty trivial things.
The annotation magic also makes things more implicit and it's not as easy to get a fast
understanding on things as you get when looking at code using Sabina. For a newbie in web
frameworks and REST Sabina will have you up and running a bit faster than using one of the
JAX-RS implementations.


Why not use Spark?
------------------

The infrastructure has been changed, Sabina uses gradle and has all the modules (http, extra and
documentation) inside the project.

It features a continuous integration process including working tests, code coverage and deployment.

And finally, it supports Undertow backend and a refactor is on the way to support more backends
(including non blocking ones).


Okey, so why use Sabina?
------------------------

Again, if you're a Java developer with neither the urge or time to learn a new programming
language and are not planning to build a super large web application that scales in all
directions, then Sabina is a great alternative. It will have you up and running in notime
without having to think about configuration and other cumbersome activities.
