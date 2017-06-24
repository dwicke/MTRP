package sim.app.mtrp.main.agents;

import sim.app.mtrp.main.MTRP;
import sim.app.mtrp.main.Task;
import sim.util.Bag;

/**
 * Whoever is closest to the task lays claim does not care about the bounty
 * Created by drew on 3/17/17.
 */
public class NearestFirst extends LearningAgent {
    public NearestFirst(MTRP state, int id) {
        super(state, id);
    }

    @Override
    public double getUtility(Task t) {


        //state.printlnSynchronized("Task " + t.getId() + " cost = " + -getCost(t));
        if (t.getNeighborhood().getId() == getId()) {
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
