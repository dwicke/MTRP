#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(1 1 1 1 1 1)
rate=(16 16 16 16 16 16)
jobLen=(13 12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000 300000)
increment=(0 .0001 .001 .01 .1 1 10 100 .05)

## No fuel
for i in 0 1 2 3 4 5;
do
    for j in 8;
    do
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/single/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 -gl bounty
    done
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 6 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 8 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done

numNeighborhood=(4 4 4 4 4 4)
rate=(16 16 16 16 16 16)
jobLen=(13 12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000 300000)
increment=(0 .0001 .001 .01 .1 1 10 100 .05)

## No fuel
for i in 0 1 2 3 4 5;
do
    for j in 8;
    do
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 -gl bounty
    done
done

## fuel
for i in 0 1 2 3 4 5;
do
    for j in 8;
    do
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 -gl bounty
    done
done

## With Fuel
#for i in 0 1 2 3 4 5;
#do
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 8 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
#done