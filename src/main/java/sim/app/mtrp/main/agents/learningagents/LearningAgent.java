package sim.app.mtrp.main.agents.learningagents;

import sim.app.mtrp.main.Agent;
import sim.app.mtrp.main.Depo;
import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.app.mtrp.main.agents.comparisonagents.FirstComeFirstServe;
import sim.app.mtrp.main.agents.comparisonagents.NearestFirst;
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

    public QTable tTable; // for each type of job we learn the
    public QTable pTable; // ptable probability of getting to the task
    public double oneUpdateGamma = .001; // .001
    double tLearningRate = .05; // set to .5 originally (should be at .95 though...) tried .75
    double tDiscountBeta = .1; // not used...
    double jLearningRate = .55;
    double pLearningRate = .75;//0.99;  //.75 is what i used to use... but .99 makes more sens
    double pDiscountBeta = .1; // not used...
    double epsilonChooseRandomTask =  .002; // was .002
    int numNeighborhoods;

    public QTable expectedNeighborhoodReward;
    double neighRewardLR = .45;



    public LearningAgent(MTRP state, int id) {
        super(state, id);
        numNeighborhoods = state.getNumNeighborhoods();
        pTable = new QTable(numNeighborhoods, 1, pLearningRate, pDiscountBeta, state.random, 1.0, 0.0);
        tTable = new QTable(state.numJobTypes + state.numEmergentJobTypes, 1, tLearningRate, tDiscountBeta, state.random, state.getJobLength(), 0.0);
        expectedNeighborhoodReward = new QTable(state.getNumNeighborhoods(), 1, neighRewardLR, .1, state.random);

    }

    public LearningAgent() {

    }

    static int beatCounter = 0;
    static int getBestCounter = 0;

    public Task getAvailableTask(Bag tasks) {

        if (!amWorking && curJob != null && !curJob.getIsAvailable()) {
            //state.printlnSynchronized("Agent " + getId() + " has been beat!" + " total = " + beatCounter++);
            // then someone beat me to it so learn
            learn(0.0);
            curJob.getTask().decommit(this);// must decommit.
            // and set curJob to null
            curJob = null;
        }


        if (curJob == null) {
            //state.printlnSynchronized("Agent " + getId() + " getting task = " + getBestCounter++);
            Task bestT = getBestTask(tasks);

            return bestT;
        }else {
            return curJob.getTask();
        }

    }

    @Override
    public Task getAvailableTask() {
        return getAvailableTask(getTasksWithinRange(state.getBondsman().getAvailableTasks()));
        //return getAvailableTask(getTasksWithinRange(state.getBondsman().getNewTasks()));
    }

    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (bagOfTasks.size() == 0 && curJob != null) {
            return curJob.getTask();
        }

        // epsilon random pick task
//        if (state.random.nextDouble() < epsilonChooseRandomTask && bagOfTasks.size() > 0) {
//            Task randTask = (Task) bagOfTasks.get(state.random.nextInt(bagOfTasks.size()));
//            return randTask;
//        }

        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        double curMax = 0.0;

        // so what if instead we actually had the cost to get to the depo...
//        Depo closestDepo = getClosestDepo();
//        if (closestDepo != null) {
//            curMax = -closestDepo.getFuelCost();
//        }


        if (curJob != null) {
            chosenTask = curJob.getTask();
            curMax = getUtility(chosenTask);
            if (Double.isInfinite(curMax)) {
                return chosenTask;
            }
        }
        for (Task t : tasks) {

            double value = getUtility(t);
            //state.printlnSynchronized("agent id = " + id + " value for task i " + t.getId() + " is = " + value);
            if (value > curMax) {
                chosenTask = t;
                curMax = value;
            }
        }
        if (chosenTask == null) {
           // state.printlnSynchronized("agent id = " + id + " decided none of that tasks were worth going after...");
        }

        return chosenTask;
    }

    public double getUtility(Task t) {
        double confidence = 1;//pTable.getQValue(t.getNeighborhood().getId(), 0) /* * jobSuccess[t.getNeighborhood().getId()].getQValue(t.getJob().getJobType(), 0)*/;
//
//        double timeWorking = 0;
//        if (curJob != null && curJob.getTask().getId() == t.getId()) {
//            timeWorking = getNumTimeStepsWorking();
//        }
//        double numSteps = getNumTimeStepsFromLocation(t.getLocation()) + Math.max(0, tTable.getQValue(t.getJob().getJobType(), 0) - timeWorking);
//        double utility = (confidence *  (t.getBounty() + getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) / numSteps;
//        //state.printlnSynchronized("Time step = " + state.schedule.getSteps() + " Agent " + getId() + " task id = " + t.getId() + " confidence, numsteps, utility " + confidence + ", " + numSteps + ", " + utility);
//        return utility;

        double totalTime = getNumTimeStepsFromLocation(t.getLocation()) +  tTable.getQValue(t.getJob().getJobType(), 0);
        //double totalTime = t.getLocation().distance(curLocation) + tTable.getQValue(t.getJob().getJobType(), 0);
        //state.printlnSynchronized("Time = " + tTable.getQValue(t.getJob().getJobType(), 0));

        //double util =  confidence * ((t.getBounty() / totalTime) + t.getJob().getBountyRate() - (getCost(t) / totalTime));
        double util =  confidence * ((t.getBounty() / totalTime) + t.getJob().getBountyRate() - (getCost(t) / totalTime)  + (expectedNeighborhoodReward.getQValue(t.getNeighborhood().getId(), 0) / totalTime));

        // this seems to work the best!!!!!!!!! for some reason... got to figure this out.
        //double util =  ( confidence *  (t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));
        //double util =  ( confidence *  (-getCost(t) + t.getBounty()+ (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0)) * state.getIncrement() - 0)) /  (getNumTimeStepsFromLocation(t.getLocation()) + tTable.getQValue(t.getJob().getJobType(), 0));
        //double util =  ( confidence *  (t.getBounty()+ getNumTimeStepsFromLocation(t.getLocation()) - getCost(t))) /  (getNumTimeStepsFromLocation(t.getLocation()) );
        return util;
    }

    public double getCost(Task t) {
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
        pTable.oneUpdate(oneUpdateGamma);

        //jobSuccess[curJob.getTask().getNeighborhood().getId()].update(curJob.getJobType(), 0, reward);
        //jobSuccess[curJob.getTask().getNeighborhood().getId()].oneUpdate(oneUpdateGamma);


        tTable.update(curJob.getJobType(), 0, getNumTimeStepsWorking());

        // need to learn the expected reward for completing a task in the neighborhood
        expectedNeighborhoodReward.update(curJob.getTask().getNeighborhood().getId(), 0, curJob.getTask().getNeighborhood().getNeighborhoodBounty());

        //tTable.update(0, 0, getNumTimeStepsWorking());

    }

    public String getPTable() {

        return pTable.getQTableAsString();
    }



//    @Override
//    public String toString() {
//        return "Agent id:" + this.id + " curJob: " + curJob + " ptable: " + pTable.getQTableAsString();
//    }
}
