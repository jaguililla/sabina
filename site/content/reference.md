title=Reference
date=2014-10-22
type=page
status=published
~~~~~~


API Documentation
=================

* [HTTP Javadoc](http/index.html)
* [Extra Javadoc](extra/index.html)


Tools used
==========

* [Travis](https://travis-ci.org/jamming/sabina) for continuous integration
* [Coveralls](https://coveralls.io/r/jamming/sabina) for test coverage
* [Huboard](https://huboard.com/jamming/sabina) for planning
* [Issues](https://github.com/jamming/sabina/issues) for tasks tracking


Release process
===============

Steps:

* Commit and push all changes
* Change version in gradle.properties
* Build and deploy (binary and documentation)
* Commit and tag the release
* Update version in gradle.properties
* Commit and push
* Confirm publishing of artifacts within Bintray (this is a manual step)

Command:

    git add . && git commit && \
    sed -i s/-SNAPSHOT// gradle.properties && \
    gw wipe bundle jacoco site publish publishGhPages && \
    git add gradle.properties && git commit -m "Release ${config.projectVersion}" && \
    git tag "${config.projectVersion}" && \
    vim gradle.properties && \
    git add gradle.properties && git commit -m "New snapshot" && git push origin master --tags


Contribute
==========

* The code should be formatted accordingly to the format provided in my `dotfiles` Github project.

* For a Pull Request to be accepted:
  * It has to pass all existing tests.
  * The coverage of the new code should be at least 70%
  * All public and protected methods and field must be documented using Javadoc

* Commit format: the preferred commit format (through it is not enforced) would have:

    - type: can be any of: feat, fix, refactor, doc, test (lowercased)
    - module: any module of the application, or tha application itself (in lowercase)
    - title:
    - Description: a more complete description of the issue
    - issue #id:

        type(module): title

        Description

        issue #id

* Bug format: when filing bugs please use the given, when, then format. This is only a good
  practise, bugs will be welcomed anyway! Ie:

        Given a condition
        And another condition
        When an action is taken
        And other after the first
        Then something happened

