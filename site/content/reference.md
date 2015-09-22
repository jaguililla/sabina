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
    gw --no-daemon wipe check assemble javadoc jacocoTestReport site publish publishGhPages && \
    git add gradle.properties && git commit -m "Release ${config.projectVersion}" && \
    git tag "${config.projectVersion}" && \
    vim gradle.properties && \
    git add gradle.properties && git commit -m "New snapshot" && git push origin master --tags

sed -i s/-SNAPSHOT// gradle.properties && \
gw --no-daemon wipe check assemble javadoc jacocoTestReport site publish publishGhPages && \
git add gradle.properties && git commit -m "Release 1.3.3" && \
git tag "1.3.3" && \
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

    - Summary: small summary of the change. In imperative form.
    - Description: a more complete description of the issue. It is optional.
    - issue #id: task Id. Optional.

        Summary

        Description

        issue #id

* Bug format: when filing bugs please use the given, when, then format. This is only a good
  practise, bugs will be welcomed anyway! Ie:

        Given a condition
        And another condition
        When an action is taken
        And other after the first
        Then something happened

