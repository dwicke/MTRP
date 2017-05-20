#!/bin/bash
mvn clean
mvn package
cd target



#mkdir /home/dfreelan/tmp
for i in 15;
do
	## dead experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 10 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 1 --shouldDie true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 10 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 1 --shouldDie true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 10 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 1 --shouldDie true
	#java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 10 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.1 --shouldDie true
done