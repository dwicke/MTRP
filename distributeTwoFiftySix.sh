#!/bin/bash



numNeighborhood=(256 256 256 256 256)
rate=(16 16 16 16 16)
jobLen=(12 11 10 9 8)
numIterations=(300000 300000 300000 300000 300000)
#bountyNodes=('node01' 'node02' 'node03' 'node04' 'node05')
#nnNodes=('node06' 'node07' 'node08' 'node09' 'node10')
nodeName=( 'node01' 'node02' 'node07' 'node08' 'node10')
## No fuel
for i in 0 1 2 3 4;
do
    ssh -n -f ${bountyNodes[i]} "sh -c 'nohup ./singleNode.sh ${numIterations[i]} ${numNeighborhoods[i]} ${jobLen[i]} ${nodeName[i]} > /dev/null 2>&1 &'"
#    ssh -n -f ${bountyNodes[i]} "cd nthreads/target;sh -c 'nohup java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 4 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numThreads 24 --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty > /home/dwicke/${bountyNodes[i]} 2>&1 &'"
#    ssh -n -f ${nnNodes[i]} "cd nthreads/target;sh -c 'nohup java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 4 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numThreads 24 --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 6 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN > /home/dwicke/${nnNodes[i]} 2>&1 &'"
done

### With Fuel
#for i in 0 1 2 3 4 5;
#do
#    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 14 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl bounty
#    java -jar MTRP-1.0-SNAPSHOT.jar -repeat 2 -parallel 2 -for ${numIterations[i]} -ignoreJob 1 -y /home/dwicke/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 6 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl NN
#    #java -jar MTRP-1.0-SNAPSHOT.jar -repeat 5 -parallel 8 -for ${numIterations[i]} -ignoreJob 1 -y /home/drew/tmp/Fuel --numAgents 256 --numNeighborhoods ${numNeighborhood[i]} --simWidth 640 --simHeight 640 --agentType 8 --fuelCapacity 3000 --hasRandomness true --numDepos 256 --fuelCost 1.0 --timestepsTilNextTask ${rate[i]} --jobLength ${jobLen[i]} --numJobTypes 1 -gl auction
#done

#ssh -n -f node01 "cd nthreads/target;sh -c 'nohup java -jar MTRP-1.0-SNAPSHOT.jar -repeat 1 -parallel 4 -for 300000 -ignoreJob 1 -y /home/dwicke/tmp/NoFuel --numThreads 24 --numAgents 256 --numNeighborhoods 256 --simWidth 640 --simHeight 640 --agentType 14 --fuelCapacity 1500000 --hasRandomness true --numDepos 256 --fuelCost 0 --timestepsTilNextTask 16 --jobLength 13 --numJobTypes 1 -gl bounty > /home/dwicke/node01 2>&1 &'"