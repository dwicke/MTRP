package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Created by drew on 4/28/17.
 */
public class LearningAgentNNJ extends LearningAgentWithJumpship {
    public LearningAgentNNJ(MTRP state, int id) {
        super(state, id);
    }


    @Override
    double getUtility(Task t) {

        double bestq = 0.0;
        int bestNeighborhoodID = 0;
        for (int i = 0; i < state.neighborhoods.length; i++) {
            double qval = pTable.getQValue(i, 0);
            if (qval > bestq) {
                bestq = qval;
                bestNeighborhoodID = i;
            }
        }
        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));
        if (t.getNeighborhood().getId() == bestNeighborhoodID) {
            return -getCost(t);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    // need this here because of the fact that utility is negative so am using negative
    public Task getBestTask(Bag bagOfTasks) {
        if (bagOfTasks.size() == 0 && curJob == null) {
            return null; // need to go for resources.
        } else if (bagOfTasks.size() == 0 && curJob != null) {
            return curJob.getTask();
        }

        // epsilon random pick task
        /*if (state.random.nextDouble() < epsilonChooseRandomTask && bagOfTasks.size() > 0) {
            Task randTask = (Task) bagOfTasks.get(state.random.nextInt(bagOfTasks.size()));
            return randTask;
        }*/

        // otherwise just pick it using real method
        Task[] tasks = (Task[]) bagOfTasks.toArray(new Task[bagOfTasks.size()]);
        Task chosenTask = null;
        double curMax = Double.NEGATIVE_INFINITY;
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
}
