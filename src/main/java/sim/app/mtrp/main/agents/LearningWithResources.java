package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Resource;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;

/**
 * Created by drew on 3/18/17.
 *
 *
 * Ok so this agent will need to learn how to deal with the resource requirements for the jobs
 * as well as the length of time each job lasts.
 *
 * i'm starting to wonder if for this type of agent it would be a good idea to make it so that
 * as soon as the agent arrives to the task that is the bounty they will receive for completing it
 * not the bounty at the end... we will see.
 */
public class LearningWithResources extends LearningAgent {


    QTable jobSuccess[];
    QTable resources[]; // for each element in the array corresponds to the job type and within the job type we must learn the number of resources that are expected of each type
    QTable tTable; // for each type of job we learn the

    double resourceLearningRate = .9;
    double tLearningRate = .75; // set to .1 originally (should be at .9 though...) tried .75
    double tDiscountBeta = .1; // not used...

    public LearningWithResources(MTRP state, int id) {
        super(state, id);
        resources = new QTable[state.numJobTypes];
        for (QTable table : resources) {
            table = new QTable(state.getNumResourceTypes(), 1, resourceLearningRate, 0.0, state.random, state.maxMeanResourcesNeededForType, 0);
        }
        tTable = new QTable(state.numJobTypes, 1, tLearningRate, tDiscountBeta, state.random, state.getJobLength(), 0.0);
        jobSuccess = new QTable[state.numNeighborhoods];
        for (int i = 0; i < jobSuccess.length; i++) {
            jobSuccess[i]  = new QTable(state.numJobTypes, 1, tLearningRate, tDiscountBeta, state.random, 1.0, 0.0);
        }

    }

    double getCost(Task t) {
        // we have a more complicated cost function because we learn the expected number of resources needed
        double resourceCostEstimated = 0.0;
        Depo nearestDepo = getClosestDepo();
        if (nearestDepo == null) {
            nearestDepo = state.depos[0];
        }
        for (int i = 0; i < state.numResourceTypes; i++) {
            resourceCostEstimated += resources[t.getJob().getJobType()].getQValue(i,0) * nearestDepo.getResourceCost(i);
        }

        return getNumTimeStepsFromLocation(t.getLocation()) * getClosestDepo().getFuelCost() + resourceCostEstimated;
    }


    /**
     * Buy resources from the depo passed
     * @param depo
     */
    public void buyResources(Depo depo) {

    }

}
