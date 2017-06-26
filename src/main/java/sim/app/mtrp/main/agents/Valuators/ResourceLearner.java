package sim.app.mtrp.main.agents.Valuators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.app.mtrp.main.*;
import sim.app.mtrp.main.agents.SimpleLearningWithResources;
import sim.app.mtrp.main.util.QTable;

import java.util.Map;
import java.util.TreeMap;

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
            if (resources[t.getJob().getJobType()].getQValue(i, 0) > myResources[i]) {
                // then we will need resources take the min because it is a worst case confidence...
                travelConf = Math.min(travelConf, myResources[i] / resources[t.getJob().getJobType()].getQValue(i, 0));
                //state.printlnSynchronized("Dif is >0 predicted num resources needed = " + resources[t.getJob().getJobType()].getQValue(i, 0) + " num resources i have = " + myResources[i] + " travel conf = " + travelConf + " calculation = " + (resources[t.getJob().getJobType()].getQValue(i, 0) - myResources[i]) / resources[t.getJob().getJobType()].getQValue(i, 0));
            } else {
                //state.printlnSynchronized("predicted num resources needed = " + resources[t.getJob().getJobType()].getQValue(i, 0) + " num resources i have = " + myResources[i]);
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
     * @param percentJobTypes key is the percent of the jobs that are of type of the value (keys must be unique so the percents must be unique)
     */
    public double buySellTaskResources(Depo nearestDepo, double bounty, TreeMap<Double, Integer> percentJobTypes) {


        // really i can look at the percentages of the total jobs available what are there job types
        // then say 50% of all of the availble nearby jobs are of type 0 then  I should buy the resources
        // that will let me do those jobs with 50% of my money.
        // then if 20% of the tasks are of another type of job then I use 20% of my money to purchase those resources
        // and so on and so forth
        // This is an estimate of the resources I need.
        // I also have to consider fuel cost and that sort of thing.
        // fuel is purchased first so I don't have to worry about saving enough to buy fuel
        // fuel is purchased so as to completely fill up
        // would be interesting to consider what percent to save?  as in don't spend? will try that later
        // for now just buy it all!!!


        for (Map.Entry<Double, Integer> en: percentJobTypes.descendingMap().entrySet()) {

            // for each of the job types I should buy resources for them
            // but first I need to figure out how many
            double budget = bounty * en.getKey();
            int jobtype = en.getValue();
            double costPerJob = 0.0;
            boolean suficientResources = true;
            int minJobs = Integer.MAX_VALUE;
            // should I take into account already owned resources before I started buying? that seems complicated
            for (int i = 0; i < state.numResourceTypes; i++) {
                if (nearestDepo.getResources()[i].getCurQuantity() < Math.ceil(resources[jobtype].getQValue(i, 0))) {
                    // we have a problem! the depo doesn't have enough of the resource in order for me to even do a single job of this type!
                    suficientResources = false;
                    break;
                } else {
                    costPerJob += Math.ceil(resources[jobtype].getQValue(i, 0)) * nearestDepo.getResourceCost(i);
                    int numJobs = (int) Math.floor(nearestDepo.getResources()[i].getCurQuantity() / Math.ceil(resources[jobtype].getQValue(i, 0)));
                    if (numJobs < minJobs) {
                        minJobs = numJobs;
                    }
                }
            }
            if (suficientResources == false) {
                continue;// we can't purchase resources for this job type because there aren't enough resources.
            }


            // we can do up to my budget but if there aren't enough resources then can only do minjobs
            int numJobsWithCash = (int) Math.min(Math.floor(budget / costPerJob), minJobs);
            for (int i = 0; i < state.numResourceTypes; i++) {
                int numShouldBuy = numJobsWithCash * (int) Math.ceil(resources[jobtype].getQValue(i, 0));
                bounty -= nearestDepo.buy(i, numShouldBuy);
                myResources[i] += numShouldBuy;
                failedJob = null;// reset failed job because I've now bought resources
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
        } else if (curJob != null) {
            state.printlnSynchronized("Travel confidence = " + getTravelConfidence(curJob.getTask()));
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
