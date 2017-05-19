#!/bin/bash
mvn clean
mvn package
cd target



#mkdir /home/dfreelan/tmp

for i in 0 5 10 15;
do
	## Dynamic experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 0.01
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 0.01
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 0.01
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.01
done


for i in 0 5 10 15;
do
	## dead experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 0.01 --shouldDie true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 0.01 --shouldDie true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 0.01 --shouldDie true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.01 --shouldDie true
done


for i in 0 5 10 15;
do
	## --hasEmergentJob experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 0.01 --hasEmergentJob true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 0.01 --hasEmergentJob true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 0.01 --hasEmergentJob true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.01 --hasEmergentJob true
done

for i in 0 5 10 15;
do
	## --hasUnexpectedlyHardJobs experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 0.01 --hasUnexpectedlyHardJobs true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 0.01 --hasUnexpectedlyHardJobs true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 0.01 --hasUnexpectedlyHardJobs true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.01 --hasUnexpectedlyHardJobs true
done

for i in 0 5 10 15;
do
	## --hasSuddenTaskIncrease experiment 
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 1 -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyNoJumpship --bountyIncrement 0.01 --hasSuddenTaskIncrease true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 8  -hr false -jl $i -nd 2 -s 30 -b 100 -gl bountyJumpship --bountyIncrement 0.01 --hasSuddenTaskIncrease true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 3 -hr false -jl $i -nd 2 -s 30 -b 100 -gl auction --bountyIncrement 0.01 --hasSuddenTaskIncrease true
	java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/dfreelan/tmp/ -na 4 -n 4 -sw 100 -sh 100 -a 5 -hr false -jl $i -nd 2 -s 30 -b 100 -gl NN --bountyIncrement 0.01 --hasSuddenTaskIncrease true
done