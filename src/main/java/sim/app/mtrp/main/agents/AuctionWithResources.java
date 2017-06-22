package sim.app.mtrp.main.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.app.mtrp.main.*;
import sim.app.mtrp.main.util.QTable;

/**
 *
 * This is a very simple method.  Assumes the agent does not have a carry capacity.
 * This agent "learns" by exponential averaging the number of resources for each of the
 * types over a trip. (a trip is defined as the time between visiting a depo with a max being the fuel capacity)
 *
 * Created by drew on 3/21/17.
 */
public class AuctionWithResources extends AuctionAgent {
    QTable jobSuccess[];
    QTable resources[]; // for each element in the array corresponds to the job type and within the job type we must learn the number of resources that are expected of each type
    QTable tTable; // for each type of job we learn the
    QTable resourceUsage; // these are the estimates on the needed resources in my bag!
    double resourceLearningRate = .9;
    double tLearningRate = .75; // set to .1 originally (should be at .9 though...) tried .75
    double tDiscountBeta = .1; // not used...

    int myResources[];

    int resourcesUsed[]; // this is the resources we have used since we last were at a depo, used to learn the resources needed.
    boolean updatedResourceUsage = true; // initially true since well we haven't actually done anything

    Job failedJob = null;
    final Logger logger = (Logger) LoggerFactory.getLogger(AuctionWithResources.class);
    private int completedTasks = 0;
    private int numFailedTasks = 0;
    private int numJumpship = 0;


    public AuctionWithResources(MTRP state, int id) {
        super(state, id);
        resources = new QTable[state.numJobTypes];
        for (int i = 0; i < resources.length; i++) {
            //resources[i] = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, state.random, state.maxMeanResourcesNeededForType, 1);
            resources[i] = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, 0);
        }
        tTable = new QTable(state.numJobTypes, 1, tLearningRate, tDiscountBeta, state.random, state.getJobLength(), 0.0);
        jobSuccess = new QTable[state.numNeighborhoods];
        for (int i = 0; i < jobSuccess.length; i++) {
            jobSuccess[i]  = new QTable(state.numJobTypes, 1, tLearningRate, tDiscountBeta, state.random, 1.0, 0.0);
        }
        // TODO: might want to learn based on which depo I'm at
        resourceUsage = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, state.random, state.maxMeanResourcesNeededForType * 5, state.maxMeanResourcesNeededForType);
        myResources = new int[state.getNumResourceTypes()];

        resourcesUsed = new int[state.getNumResourceTypes()];
    }

    @Override
    public Task getAvailableTask() {

        // TODO:
        // really I need to check if someone else has beaten me to a job...???
        // if so then learn not to do that particular type of job
        // ... figure out how...

        if (amWorking) {
            return curJob.getTask();
        }

        Task t = super.getAvailableTask();




        if (t != null /* && getTravelConfidence(t) > 0.75*/)
        {
            if (curJob != null && t.getId() != curJob.getTask().getId()) {
                numJumpship++;
            }
            return t;
        }

        if (t != null) {
            state.printlnSynchronized("Conf  = " + getTravelConfidence(t) + " for task = " + t.getId());
        }
        return null;
    }

    @Override
    public void learn(double reward) {
        super.learn(reward);

        jobSuccess[curJob.getTask().getNeighborhood().getId()].update(curJob.getJobType(), 0, reward);
        jobSuccess[curJob.getTask().getNeighborhood().getId()].oneUpdate(oneUpdateGamma);
        completedTasks++;
    }

    @Override
    double getUtility(Task t) {

        /*
        Depo nearestDepo = getClosestDepo(t.getLocation()); // TODO should i look at the depo closest to me or the task... i think the task.  also i might have to ignore the fuel left...
        if (nearestDepo == null) {
            nearestDepo = state.depos[0]; // TODO might be a better idea to ignore the fuel when getting the closest depo... I'll decide later.
        }*/
        //double travelConf = getTravelConfidence(t);
        //double expectedDistTravel = (1 - travelConf) * getNumTimeStepsFromLocation(t.getLocation()) + travelConf * getNumTimeStepsFromLocation(nearestDepo.getLocation());
        //double util =  ( confidenceSuccess *  (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) + state.getJobLength() - getCost(t))) / expectedDistTravel;
        double util =  ( (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));
        //state.printlnSynchronized("task id = " + t.getId() + " utility = " + util + " cost = " + getCost(t));

        return util;
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
    


    double getCost(Task t) {
        // we have a more complicated cost function because we learn the expected number of resources needed

        double resourceCostEstimated = 0.0;
        Depo nearestDepo = getClosestDepo(t.getLocation()); // TODO should i look at the depo closest to me or the task... i think the task.  also i might have to ignore the fuel left...
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
        return   ( (1 - travelConf) * getNumTimeStepsFromLocation(t.getLocation()) * nearestDepo.getFuelCost() ) +
                (travelConf * ( nearestDepo.getFuelCost() * getNumTimeStepsFromLocation(nearestDepo.getLocation()) + getNumTimeStepsFromLocation(t.getLocation(), nearestDepo.getLocation()) + resourceCostEstimated)) + resourceTotalOverallCost;
    }
    

    /**
     * Buy and sell resources from the depo passed
     * Basically what  are the resources and the numbers on average I need when I go on the road
     * @param nearestDepo
     */
    @Override
    public boolean buySellTaskResources(Depo nearestDepo) {
        boolean didAction = false;
        //logger.debug("Num tasks completed {} num failed {} numJumpship {}", completedTasks, numFailedTasks, numJumpship);
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

        // first go through and sell off the resources I don't want so as to have the capital to purchase all resources do need.
        /*
        for (Resource r : nearestDepo.getResources()) {
            int numShouldBuy = (int) Math.round(resourceUsage.getQValue(r.getResourceType(), 0)) - myResources[r.getResourceType()];

            if ( myResources[r.getResourceType()] + numShouldBuy >= 0 && numShouldBuy < 0) {
                // check to make sure we don't sell resources we don't own and then sell
                bounty += nearestDepo.buyBack(r.getResourceType(), -numShouldBuy);
                myResources[r.getResourceType()] -= numShouldBuy;
                didAction = true;
            }
        }
        */

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

        return didAction;
    }


    public void claimWork() {
        // then I'm at the task!
        amWorking = curJob.claimWork(this);
        if (amWorking == true) {
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
                curJob.leaveWork(this);
                failedJob = curJob;

                decommitTask();
            }
        }
    }

    @Override
    public boolean checkNeedResources(Depo nearestDepo) {
        // this does a check if we need any fuel
        boolean needResources = super.checkNeedResources(nearestDepo);
        //logger.debug("Need fuel = {}", needResources);

        if (curJob != null && getTravelConfidence(curJob.getTask()) < .75) {
            needResources = true;
        }

        if (curJob == null) {
            Task bestTask = getBestTask(getTasksWithinRange(state.getBondsman().getAvailableTasks()));
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

    @Override
    public void decommitTask() {
        if (curJob != null)
            curJob.getTask().decommit(this);
        super.decommitTask();

    }


    public int[] getMyResources() {
        return myResources;
    }




}
