#!/bin/bash

cd /app

if [ "$(ls -A release)" ];
then
  gradle build
fi

cd /app/release

java -jar api-space-invaders-1.0.jar
