#!/bin/bash
mvn clean
mvn package
cd target


numNeighborhood=(4 4 4 4 4 4)
rate=(80 80 80 80 80 80)
jobLen=(65 60 55 50 45 40)
numIterations=(300000 300000 300000 300000 300000 300000)

## No fuel
for i in 0 1 2 3 4 5 6;
do
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 5 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NNJ
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 6 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NN
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bountycomm
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bounty


    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl auctionfixed
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl bountycommfixed
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 1500000 --hasRandomness true --numDepos 4 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl bountyfixed

    
    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 1500000 --hasRandomness true --numDepos 64 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
done

## With Fuel
for i in 0 1 2 3 4 5 6;
do

    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 5 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NNJ
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NN
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bountycomm
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bounty


    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl auctionfixed
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl bountycommfixed
    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numAgents 4 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 4 --fuelCapacity 3000 --hasRandomness true --numDepos 4 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 --hasBountyRate false -gl bountyfixed

#    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl bounty
#    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl NN
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 64 --numNeighborhoods ${numNeighborhood[i]} --simWidth 80 --simHeight 80 --agentType 0 --fuelCapacity 3000 --hasRandomness true --numDepos 64 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 --taskLocLength 40 -gl auction
done