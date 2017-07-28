#!/bin/bash
mvn clean
mvn package
cd target

mkdir /home/drew/tmp/NoFuel
mkdir /home/drew/tmp/Fuel
mkdir /home/drew/tmp/Resources

numNeighborhood=(1 2 3 4 5 6)
rate=(5 10 15 20 25 30)

## No fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 300000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 1 -gl bounty
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 0 --fuelCapacity 300000 --hasRandomness true --numDepos 1 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 1 -gl auction
done

## With Fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 1 -gl bounty
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 1 -gl auction
done

## Fuel and Resources
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/Resources --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 3 --startFunds 30000 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --depoCapacity 1000 --depoRefreshRate 10 --numResourceTypes 3 --maxCostPerResource 15 --maxMeanResourcesNeededForType 10 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 3 -gl bounty
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 25 -parallel 8 -for 200000 -ignoreJob 1 -y /home/drew/tmp/Resources --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 2 --startFunds 30000 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --depoCapacity 1000 --depoRefreshRate 10 --numResourceTypes 3 --maxCostPerResource 15 --maxMeanResourcesNeededForType 10 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength 1 --numJobTypes 3 -gl auction
done

