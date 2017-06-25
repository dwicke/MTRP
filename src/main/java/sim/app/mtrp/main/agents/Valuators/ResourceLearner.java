package sim.app.mtrp.main.agents.Valuators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.app.mtrp.main.*;
import sim.app.mtrp.main.agents.SimpleLearningWithResources;
import sim.app.mtrp.main.util.QTable;

/**
 * The purpose of this class is to learn what resources
 * a particular agent needs in order to complete task before returning
 * to a depo.
 * Created by drew on 6/14/17.
 */
public class ResourceLearner {

    MTRP state;
    QTable resources[]; // for each element in the array corresponds to the job type and within the job type we must learn the number of resources that are expected of each type


    QTable resourceUsage; // these are the estimates on the needed resources in my bag!
    double resourceLearningRate = .9;

    int myResources[];

    int resourcesUsed[]; // this is the resources we have used since we last were at a depo, used to learn the resources needed.
    boolean updatedResourceUsage = true; // initially true since well we haven't actually done anything

    Job failedJob = null;
    private int completedTasks = 0;
    private int numFailedTasks = 0;
    private int numJumpship = 0;

    double oneUpdateGamma = .001; // .001


    public ResourceLearner(MTRP state) {
        this.state = state;
        resources = new QTable[state.numJobTypes];
        resources = new QTable[state.numJobTypes];
        for (int i = 0; i < resources.length; i++) {
            // TODO: figure out what i should do here i think it needs to be zero otherwise i might think i don't have enough resources to do any task
            //resources[i] = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, state.random, state.maxMeanResourcesNeededForType, 1);
            resources[i] = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, 0);
        }
        // TODO: might want to learn based on which depo I'm at
        resourceUsage = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, state.random, state.maxMeanResourcesNeededForType * 5, state.maxMeanResourcesNeededForType);
        myResources = new int[state.getNumResourceTypes()];

        resourcesUsed = new int[state.getNumResourceTypes()];
    }




    double getTravelConfidence(Task t) {
        double travelConf = 1.0;
        for (int i = 0; i < state.numResourceTypes; i++) {
            if (resources[t.getJob().getJobType()].getQValue(i, 0) - myResources[i] > 0) {
                // then we will need resources take the min because it is a worst case confidence...
                travelConf = Math.min(travelConf, (resources[t.getJob().getJobType()].getQValue(i, 0) - myResources[i]) / resources[t.getJob().getJobType()].getQValue(i, 0));
            }
        }

        return travelConf;
    }



    public double getCost(Task t, Depo nearestDepo, double distToTask, double distToDepo, double distFromTaskToDepo) {
        // we have a more complicated cost function because we learn the expected number of resources needed

        double resourceCostEstimated = 0.0;
        //Depo nearestDepo = getClosestDepo(t.getLocation()); // TODO should i look at the depo closest to me or the task... i think the task.  also i might have to ignore the fuel left...
        if (nearestDepo == null) {
            nearestDepo = state.depos[0]; // TODO might be a better idea to ignore the fuel when getting the closest depo... I'll decide later.
        }
        double travelConf = getTravelConfidence(t);
        double resourceTotalOverallCost = 0.0;
        for (int i = 0; i < state.numResourceTypes; i++) {
            resourceCostEstimated += Math.max(resources[t.getJob().getJobType()].getQValue(i,0) - myResources[i], 0) * nearestDepo.getResourceCost(i);
            resourceTotalOverallCost += nearestDepo.getResourceCost(i) * resources[t.getJob().getJobType()].getQValue(i,0);
        }
        // so my confidence that I am going to go straight to the task times that cost + confidence that I'm going to have to go to a depo and then to the task times that cost + the cost of the resources used to complete the task
        return   ( (1 - travelConf) * distToTask * nearestDepo.getFuelCost() ) +
                (travelConf * ( nearestDepo.getFuelCost() * distToDepo + distFromTaskToDepo + resourceCostEstimated)) + resourceTotalOverallCost;
    }




    /**
     * Buy and sell resources from the depo passed
     * Basically what  are the resources and the numbers on average I need when I go on the road
     * @param nearestDepo
     */
    public double buySellTaskResources(Depo nearestDepo, double bounty) {
        boolean didAction = false;
        completedTasks = 0;
        numFailedTasks = 0;
        numJumpship = 0;

        if (updatedResourceUsage == false) { // only
            updatedResourceUsage = true;
            // first learn the resources that have been used during a trip and reset
            for (int i = 0; i < resourcesUsed.length; i++) {

                if (resourcesUsed[i] > resourceUsage.getQValue(i, 0)) {
                    resourceUsage.update(i, 0, resourcesUsed[i]);
                }
                //state.printlnSynchronized("Resource [" + i + "] used = " + resourcesUsed[i] + " resource Usage = " + resourceUsage.getQValue(i,0));
                resourcesUsed[i] = 0; // reset
            }
            resourceUsage.oneUpdate(oneUpdateGamma);
        }



        // now go in and buy up them resources!
        for (Resource r : nearestDepo.getResources()) {
            int numShouldBuy = Math.min((int) r.getCurQuantity(), (int) Math.min(bounty / nearestDepo.getResourceCost(r.getResourceType()), (int) Math.round(resourceUsage.getQValue(r.getResourceType(), 0)) - myResources[r.getResourceType()]));


            if (numShouldBuy > 0) {
                //logger.debug("Agent id " + id + " buying resouce " + r.getResourceType()  + " of quantity " + numShouldBuy + " qval = " + resourceUsage.getQValue(r.getResourceType(), 0));

                //logger.debug("Agent id {} buying resource type {} quant {} total price = {} my bounty = {}", id, r.getResourceType(), numShouldBuy, numShouldBuy * nearestDepo.getResourceCost(r.getResourceType()), bounty);
                while (numShouldBuy * nearestDepo.getResourceCost(r.getResourceType()) > bounty) {
                    numShouldBuy--;
                }
                bounty -= nearestDepo.buy(r.getResourceType(), numShouldBuy);
                myResources[r.getResourceType()] += numShouldBuy;
                failedJob = null;// reset failed job because I've now bought resources
                didAction = true;
            }
        }

        return bounty;
    }


    public boolean claimWork(Job curJob) {
        // then I'm at the task!
        boolean amWorking = true;
        // now I've got to get the resources that i need to actually do the job so ask the job
        int[] resourcesNeeded = curJob.getResourcesNeeded();
        // now learn and remove the resources that I have and try and do the task
        for (int i = 0; i < resourcesNeeded.length; i++) {
            // you spend all the resources you have even if you find out that you don't have enough.  the idea is that
            // it is a geometric distribution.

            if (myResources[i] - resourcesNeeded[i] < 0) {
                amWorking = false;
                numFailedTasks++;
            }
            myResources[i] = Math.max(0, myResources[i] - resourcesNeeded[i]);

            resources[curJob.getJobType()].update(i, 0, resourcesNeeded[i]); // TODO: actually this should only be done when the task is completed...
            resourcesUsed[i] += resourcesNeeded[i]; // even though i might not have actually had them I still would have used them had I had them
            updatedResourceUsage = false;
        }

        if (amWorking == false) {
            failedJob = curJob;
        }
        return amWorking;
    }


    public boolean checkNeedResources(Depo nearestDepo, Job curJob, Task bestTask) {
        // this does a check if we need any fuel
        boolean needResources = false;
        //logger.debug("Need fuel = {}", needResources);

        if (curJob != null && getTravelConfidence(curJob.getTask()) < .75) {
            needResources = true;
        }

        if (curJob == null) {
            if (bestTask != null)
                state.printlnSynchronized("time step: " + state.schedule.getSteps() + "Best job id = " + bestTask.getJob().getId() + " confidence = " + getTravelConfidence(bestTask) + " failed Job = "  + failedJob);
            if (bestTask == null) {
                needResources = true;
            } else if (bestTask != null && getTravelConfidence(bestTask) < .75) {
                needResources = true;
            } else if (failedJob != null && bestTask != null && (bestTask.getJob().getId() == failedJob.getId())) {
                needResources = true;
            }

        }


        return needResources;
    }


    public int[] getMyResources() {
        return myResources;
    }
}
