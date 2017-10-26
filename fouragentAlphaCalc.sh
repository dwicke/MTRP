#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(1 1 1 1 1 1)
rate=(4 4 4 4 4 4)
jobLen=(13 12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000 300000)
increment=(0)
del=(.05 .1 .2 .3 .4 .5 .6 .7 .8 .9 .9999)

## No fuel
for k in 0 1 2 3 4 5 6 7 8 9 10;
do
    for i in 0;
    do
        for j in 0;
        do
            java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --bountyIncrement ${increment[j]} --numJobTypes 1 --delta ${del[k]} --taskLocLength 80.0 -gl bounty
        done
        java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 5 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --delta ${del[k]} --taskLocLength 80.0 -gl NN
        #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --delta ${del[k]} --taskLocLength 80.0 -gl auction
    done
done