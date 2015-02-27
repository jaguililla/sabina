title=Reference
date=2014-10-22
type=page
status=published
~~~~~~


API Documentation
=================

* [HTTP Javadoc](http/index.html)
* [Extra Javadoc](extra/index.html)


Release process
===============

Steps:

* Commit and push all changes
* Check build is okey
* Change version in gradle.properties
* Build and deploy (binary and documentation)
* Commit and tag the release
* Update version in gradle.properties
* Commit and push

Command:

    git add . && git commit && \
    vim gradle.properties && \
    gw clean check site publish publishGhPages && \
    git add gradle.properties && git commit -m "Release 1.0.0" && git tag "1.0.0" && \
    vim gradle.properties && \
    git add gradle.properties && git commit -m "New snapshot" && git push origin master --tags

Contribute
==========

* The code should be formatted accordingly to the format provided in my `dotfiles` Github project.

* For a Pull Request to be accepted:
  * It has to pass all existing tests.
  * The coverage of the new code should be at least 70%
  * All public and protected methods and field must be documented using Javadoc
