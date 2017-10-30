#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(64 64 64 64 64 64)
rate=(80 80 80 80 80 80)
jobLen=(65 60 55 50 45 40)
numIterations=(300000 300000 300000 300000 300000 300000)

mkdir /home/dwicke/tmp/sixtyfouragentCon
mkdir /home/dwicke/tmp/sixtyfouragentCon/NoFuel
mkdir /home/dwicke/tmp/sixtyfouragentCon/Fuel

## No fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/sixtyfouragentCon/NoFuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 64 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bounty -ls ${numIterations[i]} -es 0
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/sixtyfouragentCon/NoFuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 5 --fuelCapacity 1500000 --hasRandomness true --numDepos 64 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NN -ls ${numIterations[i]} -es 0
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 64 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
done

## With Fuel
for i in 0 1 2 3 4 5;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/sixtyfouragentCon/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bounty -ls ${numIterations[i]} -es 0
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/sixtyfouragentCon/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 5 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NN -ls ${numIterations[i]} -es 0
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 320 --simHeight 320 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
done