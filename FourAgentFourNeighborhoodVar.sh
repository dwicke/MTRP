#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(4 4 4 4 4 4)
rate=(16 16 16 16 16 16)
jobLen=(13 12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000 300000)
increment=(0 .0001 .001 .01 .1 1 5)

mkdir /home/dwicke/tmp/fouragentDiscon
mkdir /home/dwicke/tmp/fouragentDiscon/NoFuel
mkdir /home/dwicke/tmp/fouragentDiscon/Fuel

## No fuel
for i in 0 1 2 3 4 5;
do
    for j in 0 1 2 3 4 5 6;
    do
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 --hasNeighborhoodBounty true -gl NB -ls ${numIterations[i]} -es 2
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 -gl bounty -ls ${numIterations[i]} -es 2
    done
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 5 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN -ls ${numIterations[i]} -es 2
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done

## fuel
for i in 0 1 2 3 4 5;
do
    for j in 0 1 2 3 4 5 6;
    do
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 --hasNeighborhoodBounty true -gl NB -ls ${numIterations[i]} -es 2
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 -gl bounty -ls ${numIterations[i]} -es 2
    done
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 4 -parallel 16 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/fouragentDiscon/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 5 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN -ls ${numIterations[i]} -es 2
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
done

## With Fuel
#for i in 0 1 2 3 4 5;
#do
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 1 --numNeighborhoods ${numNeighborhood[i]} --simWidth 100 --simHeight 100 --agentType 8 --fuelCapacity 3000 --hasRandomness true --numDepos 1 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
#done