Thank you for taking the time for contribute!  

Please see below guidelines for contributing to ACUITY project.  

## Git

### Git Workflow

* Make a feature branch from `dev` branch
* When the work is done, [rebase your commits](https://github.com/edx/edx-platform/wiki/How-to-Rebase-a-Pull-Request) ([PDF](/docs/How-to-Rebase-a-Pull-Request.pdf)) onto the latest version of the `dev` branch and check that the new code works as expected and there are no conflicts
* Create a pull request to `dev` branch 

### Pull request checklist

Before submitting the pull request make sure:

* The issue/feature has been dev-tested
* All the new code is covered with unit tests
* All tests pass and production bundle can be built
```
mvn clean package -P checks
```

### Naming conventions
Commit message should be written in the imperative mood, starting with an uppercase letter. For example:
```
Fix console error after dataset removal
```
Commit message should tell you "Why?" while code responds on "How?"

## Code style

Coding standards for Java are checked by [CheckStyle](https://checkstyle.sourceforge.io/) tool.  
CheckStyle configuration file is `/config/src/main/resources/checkstyle.xml`.  
See [ACUITY Visualisations Wiki](TODO:add link) on how to configure CheckStyle plugin for the IDE you're using.  

<!--
## Miscellaneous

### How to update application version
Run
```
mvn versions:set -DnewVersion=1.1-SNAPSHOT -DprocessAllModules
```
If everyting is OK, then
```
mvn versions:commit -DprocessAllModules
```
-->

