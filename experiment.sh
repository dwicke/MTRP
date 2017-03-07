#!/bin/bash
mvn clean
mvn package
cd target
## just pass in the paths to the different checkpoints you desire
## you can create a checkpoint by saving from the gui before pressing start with the configuration of parameters you desire
for var in "$@"
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -checkpoint "$var"
done
