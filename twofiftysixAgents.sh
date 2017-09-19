#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(256 256 256 256 256 256)
rate=(16 16 16 16 16 16)
jobLen=(13 12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000 300000)

## No fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 6 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 8 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done

## With Fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 8 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done
