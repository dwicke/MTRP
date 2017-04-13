package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.util.QTable;
import sim.util.Bag;

/**
 * Simple bounty hunting agent that cooresponds to the SimpleCostJumpship
 * in this scenario we actually have a bit more info and we know how long it will take to get to the task
 * however once we get to the task we don't know how much time it will take to complete.  therefore once
 * we get to the task we have to decide if we are going
 * Created by drew on 3/7/17.
 */
public class LearningAgent extends Agent {

    QTable jobSuccess[];
    QTable tTable; // for each type of job we learn the
    QTable pTable; // ptable probability of getting to the task
    double oneUpdateGamma = .001;
    double tLearningRate = .95; // set to .1 originally (should be at .9 though...) tried .75
    double tDiscountBeta = .1; // not used...
    double jLearningRate = .55;
    double pLearningRate = .75;//.2; // set to .2 originally
    double pDiscountBeta = .1; // not used...
    double epsilonChooseRandomTask =  0.002;
    int numNeighborhoods;

    public LearningAgent(MTRP state, int id) {
        super(state, id);
        numNeighborhoods = state.getNumNeighborhoods();
        pTable = new QTable(numNeighborhoods, 1, pLearningRate, pDiscountBeta, state.random, 1.0, 0.0);
        tTable = new QTable(state.numJobTypes, 1, tLearningRate, tDiscountBeta, state.random, state.getJobLength(), 0.0);
        jobSuccess = new QTable[state.numNeighborhoods];
        for (int i = 0; i < jobSuccess.length; i++) {
            jobSuccess[i]  = new QTable(state.numJobTypes, 1, jLearningRate, tDiscountBeta, state.random, 1.0, 0.0);
        }
    }

    @Override
    public Task getAvailableTask() {


        if (!amWorking && curJob != null && !curJob.getIsAvailable()) {
            // then someone beat me to it so learn
            learn(0.0);
            curJob.getTask().decommit(this);// must decommit.
            // and set curJob to null
            curJob = null;
        }

//        if (amWorking) {
//            // consider letting the agent jump ship here if they need to...
//            return curJob.getTask();
//        }

        // let the agent jumpship if they have not made it to the job yet.
        Task bestT = getBestTask(getTasksWithinRange());

        if (curJob != null && ( bestT == null || bestT.getJob().getId() != curJob.getId())) {
            // then I'm jumping ship and need to decommit and maybe learn too...
            // can't decommit if working on the task!
            // now check if i should change tasks.  I don't want to be swapping tasks if the distance is essentially zero and i was working on it already

            // TODO: consider learning after jumping ship
            if (bestT != null && amWorking == true) { // maybe do this if we were working and not otherwise...???
                //state.printlnSynchronized("Time step = " + state.schedule.getSteps() + " Agent " + getId() + " jumpingship to task id = " + bestT.getJob().getId() + " with utility " + getUtility(bestT) + " from task id " + curJob.getId() + " with utility " + getUtility(curJob.getTask()));
                //jobSuccess[curJob.getTask().getNeighborhood().getId()].update(curJob.getJobType(), 0, 0.5);
            }

            curJob.leaveWork(this);
            amWorking = false;
            curJob.getTask().decommit(this);// must decommit.

        }
        return bestT;
        /*
        if (!amWorking ) {
            Task bestT = getBestTask(getTasksWithinRange());
            if (curJob != null && ( bestT == null || bestT.getJob().getId() != curJob.getId())) {
                // then I'm jumping ship and need to decommit and maybe learn too...
                curJob.getTask().decommit(this);// must decommit.
                // TODO: consider learning after jumping ship
            }
            return bestT;
        } else {
            return curJob.getTask();
        }*/

    }

    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (bagOfTasks.size() == 0 && curJob != null) {
            return curJob.getTask();
        }

        // epsilon random pick task
        if (state.random.nextDouble() < epsilonChooseRandomTask && bagOfTasks.size() > 0) {
            Task randTask = (Task) bagOfTasks.get(state.random.nextInt(bagOfTasks.size()));
            return randTask;
        }

        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        double curMax = 0.0;
        if (curJob != null) {
            chosenTask = curJob.getTask();
            curMax = getUtility(chosenTask);
            if (Double.isInfinite(curMax)) {
                return chosenTask;
            }
        }
        for (Task t : tasks) {

            double value = getUtility(t);
            if (value > curMax) {
                chosenTask = t;
                curMax = value;
            }
        }
        return chosenTask;
    }

    double getUtility(Task t) {
        double confidence = pTable.getQValue(t.getNeighborhood().getId(), 0) * jobSuccess[t.getNeighborhood().getId()].getQValue(t.getJob().getJobType(), 0);
//
//        double timeWorking = 0;
//        if (curJob != null && curJob.getTask().getId() == t.getId()) {
//            timeWorking = getNumTimeStepsWorking();
//        }
//        double numSteps = getNumTimeStepsFromLocation(t.getLocation()) + Math.max(0, tTable.getQValue(t.getJob().getJobType(), 0) - timeWorking);
//        double utility = (confidence *  (t.getBounty() + getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) / numSteps;
//        //state.printlnSynchronized("Time step = " + state.schedule.getSteps() + " Agent " + getId() + " task id = " + t.getId() + " confidence, numsteps, utility " + confidence + ", " + numSteps + ", " + utility);
//        return utility;

        // this seems to work the best!!!!!!!!! for some reason... got to figure this out.
        double util =  ( confidence *  (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) );
        return util;
    }

    double getCost(Task t) {
        // closest depo will never be null because we only consider tasks that are within distance of a depo
        return getNumTimeStepsFromLocation(t.getLocation()) * getClosestDepo(t.getLocation()).getFuelCost();
    }


    @Override
    protected void finishTask() {
        learn(1.0);
        super.finishTask();
    }

    public void learn(double reward) {
        if (reward == 1.0) {
            epsilonChooseRandomTask *= (1.0 - (1.0 / (double) this.numNeighborhoods));
        }

        pTable.update(curJob.getTask().getNeighborhood().getId(), 0, reward);
        //pTable.oneUpdate(oneUpdateGamma);

        jobSuccess[curJob.getTask().getNeighborhood().getId()].update(curJob.getJobType(), 0, reward);
        //jobSuccess[curJob.getTask().getNeighborhood().getId()].oneUpdate(oneUpdateGamma);


        tTable.update(curJob.getJobType(), 0, getNumTimeStepsWorking());

    }

    public String getPTable() {

        return pTable.getQTableAsString();
    }

    @Override
    public String toString() {
        return "Agent id:" + this.id + " curJob: " + curJob + " ptable: " + pTable.getQTableAsString();
    }
}
