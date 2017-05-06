#!/bin/bash
mvn clean
mvn package
cd target

## Dynamic experiment 
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 3 -t 15 -s 30 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 8 -t 15 -s 30 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 9 -t 15 -s 30 -b 100


## Should die experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 3 -t 15 -s 30 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 8 -t 15 -s 30 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 9 -t 15 -s 30 -b 100 -d true

## Emergent Job experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 3 -t 15 -s 30 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 8 -t 15 -s 30 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 9 -t 15 -s 30 -b 100 -e true


## Unexpectedly Hard Jobs experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 3 -t 15 -s 30 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 8 -t 15 -s 30 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 9 -t 15 -s 30 -b 100 -u true


## Sudden Task Increase experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 3 -t 15 -s 30 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 8 -t 15 -s 30 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/regular -n 4 -a 9 -t 15 -s 30 -b 100 -i true


############################################################################################################################
#
#  One Neighborhood
#
## Dynamic experiment 
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 3 -t 15 -s 5 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 8 -t 15 -s 5 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 9 -t 15 -s 5 -b 100


## Should die experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 3 -t 15 -s 5 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 8 -t 15 -s 5 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 9 -t 15 -s 5 -b 100 -d true

## Emergent Job experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 3 -t 15 -s 5 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 8 -t 15 -s 5 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 9 -t 15 -s 5 -b 100 -e true


## Unexpectedly Hard Jobs experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 3 -t 15 -s 5 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 8 -t 15 -s 5 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 9 -t 15 -s 5 -b 100 -u true


## Sudden Task Increase experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 3 -t 15 -s 5 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 8 -t 15 -s 5 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/one -n 1 -a 9 -t 15 -s 5 -b 100 -i true


############################################################################################################################
#
#  Forty Neighborhoods
#
## Dynamic experiment 
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 3 -t 15 -s 300 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 8 -t 15 -s 300 -b 100
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 9 -t 15 -s 300 -b 100


## Should die experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 3 -t 15 -s 300 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 8 -t 15 -s 300 -b 100 -d true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 9 -t 15 -s 300 -b 100 -d true

## Emergent Job experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 3 -t 15 -s 300 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 8 -t 15 -s 300 -b 100 -e true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 9 -t 15 -s 300 -b 100 -e true


## Unexpectedly Hard Jobs experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 3 -t 15 -s 300 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 8 -t 15 -s 300 -b 100 -u true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 9 -t 15 -s 300 -b 100 -u true


## Sudden Task Increase experiment
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 3 -t 15 -s 300 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 8 -t 15 -s 300 -b 100 -i true
java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/forty -n 40 -a 9 -t 15 -s 300 -b 100 -i true
