#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(1 1 1 1 1 1 1 1)
rate=(16 16 16 16 16 16 16 16)
jobLen=(15 14 13 12 11 10 9 8)
numIterations=(500000 300000 300000 300000 300000 300000 300000 300000)

## No fuel
for i in 0 1 2 3 4 5 6 7;
do
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 6 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 8 --fuelCapacity 1500000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done

## With Fuel
for i in 0 1 2 3 4 5 6 7;
do
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 8 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done