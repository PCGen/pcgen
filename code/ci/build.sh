#!/bin/sh
set -xe
if [ "$TEST_SUITE" = "slowtest"  ] 
then
  ./gradlew clean build slowtest allReports
else
  ./gradlew clean build test
fi
