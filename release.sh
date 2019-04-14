#!/bin/bash
set -e
set -x
git checkout master
git pull
mvn versions:set -DnewVersion=$1
mvn versions:commit
mvn clean install
git commit -am "[release] prepare release $1"
git push
git tag -a $1 -m "release version $1"
git push origin $1
mvn versions:set -DnewVersion=$1.1-SNAPSHOT
mvn versions:commit
git commit -am "[release] prepare for next development iteration"
git push
git checkout $1
mvn clean install
git checkout master
echo ==================================================
echo Now deploy target/*-with-dependencies.jar to
echo github release page
echo ==================================================

