#!/bin/bash
mvn clean
mvn package
cd target

mkdir /home/drew/tmp/emergentTask

## Dynamic experiment 
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/emergentTask -na 4 -n 4 -sw 100 -sh 100 -a 9 -fc 1000 -hr false -jl 0 -nd 2 -s 30 -b 100 -t 10 -e true -gl signaling
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/emergentTask -na 4 -n 4 -sw 100 -sh 100 -a 8 -fc 1000 -hr false -jl 0 -nd 2 -s 30 -b 100 -e true -gl bounty
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/emergentTask -na 4 -n 4 -sw 100 -sh 100 -a 3 -fc 1000 -hr false -jl 0 -nd 2 -s 30 -b 100 -e true -gl auction
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/emergentTask -na 4 -n 4 -sw 100 -sh 100 -a 5 -fc 1000 -hr false -jl 0 -nd 2 -s 30 -b 100 -e true -gl NN