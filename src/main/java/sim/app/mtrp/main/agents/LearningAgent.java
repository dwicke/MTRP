package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.Agent;
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

    QTable pTable, tTable; // ptable probability of getting to the task
    double oneUpdateGamma = .001;
    double tLearningRate = .75; // set to .1 originally (should be at .9 though...)
    double tDiscountBeta = .1; // not used...
    double pLearningRate = .2; // set to .2 originally
    double pDiscountBeta = .1; // not used...
    double epsilonChooseRandomTask =  0.002;
    int numNeighborhoods;

    public LearningAgent(MTRP state, int id) {
        super(state, id);
        numNeighborhoods = state.getNumNeighborhoods();
        pTable = new QTable(numNeighborhoods, 1, pLearningRate, pDiscountBeta, state.random, 1.0, 0.0);
        tTable = new QTable(numNeighborhoods, 1, tLearningRate, tDiscountBeta, state.random, 10.0, 0.0);

    }

    @Override
    public Task getAvailableTask() {

        if (!amWorking && curJob != null && !curJob.getIsAvailable()) {
            // then someone beat me to it so learn
            learn(0.0);
        }
        if (!amWorking && (curJob == null || !curJob.getIsAvailable())) {
            return getBestTask(getTasksWithinRange());
        } else {
            return curJob.getTask();
        }

    }

    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0) {
            return null; // need to go for resources.
        }

        // epsilon random pick task
        if (state.random.nextDouble() < epsilonChooseRandomTask) {
            return (Task) bagOfTasks.get(state.random.nextInt(bagOfTasks.size()));
        }

        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        double curMax = 0.0;
        if (curJob != null) {
            chosenTask = curJob.getTask();
            curMax = getUtility(chosenTask);
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
        return (pTable.getQValue(t.getNeighborhood().getId(), 0) * t.getBounty() - getCost(t)) / getNumTimeStepsFromLocation(t.getLocation());
    }

    double getCost(Task t) {
        return getNumTimeStepsFromLocation(t.getLocation()) * getClosestDepo().getFuelCost();
    }


    @Override
    protected void finishTask() {
        learn(1.0);
        super.finishTask();
    }

    public void learn(double reward) {
        epsilonChooseRandomTask *= (1.0 - (1.0 / (double)this.numNeighborhoods));


        pTable.update(curJob.getTask().getNeighborhood().getId(), 0, reward);
        pTable.oneUpdate(oneUpdateGamma);
    }

    public String getPTable() {

        return pTable.getQTableAsString();
    }

    @Override
    public String toString() {
        return "Agent id:" + this.id + " ptable: " + pTable.getQTableAsString();
    }
}
